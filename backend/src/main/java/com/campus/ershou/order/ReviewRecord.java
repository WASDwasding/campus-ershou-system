package com.campus.ershou.order;

import java.time.LocalDateTime;

public class ReviewRecord {
    private Long id;
    private Long orderId;
    private Long goodsId;
    private Long reviewerId;
    private Long revieweeId;
    private Integer score;
    private String content;
    private String reviewerNickname;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public Long getRevieweeId() { return revieweeId; }
    public void setRevieweeId(Long revieweeId) { this.revieweeId = revieweeId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getReviewerNickname() { return reviewerNickname; }
    public void setReviewerNickname(String reviewerNickname) { this.reviewerNickname = reviewerNickname; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
