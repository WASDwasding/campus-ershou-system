package com.campus.ershou.im;

import java.time.LocalDateTime;

public class ConversationRecord {
    private Long id;
    private Long goodsId;
    private Long buyerId;
    private Long sellerId;
    private Long buyerLastRead;
    private Long sellerLastRead;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public Long getBuyerLastRead() { return buyerLastRead; }
    public void setBuyerLastRead(Long buyerLastRead) { this.buyerLastRead = buyerLastRead; }
    public Long getSellerLastRead() { return sellerLastRead; }
    public void setSellerLastRead(Long sellerLastRead) { this.sellerLastRead = sellerLastRead; }
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
