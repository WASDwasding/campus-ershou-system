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
    private String goodsDescription;
    private Long goodsCategoryId;
    private String goodsCategoryName;
    private String goodsCondition;
    private Integer goodsPrice;
    private String goodsTradeNote;
    private Long goodsMeetupBuildingId;
    private String goodsMeetupBuildingName;
    private String goodsAliasesJson;
    private String goodsImagesJson;
    private String goodsCoverFileId;
    private Long conversationId;
    private Long meetupBuildingId;
    private String meetupPlace;
    private LocalDateTime meetupTime;
    private LocalDateTime buyerHandoverAt;
    private LocalDateTime sellerHandoverAt;
    private Long buyerLastRead;
    private Long sellerLastRead;
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
    public String getGoodsDescription() { return goodsDescription; }
    public void setGoodsDescription(String goodsDescription) { this.goodsDescription = goodsDescription; }
    public Long getGoodsCategoryId() { return goodsCategoryId; }
    public void setGoodsCategoryId(Long goodsCategoryId) { this.goodsCategoryId = goodsCategoryId; }
    public String getGoodsCategoryName() { return goodsCategoryName; }
    public void setGoodsCategoryName(String goodsCategoryName) { this.goodsCategoryName = goodsCategoryName; }
    public String getGoodsCondition() { return goodsCondition; }
    public void setGoodsCondition(String goodsCondition) { this.goodsCondition = goodsCondition; }
    public Integer getGoodsPrice() { return goodsPrice; }
    public void setGoodsPrice(Integer goodsPrice) { this.goodsPrice = goodsPrice; }
    public String getGoodsTradeNote() { return goodsTradeNote; }
    public void setGoodsTradeNote(String goodsTradeNote) { this.goodsTradeNote = goodsTradeNote; }
    public Long getGoodsMeetupBuildingId() { return goodsMeetupBuildingId; }
    public void setGoodsMeetupBuildingId(Long goodsMeetupBuildingId) { this.goodsMeetupBuildingId = goodsMeetupBuildingId; }
    public String getGoodsMeetupBuildingName() { return goodsMeetupBuildingName; }
    public void setGoodsMeetupBuildingName(String goodsMeetupBuildingName) { this.goodsMeetupBuildingName = goodsMeetupBuildingName; }
    public String getGoodsAliasesJson() { return goodsAliasesJson; }
    public void setGoodsAliasesJson(String goodsAliasesJson) { this.goodsAliasesJson = goodsAliasesJson; }
    public String getGoodsImagesJson() { return goodsImagesJson; }
    public void setGoodsImagesJson(String goodsImagesJson) { this.goodsImagesJson = goodsImagesJson; }
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
    public Long getBuyerLastRead() { return buyerLastRead; }
    public void setBuyerLastRead(Long buyerLastRead) { this.buyerLastRead = buyerLastRead; }
    public Long getSellerLastRead() { return sellerLastRead; }
    public void setSellerLastRead(Long sellerLastRead) { this.sellerLastRead = sellerLastRead; }
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
