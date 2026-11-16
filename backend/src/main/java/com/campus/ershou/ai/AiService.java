package com.campus.ershou.ai;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.campus.CategoryMapper;
import com.campus.ershou.common.campus.CategoryView;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.util.ContactInfo;
import com.campus.ershou.common.util.Randoms;
import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.goods.GoodsImageRow;
import com.campus.ershou.goods.GoodsMapper;
import com.campus.ershou.goods.GoodsRow;
import com.campus.ershou.goods.GoodsService;
import com.campus.ershou.user.UserService;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class AiService {

    public static final String RULE_SCRIPT = "建议价已按成色区间给出，成交以商品标价为准。";

    private static final Logger log = LoggerFactory.getLogger(AiService.class);
    private static final Duration TTL = Duration.ofSeconds(600);
    private static final int TTL_SECONDS = 600;
    private static final int DRAFT_TOKENS = 700;
    private static final int QUOTE_TOKENS = 700;
    private static final Pattern TOKEN = Pattern.compile("[0-9a-f]{16,32}");
    private static final Set<String> CONDITIONS = Set.of("NEW", "LIKE_NEW", "GOOD", "FAIR", "POOR");
    private static final String DRAFT_SYSTEM = "你是校园二手平台的发布助手。只输出一个 JSON 对象，不要 Markdown。无法从图片判断的字段用 null。不要编造电话、微信、QQ 或邮箱。";
    private static final String QUOTE_SYSTEM = "你是校园二手平台的定价助手。只输出一个 JSON 对象，不要 Markdown。"
            + "结合商品文案和图片给出参考价，并说明依据。不要编造看不见的信息，不要写议价话术，不要包含电话、微信、QQ 或邮箱。"
            + "成交以商品标价为准。";

    private final UserService userService;
    private final GoodsService goodsService;
    private final GoodsMapper goodsMapper;
    private final FileService fileService;
    private final CategoryMapper categoryMapper;
    private final RateLimitService rateLimitService;
    private final AuditService auditService;
    private final AiCompletionClient client;
    private final AppProperties props;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public AiService(
            UserService userService,
            GoodsService goodsService,
            GoodsMapper goodsMapper,
            FileService fileService,
            CategoryMapper categoryMapper,
            RateLimitService rateLimitService,
            AuditService auditService,
            AiCompletionClient client,
            AppProperties props,
            StringRedisTemplate redis,
            ObjectMapper objectMapper
    ) {
        this.userService = userService;
        this.goodsService = goodsService;
        this.goodsMapper = goodsMapper;
        this.fileService = fileService;
        this.categoryMapper = categoryMapper;
        this.rateLimitService = rateLimitService;
        this.auditService = auditService;
        this.client = client;
        this.props = props;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> draft(long userId, List<String> imageFileIds, String prompt) {
        if (!"VERIFIED".equals(userService.requireUser(userId).getVerifyStatus())) {
            throw ApiException.of(ErrorCode.NOT_VERIFIED);
        }
        checkPrompt(prompt);
        List<String> ids = normalizeImages(userId, imageFileIds);
        consumeDaily("draft", userId);
        Map<Long, CategoryView> categories = categories();
        List<AiPart> parts = new ArrayList<>();
        parts.add(AiPart.text(draftInstruction(categories, prompt)));
        boolean readable = true;
        for (String id : ids) {
            byte[] jpeg = fileService.readJpeg(id);
            if (jpeg == null || jpeg.length == 0) {
                readable = false;
                break;
            }
            parts.add(AiPart.jpeg(jpeg));
        }
        JsonNode node = null;
        if (readable) {
            node = ModelJson.object(objectMapper, ask(DRAFT_SYSTEM, parts, DRAFT_TOKENS).orElse(null));
        }
        DraftFields fields = readDraft(node, categories);
        String draftId = "d_" + Randoms.hex(8);
        Map<String, Object> body = draftBody(draftId, fields);
        store(RedisKeys.aiDraft(userId, draftId), body);
        return body;
    }

    public Map<String, Object> quote(long userId, long goodsId, String ip) {
        userService.requireUser(userId);
        GoodsRow goods = goodsService.findVisible(userId, goodsId);
        if (goods == null || goods.getPrice() == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        consumeDaily("quote", userId);
        List<AiPart> parts = new ArrayList<>();
        parts.add(AiPart.text(quoteInstruction(goods)));
        for (GoodsImageRow image : goodsMapper.listImages(List.of(goods.getId()))) {
            byte[] jpeg = fileService.readJpeg(image.getFileId());
            if (jpeg != null && jpeg.length > 0) {
                parts.add(AiPart.jpeg(jpeg));
            }
        }
        QuoteResult result = readQuoteResult(ModelJson.object(objectMapper, ask(
                QUOTE_SYSTEM,
                parts,
                QUOTE_TOKENS
        ).orElse(null)));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("goodsId", goods.getId());
        body.put("listPrice", goods.getPrice());
        if (result == null) {
            auditService.record("BUSINESS", "AI_QUOTE_FAILED", null, userId, ip, "{\"goodsId\":" + goods.getId() + "}");
            body.put("available", false);
            body.put("hint", "暂无建议价");
            return body;
        }
        body.put("available", true);
        body.put("suggestedPrice", result.price());
        body.put("reason", result.reason());
        body.put("disclaimer", "仅供参考");
        return body;
    }

    private Map<String, Object> issue(long userId, GoodsRow goods, QuoteRange range, Outcome outcome, String previousToken, String ip) {
        if (outcome.clamped()) {
            auditService.record("BUSINESS", "AI_QUOTE_CLAMPED", null, userId, ip, "{\"goodsId\":" + goods.getId() + "}");
        }
        String token = Randoms.hex(16);
        store(RedisKeys.aiQuote(token), quoteJson(new StoredQuote(
                userId,
                goods.getId(),
                goods.getPrice(),
                outcome.price(),
                range.floor(),
                range.ceiling()
        )));
        if (previousToken != null && !previousToken.equals(token)) {
            redis.delete(RedisKeys.aiQuote(previousToken));
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("goodsId", goods.getId());
        body.put("listPrice", goods.getPrice());
        body.put("priceFloor", range.floor());
        body.put("priceCeiling", range.ceiling());
        body.put("suggestedPrice", outcome.price());
        body.put("script", outcome.script());
        body.put("source", outcome.source());
        body.put("quoteToken", token);
        body.put("expiresInSeconds", TTL_SECONDS);
        return body;
    }

    private GoodsRow requireOnSale(long userId, long goodsId) {
        GoodsRow goods = goodsService.findVisible(userId, goodsId);
        if (goods == null || !"ON_SALE".equals(goods.getStatus()) || goods.getPrice() == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        return goods;
    }

    private Optional<String> ask(String system, List<AiPart> parts, int maxTokens) {
        try {
            return client.complete(system, parts, maxTokens);
        } catch (RuntimeException ex) {
            log.warn("ai upstream error type={}", ex.getClass().getSimpleName());
            return Optional.empty();
        }
    }

    private void store(String key, Object body) {
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(body), TTL);
        } catch (JacksonException ex) {
            throw new IllegalStateException("无法保存 AI 结果", ex);
        }
    }

    private ObjectNode quoteJson(StoredQuote quote) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("userId", quote.userId());
        node.put("goodsId", quote.goodsId());
        node.put("listPrice", quote.listPrice());
        node.put("suggestedPrice", quote.suggestedPrice());
        node.put("priceFloor", quote.priceFloor());
        node.put("priceCeiling", quote.priceCeiling());
        return node;
    }

    private StoredQuote readQuote(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(raw);
            Long userId = longField(node, "userId");
            Long goodsId = longField(node, "goodsId");
            Integer listPrice = intField(node, "listPrice");
            Integer suggested = intField(node, "suggestedPrice");
            Integer floor = intField(node, "priceFloor");
            Integer ceiling = intField(node, "priceCeiling");
            if (userId == null || goodsId == null || listPrice == null || suggested == null || floor == null || ceiling == null) {
                return null;
            }
            return new StoredQuote(userId, goodsId, listPrice, suggested, floor, ceiling);
        } catch (JacksonException ex) {
            return null;
        }
    }

    private Map<Long, CategoryView> categories() {
        Map<Long, CategoryView> categories = new LinkedHashMap<>();
        for (CategoryView category : categoryMapper.listEnabled()) {
            categories.put(category.getId(), category);
        }
        return categories;
    }

    private static String draftInstruction(Map<Long, CategoryView> categories, String prompt) {
        StringBuilder text = new StringBuilder();
        text.append("分类只能从下面选择 categoryId：");
        for (CategoryView category : categories.values()) {
            text.append(category.getId()).append(' ').append(category.getName()).append('；');
        }
        text.append("condition 只能是 NEW、LIKE_NEW、GOOD、FAIR、POOR。");
        text.append("字段只有 title、description、categoryId、condition、suggestedPrice、tradeNote。");
        text.append("title 2 到 30 字，description 不超过 500 字，tradeNote 不超过 100 字，suggestedPrice 是 1 到 9999900 的整数分。");
        if (prompt != null) {
            text.append("卖家补充：").append(prompt);
        }
        return text.toString();
    }

    private String quoteInstruction(GoodsRow goods) {
        String categoryName = "（无）";
        if (goods.getCategoryId() != null) {
            CategoryView category = categoryMapper.findEnabledById(goods.getCategoryId());
            if (category != null && category.getName() != null && !category.getName().isBlank()) {
                categoryName = category.getName();
            }
        }
        List<String> aliases = goodsMapper.listAliases(goods.getId());
        String aliasText = aliases == null || aliases.isEmpty() ? "（无）" : String.join("、", aliases);
        return "根据商品信息和图片给出参考价。\n"
                + "标题：" + nullToDash(goods.getTitle()) + "\n"
                + "描述：" + nullToDash(goods.getDescription()) + "\n"
                + "分类：" + categoryName + "\n"
                + "成色：" + nullToDash(goods.getConditionCode()) + "\n"
                + "同义叫法：" + aliasText + "\n"
                + "卖家标价 listPrice=" + goods.getPrice() + " 分\n"
                + "只输出 suggestedPrice 和 reason。\n"
                + "suggestedPrice 是 1 到 9999900 的整数分。\n"
                + "reason 是 20 到 300 字，结合图片、成色、品类和标价说明为什么是这个价；"
                + "看不清的地方如实写不确定，不要编造。不要写砍价话术。";
    }

    private static String nullToDash(String value) {
        return value == null || value.isBlank() ? "（无）" : value;
    }

    private Map<String, Object> draftBody(String draftId, DraftFields fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("draftId", draftId);
        body.put("filled", fields.filled());
        body.put("title", fields.title());
        body.put("description", fields.description());
        body.put("categoryId", fields.categoryId());
        body.put("condition", fields.condition());
        body.put("suggestedPrice", fields.suggestedPrice());
        body.put("aliases", fields.filled() ? List.of() : null);
        body.put("tradeNote", fields.tradeNote());
        body.put("expiresInSeconds", TTL_SECONDS);
        return body;
    }

    private static DraftFields readDraft(JsonNode node, Map<Long, CategoryView> categories) {
        if (node == null) {
            return DraftFields.empty();
        }
        return new DraftFields(
                cleanTitle(textField(node, "title")),
                cleanBounded(textField(node, "description"), 500),
                cleanCategory(intField(node, "categoryId"), categories),
                cleanCondition(textField(node, "condition")),
                cleanDraftPrice(intField(node, "suggestedPrice")),
                cleanBounded(textField(node, "tradeNote"), 100)
        );
    }

    private void consumeDaily(String kind, long userId) {
        String day = java.time.LocalDate.now(Times.SHANGHAI).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        String key = "draft".equals(kind) ? RedisKeys.aiDraftCount(userId, day) : RedisKeys.aiQuoteCount(userId, day);
        Long count = redis.opsForValue().increment(key);
        long retry = secondsUntilEndOfDay();
        if (count != null && count == 1L) {
            redis.expire(key, Duration.ofSeconds(retry));
        }
        if (count != null && count > 20) {
            throw ApiException.aiQuota(retry);
        }
    }

    private static long secondsUntilEndOfDay() {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(Times.SHANGHAI);
        java.time.ZonedDateTime end = now.toLocalDate().plusDays(1).atStartOfDay(Times.SHANGHAI);
        return Math.max(1, java.time.Duration.between(now, end).getSeconds());
    }

    private static QuoteResult readQuoteResult(JsonNode node) {
        if (node == null) {
            return null;
        }
        Integer price = intField(node, "suggestedPrice");
        if (price == null || price < 1 || price > 9_999_900) {
            return null;
        }
        String reason = cleanBounded(textField(node, "reason"), 300);
        if (reason == null) {
            return null;
        }
        int count = reason.codePointCount(0, reason.length());
        if (count < 20) {
            return null;
        }
        return new QuoteResult(price, reason);
    }

    private Outcome interpret(JsonNode node, QuoteRange range) {
        if (node == null) {
            return new Outcome(range.rulePrice(), RULE_SCRIPT, "RULE", false);
        }
        Integer price = intField(node, "suggestedPrice");
        String script = textField(node, "script");
        if (price != null && range.contains(price) && validScript(script)) {
            return new Outcome(price, script.trim(), "MODEL", false);
        }
        return new Outcome(range.rulePrice(), RULE_SCRIPT, "RULE", price != null && !range.contains(price));
    }

    private static String cleanTitle(String value) {
        if (value == null) {
            return null;
        }
        int count = value.codePointCount(0, value.length());
        if (count < 2 || count > 30 || ContactInfo.contains(value)) {
            return null;
        }
        return value;
    }

    private static String cleanBounded(String value, int max) {
        if (value == null) {
            return null;
        }
        int count = value.codePointCount(0, value.length());
        if (count < 1 || count > max || ContactInfo.contains(value)) {
            return null;
        }
        return value;
    }

    private static String cleanCondition(String value) {
        if (value == null) {
            return null;
        }
        String upper = value.toUpperCase(Locale.ROOT);
        return CONDITIONS.contains(upper) ? upper : null;
    }

    private static Long cleanCategory(Integer id, Map<Long, CategoryView> categories) {
        if (id == null) {
            return null;
        }
        long value = id.longValue();
        return categories.containsKey(value) ? value : null;
    }

    private static Integer cleanDraftPrice(Integer price) {
        if (price == null || price < 1 || price > 9_999_900) {
            return null;
        }
        return price;
    }

    private static boolean validScript(String script) {
        if (script == null) {
            return false;
        }
        String trimmed = script.trim();
        int count = trimmed.codePointCount(0, trimmed.length());
        return count >= 1 && count <= 200 && !ContactInfo.contains(trimmed);
    }

    private List<String> normalizeImages(long userId, List<String> imageFileIds) {
        if (imageFileIds == null || imageFileIds.isEmpty() || imageFileIds.size() > 9) {
            throw ApiException.validation(List.of(new FieldErrorItem("imageFileIds", "商品图片为 1–9 张")));
        }
        List<String> ids = new ArrayList<>();
        for (String raw : imageFileIds) {
            if (raw == null || raw.isBlank()) {
                throw ApiException.validation(List.of(new FieldErrorItem("imageFileIds", "商品图片为 1–9 张")));
            }
            ids.add(raw.trim());
        }
        if (new HashSet<>(ids).size() != ids.size()) {
            throw ApiException.validation(List.of(new FieldErrorItem("imageFileIds", "图片不能重复")));
        }
        for (String id : ids) {
            fileService.requireUsable(id, userId, "GOODS");
        }
        return ids;
    }

    private static void checkPrompt(String prompt) {
        if (prompt == null) {
            return;
        }
        int count = prompt.codePointCount(0, prompt.length());
        if (count < 1 || count > 200) {
            throw ApiException.validation(List.of(new FieldErrorItem("prompt", "补充说明为 1–200 字")));
        }
        if (ContactInfo.contains(prompt)) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
    }

    private static void checkMessage(String message) {
        if (message == null) {
            return;
        }
        int count = message.codePointCount(0, message.length());
        if (count < 1 || count > 200) {
            throw ApiException.validation(List.of(new FieldErrorItem("message", "议价说明为 1–200 字")));
        }
        if (ContactInfo.contains(message)) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
    }

    private static String textField(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || !value.isTextual()) {
            return null;
        }
        String trimmed = value.asText().trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static Integer intField(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || !value.isIntegralNumber() || !value.canConvertToInt()) {
            return null;
        }
        return value.intValue();
    }

    private static Long longField(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || !value.isIntegralNumber() || !value.canConvertToLong()) {
            return null;
        }
        return value.longValue();
    }

    private record DraftFields(
            String title,
            String description,
            Long categoryId,
            String condition,
            Integer suggestedPrice,
            String tradeNote
    ) {
        static DraftFields empty() {
            return new DraftFields(null, null, null, null, null, null);
        }

        boolean filled() {
            return title != null || description != null || categoryId != null
                    || condition != null || suggestedPrice != null || tradeNote != null;
        }
    }

    private record Outcome(int price, String script, String source, boolean clamped) {
    }

    private record QuoteResult(int price, String reason) {
    }

    private record StoredQuote(
            long userId,
            long goodsId,
            int listPrice,
            int suggestedPrice,
            int priceFloor,
            int priceCeiling
    ) {
    }
}
