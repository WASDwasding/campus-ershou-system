package com.campus.ershou.goods;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.campus.CategoryMapper;
import com.campus.ershou.common.campus.CategoryView;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.common.file.FileUrls;
import com.campus.ershou.common.util.ContactInfo;
import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.goods.dto.GoodsWriteRequest;
import com.campus.ershou.order.OrderRecord;
import com.campus.ershou.order.OrderService;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.campus.ershou.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    private static final Logger log = LoggerFactory.getLogger(GoodsService.class);
    private static final Set<String> CONDITIONS = Set.of("NEW", "LIKE_NEW", "GOOD", "FAIR", "POOR");
    private static final Set<String> SORTS = Set.of("latest", "price_asc", "price_desc", "nearby");
    private static final Set<String> PUBLIC_STATUS = Set.of("ON_SALE", "RESERVED", "SOLD");
    private static final Set<String> MINE_STATUS = Set.of("ON_SALE", "RESERVED", "SOLD", "OFF_SHELF", "REMOVED");
    private static final ZoneOffset SHANGHAI = ZoneOffset.ofHours(8);

    private final GoodsMapper goodsMapper;
    private final CategoryMapper categoryMapper;
    private final BuildingMapper buildingMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final FileService fileService;
    private final FileUrls fileUrls;
    private final StringRedisTemplate redis;
    private final OrderService orderService;

    public GoodsService(
            GoodsMapper goodsMapper,
            CategoryMapper categoryMapper,
            BuildingMapper buildingMapper,
            UserMapper userMapper,
            UserService userService,
            FileService fileService,
            FileUrls fileUrls,
            StringRedisTemplate redis,
            OrderService orderService
    ) {
        this.goodsMapper = goodsMapper;
        this.categoryMapper = categoryMapper;
        this.buildingMapper = buildingMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.fileService = fileService;
        this.fileUrls = fileUrls;
        this.redis = redis;
        this.orderService = orderService;
    }

    public Map<String, Object> list(long viewerId, GoodsListQuery query) {
        UserEntity viewer = userService.requireUser(viewerId);
        if (query.getKeyword() != null) {
            String raw = query.getKeyword().trim();
            if (raw.isEmpty()) {
                query.setKeyword(null);
            } else if (raw.length() > 30) {
                throw ApiException.validation(List.of(new FieldErrorItem("keyword", "关键词长度为 1–30")));
            } else {
                query.setKeyword(like(raw));
            }
        }
        if (query.getCondition() != null && !query.getCondition().isBlank()) {
            query.setCondition(query.getCondition().trim().toUpperCase());
        } else {
            query.setCondition(null);
        }
        prepareSort(query, viewer);
        List<GoodsRow> rows = goodsMapper.listOnSale(query);
        long total = goodsMapper.countOnSale(query);
        return page(rows, query.getOffset(), query.getLimit(), total, viewer, false);
    }

    public Map<String, Object> detail(long viewerId, long goodsId) {
        UserEntity viewer = userService.requireUser(viewerId);
        GoodsRow goods = goodsMapper.findById(goodsId);
        if (!canSee(goods, viewerId)) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (!viewerIdEquals(goods, viewerId)) {
            recordView(goodsId, viewerId);
            goods = goodsMapper.findById(goodsId);
        }
        rememberHistory(viewerId, goodsId);
        return toDetail(goods, viewer);
    }

    @Transactional
    public Map<String, Object> create(long sellerId, GoodsWriteRequest request) {
        UserEntity seller = requireVerified(sellerId);
        CheckedGoods checked = check(sellerId, request);
        GoodsRow goods = new GoodsRow();
        goods.setSellerId(sellerId);
        apply(goods, checked);
        goods.setStatus("ON_SALE");
        goods.setStock(1);
        goods.setVersion(0);
        goodsMapper.insert(goods);
        saveImages(goods.getId(), checked.fileIds());
        return toDetail(goodsMapper.findById(goods.getId()), seller);
    }

    @Transactional
    public Map<String, Object> update(long sellerId, long goodsId, GoodsWriteRequest request) {
        UserEntity seller = requireVerified(sellerId);
        GoodsRow existing = requireSellerGoods(sellerId, goodsId);
        if (!"ON_SALE".equals(existing.getStatus()) && !"OFF_SHELF".equals(existing.getStatus())) {
            throw ApiException.of(ErrorCode.GOODS_NOT_EDITABLE);
        }
        CheckedGoods checked = check(sellerId, request);
        if (!existing.getPrice().equals(checked.price())) {
            log.info("GOODS_PRICE_CHANGED goodsId={} from={} to={}", goodsId, existing.getPrice(), checked.price());
        }
        apply(existing, checked);
        if (goodsMapper.updateContent(existing) != 1) {
            throw ApiException.of(ErrorCode.GOODS_NOT_EDITABLE);
        }
        goodsMapper.deleteImages(goodsId);
        saveImages(goodsId, checked.fileIds());
        return toDetail(goodsMapper.findById(goodsId), seller);
    }

    @Transactional
    public Map<String, Object> changeShelf(long sellerId, long goodsId, boolean onShelf) {
        if (onShelf) {
            requireVerified(sellerId);
        } else {
            userService.requireUser(sellerId);
        }
        GoodsRow goods = requireSellerGoods(sellerId, goodsId);
        String from = onShelf ? "OFF_SHELF" : "ON_SALE";
        String to = onShelf ? "ON_SALE" : "OFF_SHELF";
        if (!from.equals(goods.getStatus())) {
            throw ApiException.of(ErrorCode.GOODS_NOT_EDITABLE);
        }
        if (goodsMapper.changeStatus(goodsId, sellerId, from, to) != 1) {
            throw ApiException.of(ErrorCode.GOODS_NOT_EDITABLE);
        }
        return Map.of("id", goodsId, "status", to);
    }

    @Transactional
    public Map<String, Object> delete(long sellerId, long goodsId) {
        userService.requireUser(sellerId);
        GoodsRow goods = requireSellerGoods(sellerId, goodsId);
        if ("RESERVED".equals(goods.getStatus()) || "DELETED".equals(goods.getStatus())) {
            throw ApiException.of(ErrorCode.GOODS_NOT_EDITABLE);
        }
        if (goodsMapper.changeStatus(goodsId, sellerId, goods.getStatus(), "DELETED") != 1) {
            throw ApiException.of(ErrorCode.GOODS_NOT_EDITABLE);
        }
        return Map.of("id", goodsId, "status", "DELETED");
    }

    public Map<String, Object> mine(long sellerId, String status, int page, int size) {
        userService.requireUser(sellerId);
        String normalized = normalizeMineStatus(status);
        int offset = (page - 1) * size;
        List<GoodsRow> rows = goodsMapper.listMine(sellerId, normalized, size, offset);
        long total = goodsMapper.countMine(sellerId, normalized);
        return page(rows, offset, size, total, userService.requireUser(sellerId), true);
    }

    public Map<String, Object> bySeller(long viewerId, long sellerId, String status, int page, int size) {
        UserEntity viewer = userService.requireUser(viewerId);
        if (userMapper.findById(sellerId) == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        String normalized = normalizePublicStatus(status);
        int offset = (page - 1) * size;
        List<GoodsRow> rows = goodsMapper.listBySellerStatus(sellerId, normalized, size, offset);
        long total = goodsMapper.countBySellerStatus(sellerId, normalized);
        return page(rows, offset, size, total, viewer, false);
    }

    @Transactional
    public Map<String, Object> favorite(long userId, long goodsId, boolean add) {
        UserEntity viewer = userService.requireUser(userId);
        GoodsRow goods = goodsMapper.findById(goodsId);
        if (!canSee(goods, userId)) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (add) {
            try {
                goodsMapper.insertFavorite(userId, goodsId);
                goodsMapper.addFavoriteCount(goodsId, 1);
            } catch (DuplicateKeyException ignored) {
                // 重复收藏保持原样
            }
        } else if (goodsMapper.deleteFavorite(userId, goodsId) == 1) {
            goodsMapper.addFavoriteCount(goodsId, -1);
        }
        goods = goodsMapper.findById(goodsId);
        return toCard(goods, viewer, imagesOf(List.of(goodsId)), false);
    }

    public Map<String, Object> favorites(long userId, int page, int size) {
        UserEntity viewer = userService.requireUser(userId);
        int offset = (page - 1) * size;
        List<Long> ids = goodsMapper.listFavoriteIds(userId, size, offset);
        long total = goodsMapper.countVisibleFavorites(userId);
        List<GoodsRow> rows = ordered(ids);
        return page(rows, offset, size, total, viewer, false);
    }

    public Map<String, Object> history(long userId, int page, int size) {
        UserEntity viewer = userService.requireUser(userId);
        List<String> raw = redis.opsForList().range(RedisKeys.history(userId), 0, -1);
        List<Long> ids = new ArrayList<>();
        if (raw != null) {
            for (String item : raw) {
                try {
                    ids.add(Long.parseLong(item));
                } catch (NumberFormatException ignored) {
                    // 跳过损坏记录
                }
            }
        }
        List<GoodsRow> visible = ordered(ids).stream().filter(row -> canSee(row, userId)).toList();
        int from = Math.min((page - 1) * size, visible.size());
        int to = Math.min(from + size, visible.size());
        return page(visible.subList(from, to), from, size, visible.size(), viewer, false);
    }

    public void clearHistory(long userId) {
        userService.requireUser(userId);
        redis.delete(RedisKeys.history(userId));
    }

    public long countStatus(long sellerId, String status) {
        return goodsMapper.countBySellerStatus(sellerId, status);
    }

    public long countFavorites(long userId) {
        return goodsMapper.countFavorites(userId);
    }

    private static String like(String raw) {
        return "%" + raw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }

    public static int[] page(Integer page, Integer size) {
        int p = page == null ? 1 : page;
        int s = size == null ? 20 : size;
        if (p < 1) {
            throw ApiException.validation(List.of(new FieldErrorItem("page", "页码从 1 开始")));
        }
        if (s < 1 || s > 50) {
            throw ApiException.validation(List.of(new FieldErrorItem("size", "每页 1–50 条")));
        }
        return new int[]{p, s};
    }

    private void prepareSort(GoodsListQuery query, UserEntity viewer) {
        String sort = query.getSort() == null || query.getSort().isBlank() ? "latest" : query.getSort().trim();
        if (!SORTS.contains(sort)) {
            throw ApiException.validation(List.of(new FieldErrorItem("sort", "排序方式不合法")));
        }
        if ("nearby".equals(sort)) {
            if (viewer.getDormBuildingId() == null) {
                sort = "latest";
            } else {
                BuildingView dorm = buildingMapper.findEnabledById(viewer.getDormBuildingId());
                if (dorm == null) {
                    sort = "latest";
                } else {
                    query.setDormBuildingId(dorm.getId());
                    query.setDormAreaId(dorm.getAreaId());
                }
            }
        }
        query.setSort(sort);
        if (query.getCondition() != null && !CONDITIONS.contains(query.getCondition())) {
            throw ApiException.validation(List.of(new FieldErrorItem("condition", "成色不合法")));
        }
    }

    private CheckedGoods check(long sellerId, GoodsWriteRequest request) {
        if (ContactInfo.contains(request.title()) || ContactInfo.contains(request.description()) || ContactInfo.contains(request.tradeNote())) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
        if (!CONDITIONS.contains(request.condition())) {
            throw ApiException.validation(List.of(new FieldErrorItem("condition", "成色不合法")));
        }
        if (categoryMapper.findEnabledById(request.categoryId()) == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("categoryId", "分类不存在或已停用")));
        }
        BuildingView meetup = buildingMapper.findEnabledById(request.meetupBuildingId());
        if (meetup == null || !Boolean.TRUE.equals(meetup.getMeetupPoint())) {
            throw ApiException.validation(List.of(new FieldErrorItem("meetupBuildingId", "面交楼栋不可用")));
        }
        List<String> fileIds = request.imageFileIds() == null ? List.of() : request.imageFileIds();
        if (fileIds.isEmpty() || fileIds.size() > 9 || fileIds.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw ApiException.validation(List.of(new FieldErrorItem("imageFileIds", "商品图片为 1–9 张")));
        }
        if (new HashSet<>(fileIds).size() != fileIds.size()) {
            throw ApiException.validation(List.of(new FieldErrorItem("imageFileIds", "图片不能重复")));
        }
        for (String fileId : fileIds) {
            fileService.requireUsable(fileId.trim(), sellerId, "GOODS");
        }
        return new CheckedGoods(
                request.title(),
                request.description(),
                request.categoryId(),
                request.condition(),
                request.price(),
                request.originalPrice(),
                request.meetupBuildingId(),
                request.tradeNote(),
                fileIds.stream().map(String::trim).toList()
        );
    }

    private static void apply(GoodsRow goods, CheckedGoods checked) {
        goods.setTitle(checked.title());
        goods.setDescription(checked.description());
        goods.setCategoryId(checked.categoryId());
        goods.setConditionCode(checked.condition());
        goods.setPrice(checked.price());
        goods.setOriginalPrice(checked.originalPrice());
        goods.setMeetupBuildingId(checked.meetupBuildingId());
        goods.setTradeNote(checked.tradeNote());
    }

    private void saveImages(long goodsId, List<String> fileIds) {
        for (int i = 0; i < fileIds.size(); i++) {
            GoodsImageRow image = new GoodsImageRow();
            image.setGoodsId(goodsId);
            image.setFileId(fileIds.get(i));
            image.setSort(i);
            goodsMapper.insertImage(image);
        }
    }

    private UserEntity requireVerified(long userId) {
        UserEntity user = userService.requireUser(userId);
        if (!"VERIFIED".equals(user.getVerifyStatus())) {
            throw ApiException.of(ErrorCode.NOT_VERIFIED);
        }
        return user;
    }

    private GoodsRow requireSellerGoods(long sellerId, long goodsId) {
        GoodsRow goods = goodsMapper.findById(goodsId);
        if (goods == null || "DELETED".equals(goods.getStatus())) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (!sellerIdEquals(goods, sellerId)) {
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        return goods;
    }

    private static String normalizeMineStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String normalized = status.trim().toUpperCase();
        if (!MINE_STATUS.contains(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("status", "商品状态不合法")));
        }
        return normalized;
    }

    private static String normalizePublicStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ON_SALE";
        }
        String normalized = status.trim().toUpperCase();
        if (!"ON_SALE".equals(normalized) && !"SOLD".equals(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("status", "只能查看在售或已售")));
        }
        return normalized;
    }

    private boolean canSee(GoodsRow goods, long viewerId) {
        if (goods == null || "DELETED".equals(goods.getStatus())) {
            return false;
        }
        if (sellerIdEquals(goods, viewerId)) {
            return true;
        }
        return PUBLIC_STATUS.contains(goods.getStatus());
    }

    private void recordView(long goodsId, long userId) {
        String key = RedisKeys.goodsDailyViews(goodsId, LocalDate.now(SHANGHAI).toString());
        Long added = redis.opsForSet().add(key, String.valueOf(userId));
        if (added != null && added == 1L) {
            redis.expire(key, Duration.ofDays(2));
            goodsMapper.incrementView(goodsId);
        }
    }

    private void rememberHistory(long userId, long goodsId) {
        String key = RedisKeys.history(userId);
        String value = String.valueOf(goodsId);
        redis.opsForList().remove(key, 0, value);
        redis.opsForList().leftPush(key, value);
        redis.opsForList().trim(key, 0, 99);
        redis.expire(key, Duration.ofDays(30));
    }

    private Map<String, Object> page(List<GoodsRow> rows, int offset, int size, long total, UserEntity viewer, boolean mine) {
        int page = size == 0 ? 1 : offset / size + 1;
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", cards(rows, viewer, mine));
        data.put("page", page);
        data.put("size", size);
        data.put("total", total);
        data.put("hasMore", (long) offset + rows.size() < total);
        return data;
    }

    private List<Map<String, Object>> cards(List<GoodsRow> rows, UserEntity viewer, boolean mine) {
        if (rows.isEmpty()) {
            return List.of();
        }
        Map<Long, List<GoodsImageRow>> images = imagesOf(rows.stream().map(GoodsRow::getId).toList());
        List<Map<String, Object>> list = new ArrayList<>();
        for (GoodsRow row : rows) {
            list.add(toCard(row, viewer, images, mine));
        }
        return list;
    }

    private Map<String, Object> toCard(GoodsRow goods, UserEntity viewer, Map<Long, List<GoodsImageRow>> images, boolean mine) {
        Catalog catalog = catalog(List.of(goods), viewer.getId());
        Map<String, Object> data = base(goods, catalog, images.getOrDefault(goods.getId(), List.of()));
        data.put("seller", seller(catalog.users.get(goods.getSellerId()), viewer, false));
        if (mine && "REMOVED".equals(goods.getStatus())) {
            data.put("removedReason", goods.getRemovedReason());
        }
        return data;
    }

    private Map<String, Object> toDetail(GoodsRow goods, UserEntity viewer) {
        Catalog catalog = catalog(List.of(goods), viewer.getId());
        List<GoodsImageRow> images = goodsMapper.listImages(List.of(goods.getId()));
        Map<String, Object> data = base(goods, catalog, images);
        data.put("description", goods.getDescription());
        data.put("tradeNote", goods.getTradeNote());
        data.put("images", imageList(images));
        data.put("viewCount", goods.getViewCount() == null ? 0 : goods.getViewCount());
        data.put("seller", seller(catalog.users.get(goods.getSellerId()), viewer, true));
        boolean mine = sellerIdEquals(goods, viewer.getId());
        boolean verified = "VERIFIED".equals(viewer.getVerifyStatus());
        boolean onSale = "ON_SALE".equals(goods.getStatus());
        Map<String, Object> viewerView = new LinkedHashMap<>();
        viewerView.put("isMine", mine);
        viewerView.put("favorited", catalog.favorited.contains(goods.getId()));
        viewerView.put("canBuy", onSale && !mine && verified);
        viewerView.put("canChat", onSale && !mine && verified);
        OrderRecord active = orderService.findActive(goods.getId());
        viewerView.put("activeOrderId", active != null && active.getBuyerId() != null && active.getBuyerId().equals(viewer.getId())
                ? active.getId() : null);
        data.put("viewer", viewerView);
        if (mine && "REMOVED".equals(goods.getStatus())) {
            data.put("removedReason", goods.getRemovedReason());
        }
        data.put("updatedAt", offset(goods.getUpdatedAt()));
        return data;
    }

    private Map<String, Object> base(GoodsRow goods, Catalog catalog, List<GoodsImageRow> images) {
        String cover = images.isEmpty() ? null : fileUrls.publicUrl(images.get(0).getFileId());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", goods.getId());
        data.put("title", goods.getTitle());
        data.put("coverUrl", cover);
        data.put("price", goods.getPrice());
        data.put("originalPrice", goods.getOriginalPrice());
        data.put("condition", goods.getConditionCode());
        data.put("status", goods.getStatus());
        data.put("category", category(catalog.categories.get(goods.getCategoryId())));
        data.put("meetupBuilding", building(catalog.buildings.get(goods.getMeetupBuildingId())));
        data.put("favoriteCount", goods.getFavoriteCount() == null ? 0 : goods.getFavoriteCount());
        data.put("createdAt", offset(goods.getCreatedAt()));
        return data;
    }

    private Catalog catalog(List<GoodsRow> rows, long viewerId) {
        Set<Long> categoryIds = rows.stream().map(GoodsRow::getCategoryId).collect(Collectors.toSet());
        Set<Long> buildingIds = rows.stream().map(GoodsRow::getMeetupBuildingId).collect(Collectors.toSet());
        Set<Long> sellerIds = rows.stream().map(GoodsRow::getSellerId).collect(Collectors.toSet());
        Set<Long> goodsIds = rows.stream().map(GoodsRow::getId).collect(Collectors.toSet());
        Catalog catalog = new Catalog();
        if (!categoryIds.isEmpty()) {
            catalog.categories = categoryMapper.findByIds(categoryIds).stream()
                    .collect(Collectors.toMap(CategoryView::getId, Function.identity()));
        }
        if (!buildingIds.isEmpty()) {
            catalog.buildings = buildingMapper.findByIds(buildingIds).stream()
                    .collect(Collectors.toMap(BuildingView::getId, Function.identity()));
        }
        if (!sellerIds.isEmpty()) {
            catalog.users = userMapper.findByIds(sellerIds).stream()
                    .collect(Collectors.toMap(UserEntity::getId, Function.identity()));
        }
        if (!goodsIds.isEmpty()) {
            catalog.favorited = new HashSet<>(goodsMapper.favoritedIds(viewerId, goodsIds));
        }
        return catalog;
    }

    private Map<Long, List<GoodsImageRow>> imagesOf(List<Long> goodsIds) {
        if (goodsIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<GoodsImageRow>> grouped = new LinkedHashMap<>();
        for (GoodsImageRow image : goodsMapper.listImages(goodsIds)) {
            grouped.computeIfAbsent(image.getGoodsId(), id -> new ArrayList<>()).add(image);
        }
        return grouped;
    }

    private List<Map<String, Object>> imageList(List<GoodsImageRow> images) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (GoodsImageRow image : images) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fileId", image.getFileId());
            item.put("url", fileUrls.publicUrl(image.getFileId()));
            item.put("thumbUrl", fileUrls.thumbUrl(image.getFileId()));
            list.add(item);
        }
        return list;
    }

    private Map<String, Object> seller(UserEntity user, UserEntity viewer, boolean withRating) {
        Map<String, Object> data = new LinkedHashMap<>();
        if (user == null) {
            return data;
        }
        data.put("id", user.getId());
        data.put("nickname", user.getNickname());
        data.put("avatarUrl", fileService.publicUrlIfOwned(user.getAvatarFileId()));
        data.put("verified", "VERIFIED".equals(user.getVerifyStatus()));
        data.put("relation", relation(viewer, user));
        if (withRating) {
            data.put("rating", orderService.rating(user.getId()));
        }
        return data;
    }

    private Map<String, Boolean> relation(UserEntity viewer, UserEntity target) {
        boolean sameBuilding = viewer.getDormBuildingId() != null
                && viewer.getDormBuildingId().equals(target.getDormBuildingId());
        boolean sameArea = false;
        if (viewer.getDormBuildingId() != null && target.getDormBuildingId() != null) {
            BuildingView mine = buildingMapper.findEnabledById(viewer.getDormBuildingId());
            BuildingView theirs = buildingMapper.findEnabledById(target.getDormBuildingId());
            sameArea = mine != null && theirs != null && mine.getAreaId() != null && mine.getAreaId().equals(theirs.getAreaId());
        }
        return Map.of("sameBuilding", sameBuilding, "sameArea", sameArea || sameBuilding);
    }

    private static Map<String, Object> category(CategoryView category) {
        if (category == null) {
            return null;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", category.getId());
        data.put("name", category.getName());
        data.put("icon", category.getIcon());
        return data;
    }

    private static Map<String, Object> building(BuildingView building) {
        if (building == null) {
            return null;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", building.getId());
        data.put("name", building.getName());
        data.put("areaId", building.getAreaId());
        data.put("areaName", building.getAreaName());
        return data;
    }

    private List<GoodsRow> ordered(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<Long, GoodsRow> found = goodsMapper.findByIds(ids).stream()
                .collect(Collectors.toMap(GoodsRow::getId, Function.identity()));
        List<GoodsRow> rows = new ArrayList<>();
        for (Long id : ids) {
            GoodsRow row = found.get(id);
            if (row != null) {
                rows.add(row);
            }
        }
        return rows;
    }

    private static boolean sellerIdEquals(GoodsRow goods, long userId) {
        return goods.getSellerId() != null && goods.getSellerId() == userId;
    }

    private static boolean viewerIdEquals(GoodsRow goods, long userId) {
        return sellerIdEquals(goods, userId);
    }

    private static Object offset(java.time.LocalDateTime time) {
        return time == null ? null : time.atOffset(SHANGHAI);
    }

    private record CheckedGoods(
            String title,
            String description,
            Long categoryId,
            String condition,
            Integer price,
            Integer originalPrice,
            Long meetupBuildingId,
            String tradeNote,
            List<String> fileIds
    ) {
    }

    private static final class Catalog {
        private Map<Long, CategoryView> categories = Map.of();
        private Map<Long, BuildingView> buildings = Map.of();
        private Map<Long, UserEntity> users = Map.of();
        private Set<Long> favorited = Set.of();
    }
}
