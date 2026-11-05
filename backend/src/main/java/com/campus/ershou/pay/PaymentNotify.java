package com.campus.ershou.pay;

import java.time.LocalDateTime;

public class PaymentNotify {
    private Long id;
    private Long paymentId;
    private String notifyNo;
    private LocalDateTime arrivedAt;
    private Integer amount;
    private Boolean adopted;
    private String reason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public String getNotifyNo() { return notifyNo; }
    public void setNotifyNo(String notifyNo) { this.notifyNo = notifyNo; }
    public LocalDateTime getArrivedAt() { return arrivedAt; }
    public void setArrivedAt(LocalDateTime arrivedAt) { this.arrivedAt = arrivedAt; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public Boolean getAdopted() { return adopted; }
    public void setAdopted(Boolean adopted) { this.adopted = adopted; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
