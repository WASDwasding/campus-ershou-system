package com.campus.ershou.auth;

import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.mail.MailService;
import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.common.util.StudentNoHasher;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AuthFlowIT {

    private static final String EMAIL = "auth-flow-it@example.com";
    private static final String STUDENT_NO = "90001122";
    private static final String USERNAME = "auth_flow_it";
    private static final String PASSWORD = "FlowTest-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private StudentNoHasher hasher;

    @BeforeEach
    void clean() {
        cleanupUser();
        redis.delete(RedisKeys.sendCooldown(EMAIL));
        redis.delete(RedisKeys.pendingRegister(EMAIL));
        String studentNoHash = hasher.hash(STUDENT_NO);
        redis.delete(RedisKeys.loginFail(studentNoHash));
        redis.delete(RedisKeys.loginLock(studentNoHash));
        CapturingMailConfig.LAST_CODE.set(null);
    }

    @AfterEach
    void tearDown() {
        cleanupUser();
        redis.delete(RedisKeys.sendCooldown(EMAIL));
        redis.delete(RedisKeys.pendingRegister(EMAIL));
    }

    @Test
    void registerVerifySetPasswordLoginAndLogout() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentNo":"%s","username":"%s","email":"%s"}
                                """.formatted(STUDENT_NO, USERNAME, EMAIL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.emailMasked").value("a***@example.com"))
                .andExpect(jsonPath("$.data.expiresInSeconds").value(600))
                .andExpect(jsonPath("$.data.debugCode").doesNotExist());

        String capturedCode = CapturingMailConfig.LAST_CODE.get();
        assertThat(capturedCode).matches("\\d{6}");
        String wrongCode = capturedCode.equals("000000") ? "111111" : "000000";

        mockMvc.perform(post("/api/auth/register/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","code":"%s"}
                                """.formatted(EMAIL, wrongCode)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_CODE"));

        MvcResult verified = mockMvc.perform(post("/api/auth/register/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","code":"%s"}
                                """.formatted(EMAIL, capturedCode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.expiresInSeconds").value(900))
                .andReturn();
        String setupToken = JsonPath.read(verified.getResponse().getContentAsString(), "$.data.setupToken");
        assertThat(setupToken).isNotBlank();

        MvcResult created = mockMvc.perform(post("/api/auth/register/set-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"setupToken":"%s","password":"%s"}
                                """.formatted(setupToken, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isNewUser").value(true))
                .andExpect(jsonPath("$.data.user.username").value(USERNAME))
                .andExpect(jsonPath("$.data.user.nickname").value(USERNAME))
                .andExpect(jsonPath("$.data.user.emailMasked").value("a***@example.com"))
                .andExpect(jsonPath("$.data.user.studentNoMasked").value("9000****22"))
                .andExpect(jsonPath("$.data.user.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.user.verifyStatus").value("UNVERIFIED"))
                .andReturn();
        String firstToken = JsonPath.read(created.getResponse().getContentAsString(), "$.data.token");

        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + firstToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(USERNAME));

        MvcResult loggedIn = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentNo":"%s","password":"%s"}
                                """.formatted(STUDENT_NO, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isNewUser").value(false))
                .andExpect(jsonPath("$.data.user.username").value(USERNAME))
                .andReturn();
        String loginToken = JsonPath.read(loggedIn.getResponse().getContentAsString(), "$.data.token");

        mockMvc.perform(post("/api/auth/logout").header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"));

        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + firstToken))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentNo":"%s","password":"wrong-password"}
                                """.formatted(STUDENT_NO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("BAD_CREDENTIALS"));
    }

    private void cleanupUser() {
        jdbc.update("DELETE FROM users WHERE email = ? OR username = ? OR student_no_hash = ?",
                EMAIL, USERNAME, hasher.hash(STUDENT_NO));
    }

    @TestConfiguration
    static class CapturingMailConfig {

        static final AtomicReference<String> LAST_CODE = new AtomicReference<>();

        @Bean
        @Primary
        MailService capturingMailService(AppProperties props) {
            ObjectProvider<JavaMailSender> unused = new DefaultListableBeanFactory().getBeanProvider(JavaMailSender.class);
            return new MailService(props, unused) {
                @Override
                public void sendVerifyCode(String to, String code) {
                    LAST_CODE.set(code);
                }
            };
        }
    }
}
