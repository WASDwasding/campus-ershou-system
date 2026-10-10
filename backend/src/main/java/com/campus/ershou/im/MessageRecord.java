package com.campus.ershou.im;

import java.time.LocalDateTime;

public class MessageRecord {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String type;
    private String content;
    private String rawContent;
    private String clientMsgId;
    private Boolean masked;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }
    public String getClientMsgId() { return clientMsgId; }
    public void setClientMsgId(String clientMsgId) { this.clientMsgId = clientMsgId; }
    public Boolean getMasked() { return masked; }
    public void setMasked(Boolean masked) { this.masked = masked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
