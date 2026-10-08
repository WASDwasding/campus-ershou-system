package com.campus.ershou.admin;

import com.campus.ershou.admin.dto.AdminLoginRequest;
import com.campus.ershou.admin.dto.AdminPasswordRequest;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.security.IssuedToken;
import com.campus.ershou.common.security.JwtService;
import com.campus.ershou.common.security.LoginAdmin;
import com.campus.ershou.common.util.Masking;
import com.campus.ershou.common.util.RedisKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AdminAuthService {

    private static final String DUMMY_BCRYPT = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

    private final AdminMapper adminMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redis;
    private final AppProperties props;

    public AdminAuthService(
            AdminMapper adminMapper,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            StringRedisTemplate redis,
            AppProperties props
    ) {
        this.adminMapper = adminMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.redis = redis;
        this.props = props;
    }

    public Map<String, Object> login(AdminLoginRequest request) {
        String username = request.username().toLowerCase(Locale.ROOT);
        assertNotLocked(username);
        AdminAccount admin = adminMapper.findByUsername(request.username());
        boolean matches = passwordEncoder.matches(
                request.password(),
                admin == null ? DUMMY_BCRYPT : admin.getPasswordHash()
        );
        if (admin == null || !matches || !"ACTIVE".equals(admin.getStatus())) {
            recordFailure(username);
            throw ApiException.of(ErrorCode.BAD_CREDENTIALS);
        }
        redis.delete(java.util.List.of(RedisKeys.adminLoginFail(username), RedisKeys.adminLoginLock(username)));
        IssuedToken issued = jwtService.issueAdmin(admin.getId(), admin.getTokenVersion());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", issued.token());
        data.put("expiresAt", issued.expiresAt());
        data.put("mustChangePassword", Boolean.TRUE.equals(admin.getMustChangePassword()));
        data.put("admin", profile(admin));
        return data;
    }

    public void logout(LoginAdmin loginAdmin) {
        adminMapper.bumpTokenVersion(loginAdmin.adminId());
        Duration ttl = Duration.between(Instant.now(), loginAdmin.expiresAt().toInstant());
        if (!ttl.isNegative() && !ttl.isZero()) {
            redis.opsForValue().set(RedisKeys.jwtDeny(loginAdmin.jti()), "1", ttl);
        }
    }

    public Map<String, Object> me(LoginAdmin loginAdmin) {
        AdminAccount admin = requireActive(loginAdmin.adminId());
        Map<String, Object> data = profile(admin);
        data.put("mustChangePassword", Boolean.TRUE.equals(admin.getMustChangePassword()));
        return data;
    }

    public void changePassword(LoginAdmin loginAdmin, AdminPasswordRequest request) {
        AdminAccount admin = requireActive(loginAdmin.adminId());
        if (!passwordEncoder.matches(request.oldPassword(), admin.getPasswordHash())) {
            throw ApiException.of(ErrorCode.BAD_CREDENTIALS);
        }
        admin.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        adminMapper.updatePassword(admin);
    }

    private AdminAccount requireActive(long adminId) {
        AdminAccount admin = adminMapper.findById(adminId);
        if (admin == null || !"ACTIVE".equals(admin.getStatus())) {
            throw ApiException.of(ErrorCode.UNAUTHORIZED);
        }
        return admin;
    }

    private static Map<String, Object> profile(AdminAccount admin) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("emailMasked", Masking.email(admin.getEmail()));
        return data;
    }

    private void assertNotLocked(String username) {
        if (Boolean.TRUE.equals(redis.hasKey(RedisKeys.adminLoginLock(username)))) {
            Long ttl = redis.getExpire(RedisKeys.adminLoginLock(username), TimeUnit.SECONDS);
            long retryAfter = ttl != null && ttl > 0 ? ttl : props.getLogin().getLockTtl().toSeconds();
            throw ApiException.loginLocked(retryAfter);
        }
    }

    private void recordFailure(String username) {
        String failKey = RedisKeys.adminLoginFail(username);
        Long count = redis.opsForValue().increment(failKey);
        if (count != null && count == 1L) {
            redis.expire(failKey, props.getLogin().getFailWindow());
        }
        if (count != null && count >= props.getLogin().getFailLimit()) {
            redis.opsForValue().set(RedisKeys.adminLoginLock(username), "1", props.getLogin().getLockTtl());
        }
    }
}
