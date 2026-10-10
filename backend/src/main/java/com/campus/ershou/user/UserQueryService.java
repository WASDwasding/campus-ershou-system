package com.campus.ershou.user;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.goods.GoodsService;
import com.campus.ershou.im.ConversationService;
import com.campus.ershou.im.NotificationService;
import com.campus.ershou.order.OrderService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserQueryService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final BuildingMapper buildingMapper;
    private final FileService fileService;
    private final GoodsService goodsService;
    private final NotificationService notificationService;
    private final OrderService orderService;
    private final ConversationService conversationService;

    public UserQueryService(
            UserMapper userMapper,
            UserService userService,
            BuildingMapper buildingMapper,
            FileService fileService,
            GoodsService goodsService,
            NotificationService notificationService,
            OrderService orderService,
            ConversationService conversationService
    ) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.buildingMapper = buildingMapper;
        this.fileService = fileService;
        this.goodsService = goodsService;
        this.notificationService = notificationService;
        this.orderService = orderService;
        this.conversationService = conversationService;
    }

    public Map<String, Object> stats(long userId) {
        userService.requireUser(userId);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("onSaleCount", goodsService.countStatus(userId, "ON_SALE"));
        data.put("offShelfCount", goodsService.countStatus(userId, "OFF_SHELF"));
        data.put("soldCount", goodsService.countStatus(userId, "SOLD"));
        data.put("favoriteCount", goodsService.countFavorites(userId));
        data.put("pendingOrderCount", orderService.countBuyerPending(userId));
        data.put("sellingOrderCount", orderService.countSellerActive(userId));
        return data;
    }

    public Map<String, Object> badges(long userId) {
        userService.requireUser(userId);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("unreadMessages", conversationService.countUnread(userId));
        data.put("unreadNotifications", notificationService.countUnread(userId));
        data.put("buyerTodos", orderService.countBuyerTodos(userId));
        return data;
    }

    public Map<String, Object> publicProfile(long viewerId, long userId) {
        UserEntity viewer = userService.requireUser(viewerId);
        UserEntity target = userMapper.findById(userId);
        if (target == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", target.getId());
        data.put("nickname", target.getNickname());
        data.put("avatarUrl", fileService.publicUrlIfOwned(target.getAvatarFileId()));
        data.put("bio", target.getBio());
        data.put("verified", "VERIFIED".equals(target.getVerifyStatus()));
        data.put("relation", relation(viewer, target));
        data.put("rating", orderService.rating(userId));
        data.put("soldCount", goodsService.countStatus(userId, "SOLD"));
        data.put("onSaleCount", goodsService.countStatus(userId, "ON_SALE"));
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
}
