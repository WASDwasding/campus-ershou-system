package com.campus.ershou.admin;

import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogMapper auditLogMapper;

    public AuditService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    public void verificationReviewed(String event, long adminId, long userId, long verificationId, String ip) {
        record("ADMIN", event, adminId, userId, ip,
                "{\"verificationId\":" + verificationId + ",\"userId\":" + userId + "}");
    }

    public void record(String category, String event, Long adminId, Long userId, String ip, String detail) {
        String safe = detail == null ? "{}" : detail;
        if (safe.length() > 500) {
            safe = safe.substring(0, 500);
        }
        auditLogMapper.insert(category, event, adminId, userId, ip, safe);
    }
}
