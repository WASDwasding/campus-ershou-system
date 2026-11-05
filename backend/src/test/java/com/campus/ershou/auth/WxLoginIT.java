package com.campus.ershou.auth;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.util.StudentNoHasher;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class WxLoginIT {

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

    @MockitoBean
    private WxSessionClient wxSessionClient;

    @AfterEach
    void cleanup() {
        jdbc.update("DELETE FROM users WHERE username LIKE 'wx%' OR email = 'wx-student@example.com'");
    }

    @Test
    void wxLoginCreatesUserAndNeverReturnsOpenid() throws Exception {
        when(wxSessionClient.exchange("fresh-code")).thenReturn("openid-fresh-1");
        when(wxSessionClient.exchange("used-code")).thenThrow(ApiException.of(ErrorCode.WX_LOGIN_FAILED));

        MvcResult created = mockMvc.perform(post("/api/auth/wx-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"fresh-code\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isNewUser").value(true))
                .andExpect(jsonPath("$.data.user.verifyStatus").value("UNVERIFIED"))
                .andExpect(jsonPath("$.data.user.publishBanned").value(false))
                .andExpect(jsonPath("$.data.user.wxBound").value(true))
                .andExpect(jsonPath("$.data.user.emailMasked").value(nullValue()))
                .andExpect(jsonPath("$.data.user.openid").doesNotExist())
                .andReturn();
        assertThat(created.getResponse().getContentAsString()).doesNotContain("openid-fresh-1");
        String token = JsonPath.read(created.getResponse().getContentAsString(), "$.data.token");

        mockMvc.perform(post("/api/auth/wx-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"fresh-code\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isNewUser").value(false));

        mockMvc.perform(post("/api/auth/wx-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"used-code\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WX_LOGIN_FAILED"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentNo\":\"91008801\",\"password\":\"FlowTest-1\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("BAD_CREDENTIALS"));

        UserEntity student = new UserEntity();
        student.setStudentNoHash(hasher.hash("91008801"));
        student.setStudentNoMasked(hasher.mask("91008801"));
        student.setUsername("wxstudent");
        student.setNickname("wxstudent");
        student.setEmail("wx-student@example.com");
        student.setPasswordHash(passwordEncoder.encode("FlowTest-1"));
        student.setStatus("ACTIVE");
        student.setVerifyStatus("UNVERIFIED");
        student.setTokenVersion(1);
        userMapper.insert(student);
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentNo\":\"91008801\",\"password\":\"FlowTest-1\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String studentToken = JsonPath.read(login.getResponse().getContentAsString(), "$.data.token");

        when(wxSessionClient.exchange("bind-code")).thenReturn("openid-fresh-1");
        mockMvc.perform(post("/api/auth/wx-bind")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"bind-code\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WX_ALREADY_BOUND"));

        when(wxSessionClient.exchange("bind-ok")).thenReturn("openid-student-2");
        mockMvc.perform(post("/api/auth/wx-bind")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"bind-ok\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.wxBound").value(true));
        mockMvc.perform(post("/api/auth/wx-bind")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"bind-ok\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.wxBound").value(true));
        when(wxSessionClient.exchange("bind-other")).thenReturn("openid-student-3");
        mockMvc.perform(post("/api/auth/wx-bind")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"bind-other\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WX_ALREADY_BOUND"));

        mockMvc.perform(post("/api/auth/campus-verification")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"realName\":\"微信同学\",\"studentNo\":\"91008801\",\"college\":\"软件学院\",\"dormBuildingId\":1,\"cardImageFileId\":\"missing\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STUDENT_NO_TAKEN"));
    }

    @Test
    void missingWxConfigIsUnavailable() {
        WxSessionClientImpl client = new WxSessionClientImpl(new AppProperties(), new ObjectMapper());
        assertThatThrownBy(() -> client.exchange("any-code"))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getErrorCode())
                .isEqualTo(ErrorCode.UPSTREAM_UNAVAILABLE);
    }
}
