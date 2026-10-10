package com.campus.ershou.order;

import java.time.LocalDateTime;

public class OrderRecord {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private Long goodsId;
    private String status;
    private Integer amount;
    private Integer quantity;
    private String goodsTitle;
    private String goodsCoverFileId;
    private Long conversationId;
    private Long meetupBuildingId;
    private String meetupPlace;
    private LocalDateTime meetupTime;
    private LocalDateTime buyerHandoverAt;
    private LocalDateTime sellerHandoverAt;
    private String cancelBy;
    private String cancelReason;
    private LocalDateTime expireAt;
    private LocalDateTime paidAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getGoodsTitle() { return goodsTitle; }
    public void setGoodsTitle(String goodsTitle) { this.goodsTitle = goodsTitle; }
    public String getGoodsCoverFileId() { return goodsCoverFileId; }
    public void setGoodsCoverFileId(String goodsCoverFileId) { this.goodsCoverFileId = goodsCoverFileId; }
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    public Long getMeetupBuildingId() { return meetupBuildingId; }
    public void setMeetupBuildingId(Long meetupBuildingId) { this.meetupBuildingId = meetupBuildingId; }
    public String getMeetupPlace() { return meetupPlace; }
    public void setMeetupPlace(String meetupPlace) { this.meetupPlace = meetupPlace; }
    public LocalDateTime getMeetupTime() { return meetupTime; }
    public void setMeetupTime(LocalDateTime meetupTime) { this.meetupTime = meetupTime; }
    public LocalDateTime getBuyerHandoverAt() { return buyerHandoverAt; }
    public void setBuyerHandoverAt(LocalDateTime buyerHandoverAt) { this.buyerHandoverAt = buyerHandoverAt; }
    public LocalDateTime getSellerHandoverAt() { return sellerHandoverAt; }
    public void setSellerHandoverAt(LocalDateTime sellerHandoverAt) { this.sellerHandoverAt = sellerHandoverAt; }
    public String getCancelBy() { return cancelBy; }
    public void setCancelBy(String cancelBy) { this.cancelBy = cancelBy; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public LocalDateTime getExpireAt() { return expireAt; }
    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
