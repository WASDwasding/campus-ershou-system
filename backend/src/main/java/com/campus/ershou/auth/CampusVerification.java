package com.campus.ershou.auth;

import java.time.LocalDateTime;

public class CampusVerification {

    private Long id;
    private Long userId;
    private Long dormBuildingId;
    private String cardFileId;
    private String status;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private Long reviewedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDormBuildingId() {
        return dormBuildingId;
    }

    public void setDormBuildingId(Long dormBuildingId) {
        this.dormBuildingId = dormBuildingId;
    }

    public String getCardFileId() {
        return cardFileId;
    }

    public void setCardFileId(String cardFileId) {
        this.cardFileId = cardFileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
