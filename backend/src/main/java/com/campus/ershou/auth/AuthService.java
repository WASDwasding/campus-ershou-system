package com.campus.ershou.auth;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.auth.dto.AuthTokenResponse;
import com.campus.ershou.auth.dto.LoginRequest;
import com.campus.ershou.auth.dto.RegisterRequest;
import com.campus.ershou.auth.dto.RegisterSentResponse;
import com.campus.ershou.auth.dto.SetPasswordRequest;
import com.campus.ershou.auth.dto.SetupTokenResponse;
import com.campus.ershou.auth.dto.VerifyCodeRequest;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.mail.MailService;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.security.IssuedToken;
import com.campus.ershou.common.security.JwtService;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.common.util.ContactInfo;
import com.campus.ershou.common.util.Masking;
import com.campus.ershou.common.util.Randoms;
import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.common.util.StudentNoHasher;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.campus.ershou.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String DUMMY_BCRYPT = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

    private final UserMapper userMapper;
    private final UserService userService;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final MailService mailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final StudentNoHasher studentNoHasher;
    private final RateLimitService rateLimitService;
    private final AppProperties props;
    private final AuditService auditService;

    public AuthService(
            UserMapper userMapper,
            UserService userService,
            StringRedisTemplate redis,
            ObjectMapper objectMapper,
            MailService mailService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            StudentNoHasher studentNoHasher,
            RateLimitService rateLimitService,
            AppProperties props,
            AuditService auditService
    ) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.mailService = mailService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.studentNoHasher = studentNoHasher;
        this.rateLimitService = rateLimitService;
        this.props = props;
        this.auditService = auditService;
    }

    public RegisterSentResponse register(RegisterRequest request, String clientIp) {
        if (ContactInfo.contains(request.username())) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
        String studentNoHash = studentNoHasher.hash(request.studentNo());
        if (userMapper.findByStudentNoHash(studentNoHash) != null) {
            throw ApiException.of(ErrorCode.STUDENT_NO_TAKEN);
        }
        if (userMapper.findByUsername(request.username()) != null) {
            throw ApiException.of(ErrorCode.USERNAME_TAKEN);
        }
        if (userMapper.findByEmail(request.email()) != null) {
            throw ApiException.of(ErrorCode.EMAIL_TAKEN);
        }

        rateLimitService.check(
                "register-ip",
                clientIp,
                props.getRegister().getIpSendLimitPerMinute(),
                Duration.ofMinutes(1)
        );
        if (!rateLimitService.tryCooldown(RedisKeys.sendCooldown(request.email()), props.getRegister().getSendCooldown())) {
            throw ApiException.rateLimited(props.getRegister().getSendCooldown().getSeconds());
        }

        String code = Randoms.digits(6);
        PendingRegister pending = new PendingRegister();
        pending.setStudentNo(request.studentNo());
        pending.setUsername(request.username());
        pending.setEmail(request.email());
        pending.setCodeHash(sha256(code));
        pending.setAttempts(0);
        redis.opsForValue().set(RedisKeys.pendingRegister(request.email()), toJson(pending), props.getRegister().getCodeTtl());
        mailService.sendVerifyCode(request.email(), code);

        String debugCode = null;
        if (!props.getMail().isEnabled() && props.getMail().isExposeCode()) {
            debugCode = code;
        }
        return new RegisterSentResponse(
                Masking.email(request.email()),
                (int) props.getRegister().getCodeTtl().toSeconds(),
                debugCode
        );
    }

    public SetupTokenResponse verifyCode(VerifyCodeRequest request) {
        String raw = redis.opsForValue().get(RedisKeys.pendingRegister(request.email()));
        if (raw == null) {
            throw ApiException.of(ErrorCode.CODE_EXPIRED);
        }
        PendingRegister pending = fromJson(raw, PendingRegister.class, ErrorCode.CODE_EXPIRED);
        pending.setAttempts(pending.getAttempts() + 1);
        if (pending.getAttempts() > props.getRegister().getCodeAttempts()) {
            redis.delete(RedisKeys.pendingRegister(request.email()));
            throw ApiException.of(ErrorCode.CODE_EXPIRED);
        }
        if (!sha256Equals(pending.getCodeHash(), request.code())) {
            Long ttl = redis.getExpire(RedisKeys.pendingRegister(request.email()), TimeUnit.SECONDS);
            Duration remain = ttl != null && ttl > 0 ? Duration.ofSeconds(ttl) : props.getRegister().getCodeTtl();
            redis.opsForValue().set(RedisKeys.pendingRegister(request.email()), toJson(pending), remain);
            throw ApiException.of(ErrorCode.INVALID_CODE);
        }
        redis.delete(RedisKeys.pendingRegister(request.email()));
        String setupToken = Randoms.hex(32);
        VerifiedRegister verified = new VerifiedRegister(pending.getStudentNo(), pending.getUsername(), pending.getEmail());
        redis.opsForValue().set(RedisKeys.setupToken(setupToken), toJson(verified), props.getRegister().getSetupTokenTtl());
        return new SetupTokenResponse(setupToken, (int) props.getRegister().getSetupTokenTtl().toSeconds());
    }

    @Transactional
    public AuthTokenResponse setPassword(SetPasswordRequest request) {
        String raw = redis.opsForValue().get(RedisKeys.setupToken(request.setupToken()));
        if (raw == null) {
            throw ApiException.of(ErrorCode.SETUP_TOKEN_INVALID);
        }
        VerifiedRegister verified = fromJson(raw, VerifiedRegister.class, ErrorCode.SETUP_TOKEN_INVALID);
        if (ContactInfo.contains(verified.getUsername())) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
        String studentNoHash = studentNoHasher.hash(verified.getStudentNo());
        if (userMapper.findByStudentNoHash(studentNoHash) != null) {
            throw ApiException.of(ErrorCode.STUDENT_NO_TAKEN);
        }
        if (userMapper.findByUsername(verified.getUsername()) != null) {
            throw ApiException.of(ErrorCode.USERNAME_TAKEN);
        }
        if (userMapper.findByEmail(verified.getEmail()) != null) {
            throw ApiException.of(ErrorCode.EMAIL_TAKEN);
        }

        UserEntity user = new UserEntity();
        user.setStudentNoHash(studentNoHash);
        user.setStudentNoMasked(studentNoHasher.mask(verified.getStudentNo()));
        user.setUsername(verified.getUsername());
        user.setNickname(verified.getUsername());
        user.setEmail(verified.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus("ACTIVE");
        user.setVerifyStatus("UNVERIFIED");
        user.setTokenVersion(1);
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException ex) {
            throw mapDuplicate(ex);
        }
        redis.delete(RedisKeys.setupToken(request.setupToken()));
        IssuedToken issued = jwtService.issueUser(user.getId(), user.getTokenVersion());
        UserEntity created = userMapper.findById(user.getId());
        return new AuthTokenResponse(issued.token(), issued.expiresAt(), true, userService.toMe(created));
    }

    public AuthTokenResponse devLogin(String devOpenid, String host, String ip) {
        boolean localHost = "127.0.0.1".equals(host) || "localhost".equalsIgnoreCase(host) || "::1".equals(host);
        if (!props.getDevLogin().isEnabled() || !localHost) {
            auditService.record("SECURITY", "DEV_LOGIN_BLOCKED", null, null, ip, "{}");
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (devOpenid == null || !devOpenid.matches("dev-[a-z0-9-]{3,32}")) {
            throw ApiException.validation(List.of(new com.campus.ershou.common.api.FieldErrorItem("devOpenid", "格式应为 dev- 加 3–32 位小写字母、数字或连字符")));
        }
        UserEntity user = userMapper.findByDevOpenid(devOpenid);
        boolean createdNow = false;
        if (user == null) {
            user = new UserEntity();
            user.setStudentNoHash(studentNoHasher.hash("dev:" + devOpenid));
            user.setStudentNoMasked("dev");
            String compact = ("d" + devOpenid.substring(4).replace("-", ""));
            if (compact.length() > 16) {
                compact = compact.substring(0, 16);
            }
            String username = compact;
            int suffix = 0;
            while (userMapper.findByUsername(username) != null && suffix < 50) {
                String tail = Integer.toString(suffix++);
                username = compact.substring(0, Math.min(compact.length(), 16 - tail.length())) + tail;
            }
            user.setUsername(username);
            user.setNickname(username);
            user.setEmail(devOpenid + "@dev.local");
            user.setPasswordHash(passwordEncoder.encode(Randoms.hex(16)));
            user.setStatus("ACTIVE");
            user.setVerifyStatus("VERIFIED");
            user.setTokenVersion(1);
            user.setDevOpenid(devOpenid);
            try {
                userMapper.insertDev(user);
            } catch (DuplicateKeyException ex) {
                user = userMapper.findByDevOpenid(devOpenid);
                if (user == null) {
                    throw mapDuplicate(ex);
                }
            }
            createdNow = user.getId() != null;
        }
        if ("BANNED".equals(user.getStatus())) {
            throw ApiException.of(ErrorCode.USER_BANNED);
        }
        IssuedToken issued = jwtService.issueUser(user.getId(), user.getTokenVersion());
        return new AuthTokenResponse(issued.token(), issued.expiresAt(), createdNow, userService.toMe(userMapper.findById(user.getId())));
    }

    public AuthTokenResponse login(LoginRequest request) {
        String studentNoHash = studentNoHasher.hash(request.studentNo());
        assertLoginNotLocked(studentNoHash);
        UserEntity user = userMapper.findByStudentNoHash(studentNoHash);
        boolean matches = passwordEncoder.matches(request.password(), user == null ? DUMMY_BCRYPT : user.getPasswordHash());
        if (user == null || !matches) {
            recordLoginFailure(studentNoHash);
            throw ApiException.of(ErrorCode.BAD_CREDENTIALS);
        }
        if ("BANNED".equals(user.getStatus())) {
            throw ApiException.of(ErrorCode.USER_BANNED, bannedData(user));
        }
        redis.delete(List.of(RedisKeys.loginFail(studentNoHash), RedisKeys.loginLock(studentNoHash)));
        IssuedToken issued = jwtService.issueUser(user.getId(), user.getTokenVersion());
        return new AuthTokenResponse(issued.token(), issued.expiresAt(), false, userService.toMe(user));
    }

    public void logout(LoginUser loginUser) {
        userMapper.bumpTokenVersion(loginUser.userId());
        Duration ttl = Duration.between(Instant.now(), loginUser.expiresAt().toInstant());
        if (!ttl.isNegative() && !ttl.isZero()) {
            redis.opsForValue().set(RedisKeys.jwtDeny(loginUser.jti()), "1", ttl);
        }
        redis.delete(RedisKeys.tokenVer(loginUser.userId()));
        log.info("user logout userId={}", loginUser.userId());
    }

    private void assertLoginNotLocked(String studentNoHash) {
        Boolean locked = redis.hasKey(RedisKeys.loginLock(studentNoHash));
        if (Boolean.TRUE.equals(locked)) {
            Long ttl = redis.getExpire(RedisKeys.loginLock(studentNoHash), TimeUnit.SECONDS);
            long retryAfter = ttl != null && ttl > 0 ? ttl : props.getLogin().getLockTtl().toSeconds();
            throw ApiException.loginLocked(retryAfter);
        }
    }

    private void recordLoginFailure(String studentNoHash) {
        String failKey = RedisKeys.loginFail(studentNoHash);
        Long count = redis.opsForValue().increment(failKey);
        if (count != null && count == 1L) {
            redis.expire(failKey, props.getLogin().getFailWindow());
        }
        if (count != null && count >= props.getLogin().getFailLimit()) {
            redis.opsForValue().set(RedisKeys.loginLock(studentNoHash), "1", props.getLogin().getLockTtl());
        }
    }

    private static Map<String, Object> bannedData(UserEntity user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("reason", user.getBanReason() == null ? "账号已被封禁" : user.getBanReason());
        data.put("until", user.getBanUntil() == null ? null : user.getBanUntil().atOffset(java.time.ZoneOffset.ofHours(8)));
        return data;
    }

    private static ApiException mapDuplicate(DuplicateKeyException ex) {
        String message = String.valueOf(ex.getMessage());
        if (message.contains("uk_users_student_no_hash")) {
            return ApiException.of(ErrorCode.STUDENT_NO_TAKEN);
        }
        if (message.contains("uk_users_username")) {
            return ApiException.of(ErrorCode.USERNAME_TAKEN);
        }
        if (message.contains("uk_users_email")) {
            return ApiException.of(ErrorCode.EMAIL_TAKEN);
        }
        return ApiException.of(ErrorCode.CONFLICT);
    }

    static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean sha256Equals(String expectedHex, String code) {
        byte[] expected = expectedHex.getBytes(StandardCharsets.UTF_8);
        byte[] actual = sha256(code).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, actual);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("无法序列化注册会话", e);
        }
    }

    private <T> T fromJson(String json, Class<T> type, ErrorCode errorCode) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw ApiException.of(errorCode);
        }
    }
}
