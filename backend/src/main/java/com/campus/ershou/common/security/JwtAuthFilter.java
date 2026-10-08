package com.campus.ershou.common.security;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.ErrorResponseWriter;
import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.admin.AdminAccount;
import com.campus.ershou.admin.AdminMapper;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final StringRedisTemplate redis;
    private final ErrorResponseWriter errorWriter;

    public JwtAuthFilter(
            JwtService jwtService,
            UserMapper userMapper,
            AdminMapper adminMapper,
            StringRedisTemplate redis,
            ErrorResponseWriter errorWriter
    ) {
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.adminMapper = adminMapper;
        this.redis = redis;
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isBlank() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Object principal = jwtService.parsePrincipal(header.substring(7).trim());
            if (principal instanceof LoginUser loginUser) {
                authenticateUser(loginUser, request, response, filterChain);
                return;
            }
            if (principal instanceof LoginAdmin loginAdmin) {
                authenticateAdmin(loginAdmin, request, response, filterChain);
                return;
            }
            filterChain.doFilter(request, response);
        } catch (ApiException ex) {
            filterChain.doFilter(request, response);
        }
    }

    private void authenticateUser(
            LoginUser loginUser,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (Boolean.TRUE.equals(redis.hasKey(RedisKeys.jwtDeny(loginUser.jti())))) {
            filterChain.doFilter(request, response);
            return;
        }
        UserEntity user = userMapper.findById(loginUser.userId());
        if (user == null || user.getTokenVersion() != loginUser.tokenVersion()) {
            filterChain.doFilter(request, response);
            return;
        }
        if ("BANNED".equals(user.getStatus())) {
            errorWriter.write(response, ErrorCode.USER_BANNED, bannedData(user));
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                loginUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ));
        filterChain.doFilter(request, response);
    }

    private void authenticateAdmin(
            LoginAdmin loginAdmin,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (Boolean.TRUE.equals(redis.hasKey(RedisKeys.jwtDeny(loginAdmin.jti())))) {
            filterChain.doFilter(request, response);
            return;
        }
        AdminAccount admin = adminMapper.findById(loginAdmin.adminId());
        if (admin == null || admin.getTokenVersion() != loginAdmin.tokenVersion() || !"ACTIVE".equals(admin.getStatus())) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                loginAdmin,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ));
        filterChain.doFilter(request, response);
    }

    private static Map<String, Object> bannedData(UserEntity user) {
        OffsetDateTime until = user.getBanUntil() == null
                ? null
                : user.getBanUntil().atOffset(ZoneOffset.ofHours(8));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("reason", user.getBanReason() == null ? "账号已被封禁" : user.getBanReason());
        data.put("until", until);
        return data;
    }
}
