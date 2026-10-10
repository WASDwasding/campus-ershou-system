package com.campus.ershou.auth;

import com.campus.ershou.admin.AdminAccount;
import com.campus.ershou.admin.AdminMapper;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AdminReviewIT {

    private static final String ADMIN = "review_admin";
    private static final String LOCKED_ADMIN = "review_admin_lock";
    private static final String USER_EMAIL = "review-user@example.com";
    private static final String PASSWORD = "ReviewTest-1234";
    private static final Path FILE_DIR = Path.of(System.getProperty("java.io.tmpdir"), "campus-ershou-review-it");

    @DynamicPropertySource
    static void fileDir(DynamicPropertyRegistry registry) {
        registry.add("app.file.dir", FILE_DIR::toString);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentNoHasher hasher;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private long userId;

    @BeforeEach
    void setUp() {
        cleanup();
        insertAdmin(ADMIN, "review-admin@example.com", false);
        insertAdmin(LOCKED_ADMIN, "review-admin-lock@example.com", true);
        userId = insertUser();
    }

    @AfterEach
    void tearDown() throws Exception {
        cleanup();
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

    @Test
    void adminCanApproveOrRejectVerification() throws Exception {
        String userToken = loginUser();
        String adminToken = loginAdmin(ADMIN);
        long pendingBefore = jdbc.queryForObject(
                "SELECT COUNT(*) FROM campus_verifications WHERE status = 'PENDING'", Long.class);

        mockMvc.perform(get("/api/admin/verifications").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        String cardId = upload(userToken, "VERIFICATION");
        mockMvc.perform(post("/api/auth/campus-verification")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dormBuildingId":1,"cardImageFileId":"%s"}
                                """.formatted(cardId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        mockMvc.perform(multipart("/api/files").file(jpeg()).param("purpose", "GOODS")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("NOT_VERIFIED"));

        mockMvc.perform(get("/api/admin/dashboard").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pendingVerifications").value((int) (pendingBefore + 1)))
                .andExpect(jsonPath("$.data.pendingReports").value(0))
                .andExpect(jsonPath("$.data.openDisputes").value(0));

        MvcResult listed = mockMvc.perform(get("/api/admin/verifications")
                        .param("status", "PENDING")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();
        List<Integer> ids = JsonPath.read(
                listed.getResponse().getContentAsString(),
                "$.data.list[?(@.user.username == 'review_user')].id");
        assertThat(ids).hasSize(1);
        int verificationId = ids.get(0);

        mockMvc.perform(get("/api/admin/verifications/" + verificationId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cardImage.fileId").value(cardId))
                .andExpect(jsonPath("$.data.cardImage.url").isNotEmpty())
                .andExpect(jsonPath("$.data.user.emailMasked").value("r***@example.com"))
                .andExpect(jsonPath("$.data.user.studentNoMasked").value("9100****11"))
                .andExpect(jsonPath("$.data.dormBuilding.name").value("学上公寓1"))
                .andExpect(jsonPath("$.data.history").isEmpty());

        mockMvc.perform(get("/api/admin/verifications").param("status", "NOPE")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

        mockMvc.perform(post("/api/admin/verifications/" + verificationId + "/reject")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"短\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

        mockMvc.perform(post("/api/admin/verifications/" + verificationId + "/reject")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"证件照片模糊\",\"status\":\"APPROVED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UNKNOWN_FIELD"));

        mockMvc.perform(post("/api/admin/verifications/" + verificationId + "/reject")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"证件照片模糊\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.user.verifyStatus").value("REJECTED"))
                .andExpect(jsonPath("$.data.history").isEmpty());

        String auditDetail = jdbc.queryForObject(
                "SELECT detail FROM audit_logs WHERE event = 'ADMIN_VERIFY_REJECT' AND user_id = ?",
                String.class,
                userId);
        assertThat(auditDetail).doesNotContain("证件照片模糊").contains("\"userId\":" + userId);

        mockMvc.perform(get("/api/auth/campus-verification").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.rejectReason").value("证件照片模糊"));
        mockMvc.perform(get("/api/me/badges").header("Authorization", "Bearer " + userToken))
                .andExpect(jsonPath("$.data.unreadNotifications").value(1));
        mockMvc.perform(get("/api/notifications").param("unreadOnly", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].type").value("VERIFICATION_REJECTED"))
                .andExpect(jsonPath("$.data.list[0].content").value("证件照片模糊"))
                .andExpect(jsonPath("$.data.list[0].read").value(false))
                .andExpect(jsonPath("$.data.list[0].ref").value(nullValue()));

        String cardId2 = upload(userToken, "VERIFICATION");
        mockMvc.perform(post("/api/auth/campus-verification")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dormBuildingId":1,"cardImageFileId":"%s"}
                                """.formatted(cardId2)))
                .andExpect(status().isOk());
        MvcResult listedAgain = mockMvc.perform(get("/api/admin/verifications")
                        .param("status", "PENDING")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();
        List<Integer> secondIds = JsonPath.read(
                listedAgain.getResponse().getContentAsString(),
                "$.data.list[?(@.user.username == 'review_user')].id");
        assertThat(secondIds).hasSize(1);
        int secondId = secondIds.get(0);

        mockMvc.perform(get("/api/admin/verifications/" + secondId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.history[0].status").value("REJECTED"))
                .andExpect(jsonPath("$.data.history[0].rejectReason").value("证件照片模糊"));

        mockMvc.perform(post("/api/admin/verifications/" + secondId + "/approve")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paid\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UNKNOWN_FIELD"));

        mockMvc.perform(post("/api/admin/verifications/" + secondId + "/approve")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.user.verifyStatus").value("VERIFIED"))
                .andExpect(jsonPath("$.data.reviewedBy").isNumber());

        mockMvc.perform(post("/api/admin/verifications/" + secondId + "/approve")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("VERIFICATION_NOT_PENDING"));

        mockMvc.perform(get("/api/admin/verifications/999999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(multipart("/api/files").file(jpeg()).param("purpose", "GOODS")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fileId").isNotEmpty());

        mockMvc.perform(get("/api/me/badges").header("Authorization", "Bearer " + userToken))
                .andExpect(jsonPath("$.data.unreadNotifications").value(2));
        MvcResult notes = mockMvc.perform(get("/api/notifications").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.list[0].type").value("VERIFICATION_APPROVED"))
                .andReturn();
        int noteId = JsonPath.read(notes.getResponse().getContentAsString(), "$.data.list[0].id");
        mockMvc.perform(post("/api/notifications/read")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\":[" + noteId + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updated").value(1));
        mockMvc.perform(get("/api/me/badges").header("Authorization", "Bearer " + userToken))
                .andExpect(jsonPath("$.data.unreadNotifications").value(1));
        mockMvc.perform(post("/api/notifications/read")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"all\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updated").value(1));
        mockMvc.perform(get("/api/me/badges").header("Authorization", "Bearer " + userToken))
                .andExpect(jsonPath("$.data.unreadNotifications").value(0));
    }

    @Test
    void mustChangePasswordBlocksReview() throws Exception {
        String token = loginAdmin(LOCKED_ADMIN);
        mockMvc.perform(get("/api/admin/verifications").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("PASSWORD_CHANGE_REQUIRED"));
        mockMvc.perform(get("/api/admin/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("PASSWORD_CHANGE_REQUIRED"));
    }

    private String loginUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentNo":"91004411","password":"%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");
    }

    private String loginAdmin(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, PASSWORD)))
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

    private static MockMultipartFile jpeg() throws Exception {
        BufferedImage image = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.BLUE.getRGB());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        return new MockMultipartFile("file", "photo.jpg", "image/jpeg", out.toByteArray());
    }

    private void insertAdmin(String username, String email, boolean mustChangePassword) {
        AdminAccount admin = new AdminAccount();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(PASSWORD));
        admin.setStatus("ACTIVE");
        admin.setMustChangePassword(mustChangePassword);
        admin.setTokenVersion(1);
        adminMapper.insert(admin);
    }

    private long insertUser() {
        UserEntity user = new UserEntity();
        user.setStudentNoHash(hasher.hash("91004411"));
        user.setStudentNoMasked(hasher.mask("91004411"));
        user.setUsername("review_user");
        user.setNickname("review_user");
        user.setEmail(USER_EMAIL);
        user.setPasswordHash(passwordEncoder.encode(PASSWORD));
        user.setStatus("ACTIVE");
        user.setVerifyStatus("UNVERIFIED");
        user.setTokenVersion(1);
        userMapper.insert(user);
        return user.getId();
    }

    private void cleanup() {
        jdbc.update("""
                DELETE n FROM notifications n
                INNER JOIN users u ON u.id = n.user_id
                WHERE u.email = ?
                """, USER_EMAIL);
        jdbc.update("""
                DELETE a FROM audit_logs a
                INNER JOIN users u ON u.id = a.user_id
                WHERE u.email = ?
                """, USER_EMAIL);
        jdbc.update("""
                DELETE a FROM audit_logs a
                INNER JOIN admins ad ON ad.id = a.admin_id
                WHERE ad.username IN (?, ?)
                """, ADMIN, LOCKED_ADMIN);
        jdbc.update("""
                DELETE v FROM campus_verifications v
                INNER JOIN users u ON u.id = v.user_id
                WHERE u.email = ?
                """, USER_EMAIL);
        jdbc.update("""
                DELETE sf FROM stored_files sf
                INNER JOIN users u ON u.id = sf.owner_id
                WHERE u.email = ?
                """, USER_EMAIL);
        jdbc.update("DELETE FROM users WHERE email = ?", USER_EMAIL);
        jdbc.update("DELETE FROM admins WHERE username IN (?, ?)", ADMIN, LOCKED_ADMIN);
    }
}
