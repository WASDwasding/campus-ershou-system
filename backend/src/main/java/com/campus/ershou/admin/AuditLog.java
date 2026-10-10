package com.campus.ershou.admin;

import java.time.LocalDateTime;

public class AuditLog {
    private Long id;
    private String category;
    private String event;
    private Long adminId;
    private Long userId;
    private String ip;
    private String detail;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
