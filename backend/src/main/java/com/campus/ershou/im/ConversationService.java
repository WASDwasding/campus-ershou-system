package com.campus.ershou.im;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.common.file.FileUrls;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.util.ContactInfo;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.goods.GoodsMapper;
import com.campus.ershou.goods.GoodsRow;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.campus.ershou.user.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConversationService {

    private final ConversationMapper conversationMapper;
    private final GoodsMapper goodsMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final FileService fileService;
    private final FileUrls fileUrls;
    private final RateLimitService rateLimitService;
    private final ImHub imHub;

    public ConversationService(
            ConversationMapper conversationMapper,
            GoodsMapper goodsMapper,
            UserMapper userMapper,
            UserService userService,
            FileService fileService,
            FileUrls fileUrls,
            RateLimitService rateLimitService,
            ImHub imHub
    ) {
        this.conversationMapper = conversationMapper;
        this.goodsMapper = goodsMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.fileService = fileService;
        this.fileUrls = fileUrls;
        this.rateLimitService = rateLimitService;
        this.imHub = imHub;
    }

    public Map<String, Object> open(long userId, long goodsId) {
        UserEntity me = requireVerified(userId);
        rateLimitService.check("im-open", String.valueOf(userId), 30, Duration.ofHours(1));
        GoodsRow goods = goodsMapper.findById(goodsId);
        if (goods == null || !"ON_SALE".equals(goods.getStatus())) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (goods.getSellerId() != null && goods.getSellerId() == userId) {
            throw ApiException.of(ErrorCode.CANNOT_CHAT_WITH_SELF);
        }
        ConversationRecord conversation = ensure(goodsId, userId, goods.getSellerId());
        return toDetail(conversation, me);
    }

    @Transactional
    public ConversationRecord ensure(long goodsId, long buyerId, long sellerId) {
        ConversationRecord existing = conversationMapper.findByGoodsBuyer(goodsId, buyerId);
        if (existing != null) {
            return existing;
        }
        ConversationRecord created = new ConversationRecord();
        created.setGoodsId(goodsId);
        created.setBuyerId(buyerId);
        created.setSellerId(sellerId);
        try {
            conversationMapper.insert(created);
            return created;
        } catch (DuplicateKeyException ex) {
            ConversationRecord again = conversationMapper.findByGoodsBuyer(goodsId, buyerId);
            if (again == null) {
                throw ex;
            }
            return again;
        }
    }

    public Map<String, Object> list(long userId, Integer page, Integer size) {
        userService.requireUser(userId);
        int[] paging = Paging.page(page, size);
        int offset = (paging[0] - 1) * paging[1];
        List<ConversationRecord> rows = conversationMapper.listForUser(userId, offset, paging[1]);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ConversationRecord row : rows) {
            list.add(toCard(row, userId));
        }
        return Paging.of(list, paging[0], paging[1], conversationMapper.countForUser(userId));
    }

    public Map<String, Object> detail(long userId, long conversationId) {
        UserEntity me = userService.requireUser(userId);
        return toDetail(requireMember(conversationId, userId), me);
    }

    public Map<String, Object> messages(long userId, long conversationId, Long beforeId, Long afterId, Integer size) {
        requireMember(conversationId, userId);
        if (beforeId != null && afterId != null) {
            throw ApiException.validation(List.of(new FieldErrorItem("beforeId", "beforeId 和 afterId 只能带一个")));
        }
        int limit = size == null ? 30 : size;
        if (limit < 1 || limit > 50) {
            throw ApiException.validation(List.of(new FieldErrorItem("size", "每页 1–50 条")));
        }
        List<MessageRecord> rows = conversationMapper.listMessages(conversationId, beforeId, afterId, limit);
        if (afterId == null) {
            Collections.reverse(rows);
        }
        List<Map<String, Object>> list = rows.stream().map(this::toMessage).toList();
        return Map.of("list", list);
    }

    public Map<String, Object> send(long userId, long conversationId, String type, String content, String clientMsgId) {
        requireVerified(userId);
        rateLimitService.check("im-send", String.valueOf(userId), 30, Duration.ofMinutes(1));
        ConversationRecord conversation = requireMember(conversationId, userId);
        if (clientMsgId == null || !clientMsgId.matches("[A-Za-z0-9_-]{8,40}")) {
            throw ApiException.validation(List.of(new FieldErrorItem("clientMsgId", "长度为 8–40 位")));
        }
        String normalized = type == null ? "" : type.trim().toUpperCase();
        MessageRecord message = new MessageRecord();
        message.setConversationId(conversationId);
        message.setSenderId(userId);
        message.setClientMsgId(clientMsgId);
        message.setType(normalized);
        if ("TEXT".equals(normalized)) {
            String text = content == null ? "" : content.trim();
            if (text.isEmpty() || text.codePointCount(0, text.length()) > 500) {
                throw ApiException.validation(List.of(new FieldErrorItem("content", "文字 1–500 字")));
            }
            String masked = ContactInfo.mask(text);
            message.setRawContent(text);
            message.setContent(masked);
            message.setMasked(!masked.equals(text));
        } else if ("IMAGE".equals(normalized)) {
            String fileId = content == null ? "" : content.trim();
            fileService.requireUsable(fileId, userId, "IM");
            message.setContent(fileId);
            message.setRawContent(fileId);
            message.setMasked(false);
        } else {
            throw ApiException.validation(List.of(new FieldErrorItem("type", "只能发 TEXT 或 IMAGE")));
        }
        MessageRecord existing = conversationMapper.findByClientMsg(conversationId, userId, clientMsgId);
        if (existing != null) {
            return toMessage(existing);
        }
        try {
            conversationMapper.insertMessage(message);
        } catch (DuplicateKeyException ex) {
            MessageRecord again = conversationMapper.findByClientMsg(conversationId, userId, clientMsgId);
            if (again != null) {
                return toMessage(again);
            }
            throw ex;
        }
        conversationMapper.touch(conversationId);
        Map<String, Object> view = toMessage(conversationMapper.findMessage(message.getId()));
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("type", "MESSAGE");
        event.put("conversationId", conversationId);
        event.put("message", view);
        imHub.push(conversation.getBuyerId(), event);
        imHub.push(conversation.getSellerId(), event);
        long peer = conversation.getBuyerId() == userId ? conversation.getSellerId() : conversation.getBuyerId();
        imHub.push(peer, Map.of("type", "BADGE"));
        return view;
    }

    public Map<String, Object> read(long userId, long conversationId, long lastReadMessageId) {
        ConversationRecord conversation = requireMember(conversationId, userId);
        MessageRecord message = conversationMapper.findMessage(lastReadMessageId);
        if (message == null || !conversationIdEquals(message, conversationId)) {
            throw ApiException.validation(List.of(new FieldErrorItem("lastReadMessageId", "消息不存在")));
        }
        if (conversation.getBuyerId() == userId) {
            conversationMapper.readBuyer(conversationId, userId, lastReadMessageId);
        } else {
            conversationMapper.readSeller(conversationId, userId, lastReadMessageId);
        }
        long peer = conversation.getBuyerId() == userId ? conversation.getSellerId() : conversation.getBuyerId();
        imHub.push(peer, Map.of("type", "READ", "conversationId", conversationId, "userId", userId, "lastReadMessageId", lastReadMessageId));
        return Map.of("lastReadMessageId", lastReadMessageId);
    }

    public void system(long conversationId, String content) {
        if (conversationId <= 0 || content == null || content.isBlank()) {
            return;
        }
        MessageRecord message = new MessageRecord();
        message.setConversationId(conversationId);
        message.setType("SYSTEM");
        message.setContent(content);
        message.setMasked(false);
        conversationMapper.insertMessage(message);
        conversationMapper.touch(conversationId);
        ConversationRecord conversation = conversationMapper.findById(conversationId);
        if (conversation == null) {
            return;
        }
        Map<String, Object> view = toMessage(conversationMapper.findMessage(message.getId()));
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("type", "MESSAGE");
        event.put("conversationId", conversationId);
        event.put("message", view);
        imHub.push(conversation.getBuyerId(), event);
        imHub.push(conversation.getSellerId(), event);
    }

    public int countUnread(long userId) {
        return conversationMapper.countUnread(userId);
    }

    public ConversationRecord requireMember(long conversationId, long userId) {
        ConversationRecord conversation = conversationMapper.findById(conversationId);
        if (conversation == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (conversation.getBuyerId() != userId && conversation.getSellerId() != userId) {
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        return conversation;
    }

    private Map<String, Object> toCard(ConversationRecord conversation, long userId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", conversation.getId());
        data.put("goodsId", conversation.getGoodsId());
        GoodsRow goods = goodsMapper.findById(conversation.getGoodsId());
        data.put("goodsTitle", goods == null ? null : goods.getTitle());
        long peerId = conversation.getBuyerId() == userId ? conversation.getSellerId() : conversation.getBuyerId();
        UserEntity peer = userMapper.findById(peerId);
        data.put("peer", person(peer));
        data.put("myRole", conversation.getBuyerId() == userId ? "BUYER" : "SELLER");
        data.put("lastMessageAt", Times.at(conversation.getLastMessageAt()));
        return data;
    }

    private Map<String, Object> toDetail(ConversationRecord conversation, UserEntity me) {
        Map<String, Object> data = toCard(conversation, me.getId());
        data.put("buyer", person(userMapper.findById(conversation.getBuyerId())));
        data.put("seller", person(userMapper.findById(conversation.getSellerId())));
        data.put("createdAt", Times.at(conversation.getCreatedAt()));
        return data;
    }

    private Map<String, Object> toMessage(MessageRecord message) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", message.getId());
        data.put("conversationId", message.getConversationId());
        data.put("senderId", message.getSenderId());
        data.put("type", message.getType());
        if ("IMAGE".equals(message.getType())) {
            var file = fileService.find(message.getContent());
            data.put("fileId", message.getContent());
            data.put("content", file == null ? null : fileService.describe(file, false).get("url"));
        } else {
            data.put("content", message.getContent());
        }
        data.put("clientMsgId", message.getClientMsgId());
        data.put("masked", Boolean.TRUE.equals(message.getMasked()));
        data.put("createdAt", Times.at(message.getCreatedAt()));
        return data;
    }

    private Map<String, Object> person(UserEntity user) {
        if (user == null) {
            return null;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("nickname", user.getNickname());
        data.put("avatarUrl", fileService.publicUrlIfOwned(user.getAvatarFileId()));
        return data;
    }

    private UserEntity requireVerified(long userId) {
        UserEntity user = userService.requireUser(userId);
        if (!"VERIFIED".equals(user.getVerifyStatus())) {
            throw ApiException.of(ErrorCode.NOT_VERIFIED);
        }
        return user;
    }

    private static boolean conversationIdEquals(MessageRecord message, long conversationId) {
        return message.getConversationId() != null && message.getConversationId() == conversationId;
    }
}
