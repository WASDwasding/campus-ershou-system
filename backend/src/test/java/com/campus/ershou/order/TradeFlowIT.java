package com.campus.ershou.order;

import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.common.util.StudentNoHasher;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TradeFlowIT {

    private static final String SELLER_NO = "91002101";
    private static final String BUYER_NO = "91002102";
    private static final String OTHER_NO = "91002103";
    private static final Path FILE_DIR = Path.of(System.getProperty("java.io.tmpdir"), "campus-ershou-trade-it");

    @DynamicPropertySource
    static void fileDir(DynamicPropertyRegistry registry) {
        registry.add("app.file.dir", FILE_DIR::toString);
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

    @BeforeEach
    void setUp() {
        cleanup();
        insertUser(SELLER_NO, "trade_seller", "trade-seller@example.com");
        insertUser(BUYER_NO, "trade_buyer", "trade-buyer@example.com");
        insertUser(OTHER_NO, "trade_other", "trade-other@example.com");
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    void orderPayMeetupAndReview() throws Exception {
        String seller = login(SELLER_NO);
        String buyer = login(BUYER_NO);
        String other = login(OTHER_NO);
        String fileId = upload(seller);
        MvcResult published = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + seller)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"交易测试书","description":"几乎全新","categoryId":1,"condition":"LIKE_NEW","price":1500,
                                 "imageFileIds":["%s"],"meetupBuildingId":1}
                                """.formatted(fileId)))
                .andExpect(status().isOk())
                .andReturn();
        int goodsId = JsonPath.read(published.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + buyer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + ",\"quantity\":1,\"price\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UNKNOWN_FIELD"));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM orders WHERE goods_id = ?", Integer.class, goodsId)).isZero();

        MvcResult created = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + buyer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + ",\"quantity\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING_PAY"))
                .andExpect(jsonPath("$.data.amount").value(1500))
                .andExpect(jsonPath("$.data.allowedActions[0]").value("PAY"))
                .andReturn();
        int orderId = JsonPath.read(created.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + other)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + ",\"quantity\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("GOODS_NOT_AVAILABLE"));

        mockMvc.perform(get("/api/orders/" + orderId).header("Authorization", "Bearer " + other))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        MvcResult payment = mockMvc.perform(post("/api/orders/" + orderId + "/payments")
                        .header("Authorization", "Bearer " + buyer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"channel\":\"MOCK\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(1500))
                .andReturn();
        String paymentNo = JsonPath.read(payment.getResponse().getContentAsString(), "$.data.paymentNo");

        mockMvc.perform(post("/api/pay/notify/mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"paymentNo":"%s","orderNo":"O","amount":"1","status":"SUCCESS","gatewayTradeNo":"X","timestamp":"1","nonce":"1234567890abcdef","sign":"00"}
                                """.formatted(paymentNo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("FAIL"));
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, orderId)).isEqualTo("PENDING_PAY");

        mockMvc.perform(post("/api/pay/mock/" + paymentNo + "/confirm")
                        .header("Authorization", "Bearer " + buyer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"result\":\"SUCCESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, orderId)).isEqualTo("PAID");

        String meetup = """
                {"buildingId":1,"place":"东门传达室","time":"%s"}
                """.formatted(java.time.OffsetDateTime.now().plusDays(1).withNano(0));
        mockMvc.perform(put("/api/orders/" + orderId + "/meetup")
                        .header("Authorization", "Bearer " + buyer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(meetup))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetup.place").value("东门传达室"));

        mockMvc.perform(post("/api/orders/" + orderId + "/handover-confirm")
                        .header("Authorization", "Bearer " + buyer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
        mockMvc.perform(post("/api/orders/" + orderId + "/handover-confirm")
                        .header("Authorization", "Bearer " + seller))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        mockMvc.perform(post("/api/orders/" + orderId + "/review")
                        .header("Authorization", "Bearer " + buyer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":5,\"content\":\"很好\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.review.mine.score").value(5));
        mockMvc.perform(get("/api/users/" + jdbc.queryForObject("SELECT id FROM users WHERE email = 'trade-seller@example.com'", Long.class) + "/reviews")
                        .header("Authorization", "Bearer " + buyer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1));
    }

    private String login(String studentNo) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentNo\":\"" + studentNo + "\",\"password\":\"FlowTest-1\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");
    }

    private String upload(String token) throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/files")
                        .file(jpeg())
                        .param("purpose", "GOODS")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.fileId");
    }

    private static MockMultipartFile jpeg() throws Exception {
        BufferedImage image = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.BLUE.getRGB());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        return new MockMultipartFile("file", "photo.jpg", "image/jpeg", out.toByteArray());
    }

    private void insertUser(String studentNo, String username, String email) {
        UserEntity user = new UserEntity();
        user.setStudentNoHash(hasher.hash(studentNo));
        user.setStudentNoMasked(hasher.mask(studentNo));
        user.setUsername(username);
        user.setNickname(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("FlowTest-1"));
        user.setStatus("ACTIVE");
        user.setVerifyStatus("VERIFIED");
        user.setTokenVersion(1);
        userMapper.insert(user);
    }

    private void cleanup() {
        jdbc.update("""
                DELETE df FROM dispute_files df
                INNER JOIN disputes d ON d.id = df.dispute_id
                INNER JOIN orders o ON o.id = d.order_id
                INNER JOIN users u ON u.id = o.buyer_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE d FROM disputes d
                INNER JOIN orders o ON o.id = d.order_id
                INNER JOIN users u ON u.id = o.buyer_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE r FROM reviews r
                INNER JOIN users u ON u.id = r.reviewer_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE p FROM payments p
                INNER JOIN users u ON u.id = p.buyer_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE e FROM order_events e
                INNER JOIN orders o ON o.id = e.order_id
                INNER JOIN users u ON u.id = o.buyer_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE o FROM orders o
                INNER JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE m FROM messages m
                INNER JOIN conversations c ON c.id = m.conversation_id
                INNER JOIN users u ON u.id = c.buyer_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE c FROM conversations c
                INNER JOIN users u ON u.id = c.buyer_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE n FROM notifications n
                INNER JOIN users u ON u.id = n.user_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE gi FROM goods_images gi
                INNER JOIN goods g ON g.id = gi.goods_id
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE g FROM goods g
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("""
                DELETE sf FROM stored_files sf
                INNER JOIN users u ON u.id = sf.owner_id
                WHERE u.email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')
                """);
        jdbc.update("DELETE FROM users WHERE email IN ('trade-seller@example.com', 'trade-buyer@example.com', 'trade-other@example.com')");
        redis.delete(RedisKeys.orderTimeouts(0));
        try {
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
        } catch (Exception ignored) {
            // 目录不存在
        }
    }
}
