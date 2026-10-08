package com.campus.ershou.admin;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AdminAuthIT {

    private static final String USERNAME = "admin_it";
    private static final String EMAIL = "admin-it@example.com";
    private static final String PASSWORD = "AdminTest-1234";

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

    @BeforeEach
    void setUp() {
        cleanup();
        AdminAccount admin = new AdminAccount();
        admin.setUsername(USERNAME);
        admin.setEmail(EMAIL);
        admin.setPasswordHash(passwordEncoder.encode(PASSWORD));
        admin.setStatus("ACTIVE");
        admin.setMustChangePassword(false);
        admin.setTokenVersion(1);
        adminMapper.insert(admin);

        UserEntity user = new UserEntity();
        user.setStudentNoHash(hasher.hash("91002201"));
        user.setStudentNoMasked("9100****01");
        user.setUsername("user_admin_it");
        user.setNickname("user_admin_it");
        user.setEmail("user-for-admin-it@example.com");
        user.setPasswordHash(passwordEncoder.encode(PASSWORD));
        user.setStatus("ACTIVE");
        user.setVerifyStatus("UNVERIFIED");
        user.setTokenVersion(1);
        userMapper.insert(user);
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    void adminLoginIsSeparateFromUserLogin() throws Exception {
        MvcResult loggedIn = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mustChangePassword").value(false))
                .andExpect(jsonPath("$.data.admin.username").value(USERNAME))
                .andExpect(jsonPath("$.data.admin.emailMasked").value("a***@example.com"))
                .andReturn();
        String adminToken = JsonPath.read(loggedIn.getResponse().getContentAsString(), "$.data.token");

        mockMvc.perform(get("/api/admin/me").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(USERNAME));
        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());

        MvcResult userLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentNo":"91002201","password":"%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        String userToken = JsonPath.read(userLogin.getResponse().getContentAsString(), "$.data.token");
        mockMvc.perform(get("/api/admin/me").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"wrong-password"}
                                """.formatted(USERNAME)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("BAD_CREDENTIALS"));

        mockMvc.perform(post("/api/admin/auth/logout").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/me").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isUnauthorized());
    }

    private void cleanup() {
        jdbc.update("DELETE FROM admins WHERE username = ?", USERNAME);
        jdbc.update("DELETE FROM users WHERE email = ? OR username = ?", "user-for-admin-it@example.com", "user_admin_it");
    }
}
