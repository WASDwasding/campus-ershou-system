package com.campus.ershou.pay;

import java.time.LocalDateTime;

public class PaymentRecord {
    private Long id;
    private String paymentNo;
    private Long orderId;
    private String orderNo;
    private Long buyerId;
    private Integer amount;
    private String channel;
    private String status;
    private String gatewayTradeNo;
    private String gatewayResult;
    private Integer notifyAttempts;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaymentNo() { return paymentNo; }
    public void setPaymentNo(String paymentNo) { this.paymentNo = paymentNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGatewayTradeNo() { return gatewayTradeNo; }
    public void setGatewayTradeNo(String gatewayTradeNo) { this.gatewayTradeNo = gatewayTradeNo; }
    public String getGatewayResult() { return gatewayResult; }
    public void setGatewayResult(String gatewayResult) { this.gatewayResult = gatewayResult; }
    public Integer getNotifyAttempts() { return notifyAttempts; }
    public void setNotifyAttempts(Integer notifyAttempts) { this.notifyAttempts = notifyAttempts; }
    public LocalDateTime getExpireAt() { return expireAt; }
    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
