package com.campus.ershou.order;

import com.campus.ershou.admin.AdminAccount;
import com.campus.ershou.admin.AdminMapper;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.util.Randoms;
import com.campus.ershou.common.util.StudentNoHasher;
import com.campus.ershou.pay.PaySigner;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class V161RulesIT {

    private static final String SELLER_NO = "91003101";
    private static final String BUYER_NO = "91003102";
    private static final String OTHER_NO = "91003103";
    private static final String PASSWORD = "FlowTest-1";
    private static final Path FILE_DIR = Path.of(System.getProperty("java.io.tmpdir"), "campus-ershou-v161-it");

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
    private AdminMapper adminMapper;
    @Autowired
    private StudentNoHasher hasher;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AppProperties props;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        cleanup();
        insertUser(SELLER_NO, "v161_seller", "v161-seller@example.com");
        insertUser(BUYER_NO, "v161_buyer", "v161-buyer@example.com");
        insertUser(OTHER_NO, "v161_other", "v161-other@example.com");
        insertAdmin();
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    void lateSuccessDoesNotReviveCancelledOrder() throws Exception {
        String seller = login(SELLER_NO);
        String buyer = login(BUYER_NO);
        String other = login(OTHER_NO);
        long goodsId = publish(seller);
        int orderId = order(buyer, goodsId);
        String orderNo = jdbc.queryForObject("SELECT order_no FROM orders WHERE id = ?", String.class, orderId);
        String paymentNo = jdbc.queryForObject("SELECT payment_no FROM payments WHERE order_id = ?", String.class, orderId);

        mockMvc.perform(post("/api/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + buyer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        notifySuccess(paymentNo, orderNo, 1500, "N-late-0001");
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, orderId)).isEqualTo("CANCELLED");
        assertThat(jdbc.queryForObject("SELECT status FROM payments WHERE payment_no = ?", String.class, paymentNo)).isEqualTo("MOCK_REFUNDED");
        assertThat(jdbc.queryForObject("SELECT reason FROM payments WHERE payment_no = ?", String.class, paymentNo)).isEqualTo("取消或释放后的迟到支付");
        assertThat(jdbc.queryForObject("SELECT status FROM goods WHERE id = ?", String.class, goodsId)).isEqualTo("ON_SALE");

        int nextId = order(other, goodsId);
        notifySuccess(paymentNo, orderNo, 1500, "N-late-0002");
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, orderId)).isEqualTo("CANCELLED");
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, nextId)).isEqualTo("PENDING_PAY");
        assertThat(jdbc.queryForObject("SELECT status FROM goods WHERE id = ?", String.class, goodsId)).isEqualTo("RESERVED");
    }

    @Test
    void threeDisputeResults() throws Exception {
        String seller = login(SELLER_NO);
        String buyer = login(BUYER_NO);
        String admin = loginAdmin();

        long rejectGoods = publish(seller);
        int rejectOrder = pay(buyer, rejectGoods);
        dispute(buyer, rejectOrder, "MISSED_MEETUP");
        mockMvc.perform(post("/api/admin/disputes/" + disputeId(rejectOrder) + "/resolve")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"result\":\"COMPLETE\",\"remark\":\"双方还没确认\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ORDER_STATUS_INVALID"));
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, rejectOrder)).isEqualTo("DISPUTED");

        mockMvc.perform(post("/api/admin/disputes/" + disputeId(rejectOrder) + "/resolve")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"result\":\"REJECT\",\"remark\":\"驳回后继续面交\"}"))
                .andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, rejectOrder)).isEqualTo("PAID");
        assertThat(jdbc.queryForObject("SELECT status FROM goods WHERE id = ?", String.class, rejectGoods)).isEqualTo("MEETUP");

        long doneGoods = publish(seller);
        int doneOrder = pay(buyer, doneGoods);
        dispute(buyer, doneOrder, "FALSE_DESCRIPTION");
        mockMvc.perform(post("/api/orders/" + doneOrder + "/handover-confirm").header("Authorization", "Bearer " + buyer))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/orders/" + doneOrder + "/handover-confirm").header("Authorization", "Bearer " + seller))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/admin/disputes/" + disputeId(doneOrder) + "/resolve")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"result\":\"COMPLETE\",\"remark\":\"双方已确认，完成交易\"}"))
                .andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, doneOrder)).isEqualTo("COMPLETED");
        assertThat(jdbc.queryForObject("SELECT status FROM goods WHERE id = ?", String.class, doneGoods)).isEqualTo("SOLD");

        long fraudGoods = publish(seller);
        int fraudOrder = pay(buyer, fraudGoods);
        dispute(buyer, fraudOrder, "SUSPECTED_FRAUD");
        mockMvc.perform(post("/api/admin/disputes/" + disputeId(fraudOrder) + "/resolve")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"result\":\"CANCEL\",\"remark\":\"疑似欺诈，取消订单\"}"))
                .andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, fraudOrder)).isEqualTo("CANCELLED");
        assertThat(jdbc.queryForObject("SELECT status FROM payments WHERE order_id = ?", String.class, fraudOrder)).isEqualTo("MOCK_REFUNDED");
        assertThat(jdbc.queryForObject("SELECT status FROM goods WHERE id = ?", String.class, fraudGoods)).isEqualTo("OFF_SHELF");
        assertThat(jdbc.queryForObject("SELECT publish_banned FROM users WHERE email = 'v161-seller@example.com'", Integer.class)).isEqualTo(1);
        login(SELLER_NO);
    }

    @Test
    void reportRemoveDoesNotCancelOrder() throws Exception {
        String seller = login(SELLER_NO);
        String buyer = login(BUYER_NO);
        String other = login(OTHER_NO);
        String admin = loginAdmin();
        long goodsId = publish(seller);
        int orderId = order(buyer, goodsId);
        mockMvc.perform(post("/api/reports")
                        .header("Authorization", "Bearer " + other)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + ",\"description\":\"标题和实物不符\"}"))
                .andExpect(status().isOk());
        int reportId = jdbc.queryForObject(
                "SELECT id FROM reports WHERE target_id = ? AND status = 'PENDING'", Integer.class, goodsId);
        mockMvc.perform(post("/api/admin/reports/" + reportId + "/handle")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"REMOVE_GOODS\",\"reason\":\"描述不实，下架商品\"}"))
                .andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT status FROM goods WHERE id = ?", String.class, goodsId)).isEqualTo("REMOVED");
        assertThat(jdbc.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, orderId)).isEqualTo("PENDING_PAY");
        assertThat(jdbc.queryForObject("SELECT status FROM payments WHERE order_id = ?", String.class, orderId)).isEqualTo("PENDING");
    }

    private void notifySuccess(String paymentNo, String orderNo, int amount, String notifyNo) throws Exception {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("paymentNo", paymentNo);
        body.put("orderNo", orderNo);
        body.put("amount", String.valueOf(amount));
        body.put("notifyNo", notifyNo);
        body.put("status", "SUCCESS");
        body.put("timestamp", String.valueOf(Instant.now().getEpochSecond()));
        body.put("nonce", Randoms.hex(8));
        body.put("sign", PaySigner.sign(body, props.getPay().getMock().getSecret()));
        mockMvc.perform(post("/api/pay/notify/mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
    }

    private long publish(String token) throws Exception {
        String fileId = upload(token);
        MvcResult created = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"规则测试书","description":"几乎全新","categoryId":1,"condition":"LIKE_NEW","price":1500,
                                 "imageFileIds":["%s"],"meetupBuildingId":1}
                                """.formatted(fileId)))
                .andExpect(status().isOk())
                .andReturn();
        Number id = JsonPath.read(created.getResponse().getContentAsString(), "$.data.id");
        return id.longValue();
    }

    private int order(String token, long goodsId) throws Exception {
        MvcResult created = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goodsId\":" + goodsId + ",\"quantity\":1}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(created.getResponse().getContentAsString(), "$.data.id");
    }

    private int pay(String token, long goodsId) throws Exception {
        int orderId = order(token, goodsId);
        String paymentNo = jdbc.queryForObject("SELECT payment_no FROM payments WHERE order_id = ?", String.class, orderId);
        mockMvc.perform(post("/api/pay/mock/" + paymentNo + "/confirm")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
        return orderId;
    }

    private void dispute(String token, int orderId, String reason) throws Exception {
        String evidence = "";
        if ("FALSE_DESCRIPTION".equals(reason) || "GOODS_MISMATCH".equals(reason) || "SUSPECTED_FRAUD".equals(reason)) {
            String fileId = upload(token, "EVIDENCE");
            evidence = ",\"evidenceFileIds\":[\"" + fileId + "\"]";
        }
        mockMvc.perform(post("/api/orders/" + orderId + "/dispute")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"" + reason + "\",\"description\":\"实物和页面差得比较多，申请处理。\"" + evidence + "}"))
                .andExpect(status().isOk());
    }

    private int disputeId(int orderId) {
        return jdbc.queryForObject("SELECT id FROM disputes WHERE order_id = ? ORDER BY id DESC LIMIT 1", Integer.class, orderId);
    }

    private String login(String studentNo) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentNo\":\"" + studentNo + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");
    }

    private String loginAdmin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"v161_admin\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");
    }

    private String upload(String token) throws Exception {
        return upload(token, "GOODS");
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
        user.setPasswordHash(passwordEncoder.encode(PASSWORD));
        user.setStatus("ACTIVE");
        user.setVerifyStatus("VERIFIED");
        user.setTokenVersion(1);
        userMapper.insert(user);
    }

    private void insertAdmin() {
        AdminAccount admin = new AdminAccount();
        admin.setUsername("v161_admin");
        admin.setEmail("v161-admin@example.com");
        admin.setPasswordHash(passwordEncoder.encode(PASSWORD));
        admin.setStatus("ACTIVE");
        admin.setMustChangePassword(false);
        admin.setTokenVersion(1);
        adminMapper.insert(admin);
    }

    private void cleanup() {
        jdbc.update("""
                DELETE rf FROM report_files rf
                INNER JOIN reports r ON r.id = rf.report_id
                INNER JOIN users u ON u.id = r.reporter_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE r FROM reports r
                INNER JOIN users u ON u.id = r.reporter_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE df FROM dispute_files df
                INNER JOIN disputes d ON d.id = df.dispute_id
                INNER JOIN orders o ON o.id = d.order_id
                INNER JOIN users u ON u.id = o.buyer_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE d FROM disputes d
                INNER JOIN orders o ON o.id = d.order_id
                INNER JOIN users u ON u.id = o.buyer_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE om FROM order_messages om
                INNER JOIN orders o ON o.id = om.order_id
                INNER JOIN users u ON u.id = o.buyer_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE pn FROM payment_notifies pn
                INNER JOIN payments p ON p.id = pn.payment_id
                INNER JOIN users u ON u.id = p.buyer_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE p FROM payments p
                INNER JOIN users u ON u.id = p.buyer_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE e FROM order_events e
                INNER JOIN orders o ON o.id = e.order_id
                INNER JOIN users u ON u.id = o.buyer_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE o FROM orders o
                INNER JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE n FROM notifications n
                INNER JOIN users u ON u.id = n.user_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE ga FROM goods_aliases ga
                INNER JOIN goods g ON g.id = ga.goods_id
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE gi FROM goods_images gi
                INNER JOIN goods g ON g.id = gi.goods_id
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE g FROM goods g
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("""
                DELETE sf FROM stored_files sf
                INNER JOIN users u ON u.id = sf.owner_id
                WHERE u.email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')
                """);
        jdbc.update("DELETE FROM users WHERE email IN ('v161-seller@example.com', 'v161-buyer@example.com', 'v161-other@example.com')");
        jdbc.update("DELETE FROM admins WHERE username = 'v161_admin'");
        try {
            if (Files.isDirectory(FILE_DIR)) {
                try (var paths = Files.walk(FILE_DIR)) {
                    paths.sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                        }
                    });
                }
            }
        } catch (Exception ignored) {
        }
    }
}
