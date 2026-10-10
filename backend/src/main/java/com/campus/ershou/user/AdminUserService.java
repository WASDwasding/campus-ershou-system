package com.campus.ershou.user;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.goods.GoodsMapper;
import com.campus.ershou.order.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdminUserService {

    private static final Set<String> STATUSES = Set.of("ACTIVE", "BANNED");
    private static final Set<String> VERIFY = Set.of("UNVERIFIED", "PENDING", "VERIFIED", "REJECTED");

    private final UserMapper userMapper;
    private final GoodsMapper goodsMapper;
    private final OrderService orderService;
    private final AuditService auditService;

    public AdminUserService(UserMapper userMapper, GoodsMapper goodsMapper, OrderService orderService, AuditService auditService) {
        this.userMapper = userMapper;
        this.goodsMapper = goodsMapper;
        this.orderService = orderService;
        this.auditService = auditService;
    }

    public Map<String, Object> list(String keyword, String status, String verifyStatus, Integer page, Integer size) {
        String normalizedStatus = normalize(status, STATUSES, "status");
        String normalizedVerify = normalize(verifyStatus, VERIFY, "verifyStatus");
        int[] paging = Paging.page(page, size);
        String like = like(keyword);
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserEntity user : userMapper.adminSearch(like, normalizedStatus, normalizedVerify, (paging[0] - 1) * paging[1], paging[1])) {
            list.add(toView(user));
        }
        return Paging.of(list, paging[0], paging[1], userMapper.adminCount(like, normalizedStatus, normalizedVerify));
    }

    public Map<String, Object> detail(long userId) {
        UserEntity user = userMapper.findById(userId);
        if (user == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        return toView(user);
    }

    public Map<String, Object> ban(long adminId, long userId, String reason, Integer days, String ip) {
        String text = reason == null ? "" : reason.trim();
        int length = text.codePointCount(0, text.length());
        if (length < 2 || length > 100) {
            throw ApiException.validation(List.of(new FieldErrorItem("reason", "原因 2–100 字")));
        }
        if (days != null && (days < 1 || days > 3650)) {
            throw ApiException.validation(List.of(new FieldErrorItem("days", "天数 1–3650，空为永久")));
        }
        UserEntity user = userMapper.findById(userId);
        if (user == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        LocalDateTime until = days == null ? null : LocalDateTime.now(Times.SHANGHAI).plusDays(days);
        if (userMapper.ban(userId, text, until) != 1) {
            throw ApiException.of(ErrorCode.CONFLICT);
        }
        orderService.cancelPendingOf(userId, "账号已封禁");
        goodsMapper.offShelfOnSaleBySeller(userId);
        auditService.record("ADMIN", "ADMIN_USER_BAN", adminId, userId, ip,
                "{\"userId\":" + userId + ",\"days\":" + (days == null ? "null" : days) + "}");
        return detail(userId);
    }

    public Map<String, Object> unban(long adminId, long userId, String ip) {
        if (userMapper.findById(userId) == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (userMapper.unban(userId) != 1) {
            throw ApiException.of(ErrorCode.CONFLICT);
        }
        auditService.record("ADMIN", "ADMIN_USER_UNBAN", adminId, userId, ip, "{\"userId\":" + userId + "}");
        return detail(userId);
    }

    private Map<String, Object> toView(UserEntity user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("email", user.getEmail());
        data.put("studentNoMasked", user.getStudentNoMasked());
        data.put("status", user.getStatus());
        data.put("verifyStatus", user.getVerifyStatus());
        data.put("banReason", user.getBanReason());
        data.put("banUntil", Times.at(user.getBanUntil()));
        data.put("createdAt", Times.at(user.getCreatedAt()));
        return data;
    }

    private static String normalize(String value, Set<String> allowed, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        if (!allowed.contains(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem(field, "取值不合法")));
        }
        return normalized;
    }

    private static String like(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return "%" + keyword.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }
}
