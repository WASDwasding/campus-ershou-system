package com.campus.ershou.ai;

import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.common.util.StudentNoHasher;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AiFlowIT {

    private static final String SELLER_NO = "91002101";
    private static final String BUYER_NO = "91002102";
    private static final String SELLER_EMAIL = "ai-seller@example.com";
    private static final String BUYER_EMAIL = "ai-buyer@example.com";
    private static final Path FILE_DIR = Path.of(System.getProperty("java.io.tmpdir"), "campus-ershou-ai-it-files");
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final AtomicReference<String> UPSTREAM_BODY = new AtomicReference<>("{\"choices\":[{\"message\":{\"content\":\"\"}}]}");
    private static final AtomicInteger UPSTREAM_CALLS = new AtomicInteger();
    private static final HttpServer UPSTREAM = startUpstream();

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("app.file.dir", FILE_DIR::toString);
        registry.add("app.ai.enabled", () -> "true");
        registry.add("app.ai.api-key", () -> "test-key");
        registry.add("app.ai.base-url", () -> "http://127.0.0.1:" + UPSTREAM.getAddress().getPort() + "/v1");
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentNoHasher hasher;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate redis;

    private long sellerId;
    private long buyerId;

    @BeforeEach
    void setUp() throws Exception {
        cleanup();
        UPSTREAM_CALLS.set(0);
        reply("");
        sellerId = insertUser(SELLER_NO, "ai_seller", SELLER_EMAIL, "VERIFIED");
        buyerId = insertUser(BUYER_NO, "ai_buyer", BUYER_EMAIL, "UNVERIFIED");
    }

    @AfterEach
    void tearDown() throws Exception {
        cleanup();
    }

    @Test
    void draftFallsBackWhenUpstreamIsEmpty() throws Exception {
        String sellerToken = login(SELLER_NO);
        String imageId = upload(sellerToken, "GOODS");

        MvcResult result = mockMvc.perform(post("/api/ai/goods-draft")
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"imageFileIds":["%s"]}
                                """.formatted(imageId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.filled").value(false))
                .andExpect(jsonPath("$.data.draftId").value(startsWith("d_")))
                .andExpect(jsonPath("$.data.title").value(nullValue()))
                .andExpect(jsonPath("$.data.expiresInSeconds").value(600))
                .andReturn();
        String draftId = JsonPath.read(result.getResponse().getContentAsString(), "$.data.draftId");
        String key = RedisKeys.aiDraft(sellerId, draftId);
        assertThat(redis.opsForValue().get(key)).isNotBlank();
        assertThat(redis.getExpire(key)).isBetween(1L, 600L);
    }

    @Test
    void draftKeepsValidFieldsAndPublishIgnoresSuggestedPrice() throws Exception {
        reply("""
                ```json
                {"title":"蓝牙耳机","description":"联系13800138000","categoryId":2,"condition":"good","suggestedPrice":8800,"tradeNote":"一楼"}
                ```
                """);
        String sellerToken = login(SELLER_NO);
        String imageId = upload(sellerToken, "GOODS");

        mockMvc.perform(post("/api/ai/goods-draft")
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"imageFileIds":["%s"],"prompt":"耳机"}
                                """.formatted(imageId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.filled").value(true))
                .andExpect(jsonPath("$.data.title").value("蓝牙耳机"))
                .andExpect(jsonPath("$.data.description").value(nullValue()))
                .andExpect(jsonPath("$.data.categoryId").value(2))
                .andExpect(jsonPath("$.data.condition").value("GOOD"))
                .andExpect(jsonPath("$.data.suggestedPrice").value(8800))
                .andExpect(jsonPath("$.data.tradeNote").value("一楼"));

        mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(goodsJson(imageId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.price").value(1500));
    }

    @Test
    void unverifiedAndForeignFileAndContactAreRejected() throws Exception {
        String sellerToken = login(SELLER_NO);
        String buyerToken = login(BUYER_NO);
        String imageId = upload(sellerToken, "GOODS");

        mockMvc.perform(post("/api/ai/goods-draft")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"imageFileIds":["missing"]}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("NOT_VERIFIED"));

        jdbc.update("UPDATE users SET verify_status = 'VERIFIED' WHERE id = ?", buyerId);
        mockMvc.perform(post("/api/ai/goods-draft")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"imageFileIds":["%s"]}
                                """.formatted(imageId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("FILE_NOT_USABLE"));

        mockMvc.perform(post("/api/ai/goods-draft")
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"imageFileIds":["%s"],"prompt":"电话13800138000"}
                                """.formatted(imageId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CONTACT_INFO_NOT_ALLOWED"));
        assertThat(UPSTREAM_CALLS.get()).isZero();
    }

    @Test
    void quoteUsesRuleWhenUpstreamIsEmpty() throws Exception {
        String sellerToken = login(SELLER_NO);
        String buyerToken = login(BUYER_NO);
        long goodsId = createGoods(sellerToken);

        MvcResult result = mockMvc.perform(post("/api/ai/quotes")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.listPrice").value(1500))
                .andExpect(jsonPath("$.data.priceFloor").value(1050))
                .andExpect(jsonPath("$.data.priceCeiling").value(1500))
                .andExpect(jsonPath("$.data.suggestedPrice").value(1275))
                .andExpect(jsonPath("$.data.script").value(AiService.RULE_SCRIPT))
                .andExpect(jsonPath("$.data.source").value("RULE"))
                .andExpect(jsonPath("$.data.expiresInSeconds").value(600))
                .andReturn();
        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.data.quoteToken");
        assertThat(token).hasSize(32);
        assertThat(redis.opsForValue().get(RedisKeys.aiQuote(token))).contains("\"listPrice\":1500");
        assertThat(clampCount(buyerId, goodsId)).isZero();
    }

    @Test
    void quoteClampsOutOfRangeAndKeepsInRangeModelPrice() throws Exception {
        String sellerToken = login(SELLER_NO);
        String buyerToken = login(BUYER_NO);
        long goodsId = createGoods(sellerToken);

        reply("""
                {"suggestedPrice":100,"script":"可以便宜"}
                """);
        mockMvc.perform(post("/api/ai/quotes")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.suggestedPrice").value(1275))
                .andExpect(jsonPath("$.data.source").value("RULE"))
                .andExpect(jsonPath("$.data.script").value(AiService.RULE_SCRIPT));
        assertThat(clampCount(buyerId, goodsId)).isEqualTo(1);

        reply("""
                {"suggestedPrice":1200,"script":"请加微信 abcdef"}
                """);
        mockMvc.perform(post("/api/ai/quotes")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.source").value("RULE"));
        assertThat(clampCount(buyerId, goodsId)).isEqualTo(1);

        reply("""
                {"suggestedPrice":1200,"script":"可以小刀，当面看货"}
                """);
        mockMvc.perform(post("/api/ai/quotes")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.suggestedPrice").value(1200))
                .andExpect(jsonPath("$.data.source").value("MODEL"))
                .andExpect(jsonPath("$.data.script").value("可以小刀，当面看货"));
        assertThat(clampCount(buyerId, goodsId)).isEqualTo(1);
    }

    @Test
    void bargainReplacesTokenAndRejectsStalePriceOrShelf() throws Exception {
        String sellerToken = login(SELLER_NO);
        String buyerToken = login(BUYER_NO);
        long goodsId = createGoods(sellerToken);
        String token = quoteToken(buyerToken, goodsId);

        mockMvc.perform(post("/api/ai/bargain")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"goodsId":%d,"quoteToken":"aaaaaaaaaaaaaaaa","message":"能少点吗"}
                                """.formatted(goodsId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("QUOTE_TOKEN_INVALID"));
        assertThat(redis.hasKey(RedisKeys.aiQuote(token))).isTrue();

        reply("""
                {"suggestedPrice":1100,"script":"当面可以再少一点"}
                """);
        MvcResult bargained = mockMvc.perform(post("/api/ai/bargain")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"goodsId":%d,"quoteToken":"%s","message":"能少点吗"}
                                """.formatted(goodsId, token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.suggestedPrice").value(1100))
                .andExpect(jsonPath("$.data.source").value("MODEL"))
                .andReturn();
        String next = JsonPath.read(bargained.getResponse().getContentAsString(), "$.data.quoteToken");
        assertThat(next).isNotEqualTo(token);
        assertThat(redis.hasKey(RedisKeys.aiQuote(token))).isFalse();
        assertThat(redis.hasKey(RedisKeys.aiQuote(next))).isTrue();

        long secondId = createGoods(sellerToken);
        String secondToken = quoteToken(buyerToken, secondId);
        jdbc.update("UPDATE goods SET price = 2000 WHERE id = ?", secondId);
        mockMvc.perform(post("/api/ai/bargain")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"goodsId":%d,"quoteToken":"%s"}
                                """.formatted(secondId, secondToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("QUOTE_TOKEN_INVALID"));
        assertThat(redis.hasKey(RedisKeys.aiQuote(secondToken))).isTrue();

        long thirdId = createGoods(sellerToken);
        String thirdToken = quoteToken(buyerToken, thirdId);
        mockMvc.perform(post("/api/goods/" + thirdId + "/off-shelf")
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/ai/bargain")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"goodsId":%d,"quoteToken":"%s"}
                                """.formatted(thirdId, thirdToken)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("GOODS_NOT_AVAILABLE"));
    }

    @Test
    void hiddenGoodsAndTamperedPriceAreRejected() throws Exception {
        String buyerToken = login(BUYER_NO);

        mockMvc.perform(post("/api/ai/quotes")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":999999999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        mockMvc.perform(post("/api/ai/quotes")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":1,\"suggestedPrice\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UNKNOWN_FIELD"));
    }

    @Test
    void quoteIsRateLimited() throws Exception {
        String sellerToken = login(SELLER_NO);
        String buyerToken = login(BUYER_NO);
        long goodsId = createGoods(sellerToken);
        String body = "{\"goodsId\":" + goodsId + "}";
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/ai/quotes")
                            .header("Authorization", "Bearer " + buyerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(post("/api/ai/quotes")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(HttpHeaders.RETRY_AFTER, "60"))
                .andExpect(jsonPath("$.code").value("RATE_LIMITED"));
    }

    private String quoteToken(String token, long goodsId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ai/quotes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + "}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.quoteToken");
    }

    private long createGoods(String sellerToken) throws Exception {
        String imageId = upload(sellerToken, "GOODS");
        MvcResult created = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(goodsJson(imageId)))
                .andExpect(status().isOk())
                .andReturn();
        Number id = JsonPath.read(created.getResponse().getContentAsString(), "$.data.id");
        return id.longValue();
    }

    private int clampCount(long userId, long goodsId) {
        Integer count = jdbc.queryForObject("""
                SELECT COUNT(*) FROM audit_logs
                WHERE category = 'BUSINESS' AND event = 'AI_QUOTE_CLAMPED' AND user_id = ? AND detail = ?
                """, Integer.class, userId, "{\"goodsId\":" + goodsId + "}");
        return count == null ? 0 : count;
    }

    private String login(String studentNo) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentNo":"%s","password":"FlowTest-1"}
                                """.formatted(studentNo)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");
    }

    private String upload(String token, String purpose) throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/files")
                        .file(jpeg())
                        .param("purpose", purpose)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.fileId");
    }

    private static String goodsJson(String imageId) {
        return """
                {"title":"高数教材","description":"九成新","categoryId":1,"condition":"GOOD","price":1500,
                 "imageFileIds":["%s"],"meetupBuildingId":1,"tradeNote":"宿舍楼下"}
                """.formatted(imageId);
    }

    private static MockMultipartFile jpeg() throws Exception {
        BufferedImage image = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.BLUE.getRGB());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        return new MockMultipartFile("file", "photo.jpg", "image/jpeg", out.toByteArray());
    }

    private long insertUser(String studentNo, String username, String email, String verifyStatus) {
        UserEntity user = new UserEntity();
        user.setStudentNoHash(hasher.hash(studentNo));
        user.setStudentNoMasked(hasher.mask(studentNo));
        user.setUsername(username);
        user.setNickname(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("FlowTest-1"));
        user.setStatus("ACTIVE");
        user.setVerifyStatus(verifyStatus);
        user.setTokenVersion(1);
        userMapper.insert(user);
        return user.getId();
    }

    private void cleanup() throws Exception {
        jdbc.update("""
                DELETE a FROM audit_logs a
                INNER JOIN users u ON u.id = a.user_id
                WHERE u.email IN (?, ?)
                """, SELLER_EMAIL, BUYER_EMAIL);
        jdbc.update("""
                DELETE gi FROM goods_images gi
                INNER JOIN goods g ON g.id = gi.goods_id
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN (?, ?)
                """, SELLER_EMAIL, BUYER_EMAIL);
        jdbc.update("""
                DELETE g FROM goods g
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN (?, ?)
                """, SELLER_EMAIL, BUYER_EMAIL);
        jdbc.update("""
                DELETE sf FROM stored_files sf
                INNER JOIN users u ON u.id = sf.owner_id
                WHERE u.email IN (?, ?)
                """, SELLER_EMAIL, BUYER_EMAIL);
        jdbc.update("DELETE FROM users WHERE email IN (?, ?)", SELLER_EMAIL, BUYER_EMAIL);
        if (Files.isDirectory(FILE_DIR)) {
            try (var paths = Files.walk(FILE_DIR)) {
                paths.sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                        // 测试目录清不掉不影响断言
                    }
                });
            }
        }
    }

    private static void reply(String modelText) throws Exception {
        ObjectNode message = JSON.createObjectNode();
        message.put("content", modelText);
        ObjectNode choice = JSON.createObjectNode();
        choice.set("message", message);
        ObjectNode root = JSON.createObjectNode();
        root.putArray("choices").add(choice);
        UPSTREAM_BODY.set(JSON.writeValueAsString(root));
    }

    private static HttpServer startUpstream() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/v1/chat/completions", exchange -> {
                exchange.getRequestBody().readAllBytes();
                UPSTREAM_CALLS.incrementAndGet();
                byte[] body = UPSTREAM_BODY.get().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            ExecutorService pool = Executors.newCachedThreadPool(task -> {
                Thread thread = new Thread(task, "ai-it-upstream");
                thread.setDaemon(true);
                return thread;
            });
            server.setExecutor(pool);
            server.start();
            return server;
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @AfterAll
    static void stopUpstream() {
        UPSTREAM.stop(0);
    }
}
