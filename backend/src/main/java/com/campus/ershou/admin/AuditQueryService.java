package com.campus.ershou.admin;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.common.util.Times;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AuditQueryService {

    private static final Set<String> CATEGORIES = Set.of("SECURITY", "BUSINESS", "ADMIN");

    private final AuditLogMapper auditLogMapper;

    public AuditQueryService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    public Map<String, Object> list(String category, String event, Long userId, String ip, String from, String to, Integer page, Integer size) {
        String normalized = null;
        if (category != null && !category.isBlank()) {
            normalized = category.trim().toUpperCase();
            if (!CATEGORIES.contains(normalized)) {
                throw ApiException.validation(List.of(new FieldErrorItem("category", "类别不合法")));
            }
        }
        int[] paging = Paging.page(page, size);
        LocalDateTime fromTime = parse(from, "from");
        LocalDateTime toTime = parse(to, "to");
        String eventValue = event == null || event.isBlank() ? null : event.trim();
        String ipValue = ip == null || ip.isBlank() ? null : ip.trim();
        List<Map<String, Object>> list = new ArrayList<>();
        for (AuditLog row : auditLogMapper.search(normalized, eventValue, userId, ipValue, fromTime, toTime, (paging[0] - 1) * paging[1], paging[1])) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", row.getId());
            item.put("category", row.getCategory());
            item.put("event", row.getEvent());
            item.put("adminId", row.getAdminId());
            item.put("userId", row.getUserId());
            item.put("ip", row.getIp());
            item.put("detail", row.getDetail());
            item.put("createdAt", Times.at(row.getCreatedAt()));
            list.add(item);
        }
        return Paging.of(list, paging[0], paging[1], auditLogMapper.count(normalized, eventValue, userId, ipValue, fromTime, toTime));
    }

    private static LocalDateTime parse(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Times.toShanghai(OffsetDateTime.parse(value.trim()));
        } catch (DateTimeParseException ex) {
            throw ApiException.validation(List.of(new FieldErrorItem(field, "时间格式应为 2026-10-10T08:00:00+08:00")));
        }
    }
}
