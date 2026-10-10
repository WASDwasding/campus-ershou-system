package com.campus.ershou.pay;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.redis.IdempotencyStore;
import com.campus.ershou.common.util.Randoms;
import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.im.ConversationService;
import com.campus.ershou.im.ImHub;
import com.campus.ershou.im.NotificationService;
import com.campus.ershou.order.OrderMapper;
import com.campus.ershou.order.OrderRecord;
import com.campus.ershou.order.OrderService;
import com.campus.ershou.order.OrderTx;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayService {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;
    private final OrderTx orderTx;
    private final ConversationService conversationService;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ImHub imHub;
    private final RateLimitService rateLimitService;
    private final IdempotencyStore idempotencyStore;
    private final StringRedisTemplate redis;
    private final AppProperties props;

    public PayService(
            PaymentMapper paymentMapper,
            OrderMapper orderMapper,
            OrderService orderService,
            OrderTx orderTx,
            ConversationService conversationService,
            NotificationService notificationService,
            AuditService auditService,
            ImHub imHub,
            RateLimitService rateLimitService,
            IdempotencyStore idempotencyStore,
            StringRedisTemplate redis,
            AppProperties props
    ) {
        this.paymentMapper = paymentMapper;
        this.orderMapper = orderMapper;
        this.orderService = orderService;
        this.orderTx = orderTx;
        this.conversationService = conversationService;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.imHub = imHub;
        this.rateLimitService = rateLimitService;
        this.idempotencyStore = idempotencyStore;
        this.redis = redis;
        this.props = props;
    }

    public Map<String, Object> create(long userId, long orderId, String channel, String idempotencyKey, String ip) {
        rateLimitService.check("pay-create", String.valueOf(userId), 20, Duration.ofHours(1));
        return idempotencyStore.run(userId, idempotencyKey, () -> createOnce(userId, orderId, channel, ip));
    }

    private Map<String, Object> createOnce(long userId, long orderId, String channel, String ip) {
        if (channel == null || !"MOCK".equals(channel.trim().toUpperCase())) {
            throw ApiException.of(ErrorCode.PAY_CHANNEL_UNSUPPORTED);
        }
        OrderRecord order = orderService.requireParty(userId, orderId, ip);
        if (order.getBuyerId() != userId) {
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        if (!"PENDING_PAY".equals(order.getStatus())) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        if (order.getExpireAt() != null && !order.getExpireAt().isAfter(LocalDateTime.now(Times.SHANGHAI))) {
            throw ApiException.of(ErrorCode.ORDER_EXPIRED);
        }
        PaymentRecord open = paymentMapper.findOpen(orderId);
        if (open != null) {
            return view(open);
        }
        PaymentRecord payment = new PaymentRecord();
        payment.setPaymentNo("P" + STAMP.format(LocalDateTime.now(Times.SHANGHAI)) + Randoms.digits(4));
        payment.setOrderId(orderId);
        payment.setOrderNo(order.getOrderNo());
        payment.setBuyerId(userId);
        payment.setAmount(order.getAmount());
        payment.setChannel("MOCK");
        payment.setStatus("CREATED");
        payment.setExpireAt(order.getExpireAt());
        paymentMapper.insert(payment);
        return view(payment);
    }

    public Map<String, Object> detail(long userId, String paymentNo, String ip) {
        PaymentRecord payment = paymentMapper.findByNo(paymentNo);
        if (payment == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (payment.getBuyerId() == null || payment.getBuyerId() != userId) {
            auditService.record("SECURITY", "ACCESS_DENIED", null, userId, ip, "{\"paymentNo\":\"" + paymentNo + "\"}");
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        return view(payment);
    }

    public Map<String, Object> confirm(long userId, String paymentNo, String result, String ip) {
        rateLimitService.check("pay-confirm", String.valueOf(userId), 20, Duration.ofHours(1));
        String outcome = result == null ? "" : result.trim().toUpperCase();
        if (!"SUCCESS".equals(outcome) && !"FAIL".equals(outcome)) {
            throw ApiException.validation(List.of(new FieldErrorItem("result", "只能是 SUCCESS 或 FAIL")));
        }
        PaymentRecord payment = paymentMapper.findByNo(paymentNo);
        if (payment == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (payment.getBuyerId() == null || payment.getBuyerId() != userId) {
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        if (!"CREATED".equals(payment.getStatus())
                || payment.getExpireAt() == null
                || !payment.getExpireAt().isAfter(LocalDateTime.now(Times.SHANGHAI))) {
            throw ApiException.of(ErrorCode.PAYMENT_NOT_PAYABLE);
        }
        String tradeNo = "MOCK" + STAMP.format(LocalDateTime.now(Times.SHANGHAI)) + Randoms.digits(6);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("paymentNo", payment.getPaymentNo());
        body.put("orderNo", payment.getOrderNo());
        body.put("amount", String.valueOf(payment.getAmount()));
        body.put("status", outcome);
        body.put("gatewayTradeNo", tradeNo);
        body.put("timestamp", String.valueOf(Instant.now().getEpochSecond()));
        body.put("nonce", Randoms.hex(8));
        body.put("sign", PaySigner.sign(body, props.getPay().getMock().getSecret()));
        handleNotify(body, ip);
        return detail(userId, paymentNo, ip);
    }

    public void handleNotify(Map<String, String> body, String ip) {
        String paymentNo = text(body, "paymentNo");
        String orderNo = text(body, "orderNo");
        String amountText = text(body, "amount");
        String status = text(body, "status");
        String tradeNo = text(body, "gatewayTradeNo");
        String timestampText = text(body, "timestamp");
        String nonce = text(body, "nonce");
        String sign = text(body, "sign");
        if (paymentNo == null || orderNo == null || amountText == null || status == null
                || tradeNo == null || timestampText == null || nonce == null || sign == null
                || (!"SUCCESS".equals(status) && !"FAIL".equals(status))
                || nonce.length() < 16 || nonce.length() > 32) {
            throw new PayNotifyException("BAD_REQUEST");
        }
        long timestamp;
        int amount;
        try {
            timestamp = Long.parseLong(timestampText);
            amount = Integer.parseInt(amountText);
        } catch (NumberFormatException ex) {
            throw new PayNotifyException("BAD_REQUEST");
        }
        if (Math.abs(Instant.now().getEpochSecond() - timestamp) > 300) {
            auditService.record("SECURITY", "PAY_NOTIFY_EXPIRED", null, null, ip, "{\"paymentNo\":\"" + paymentNo + "\"}");
            throw new PayNotifyException("EXPIRED_TIMESTAMP");
        }
        if (!PaySigner.matches(PaySigner.sign(body, props.getPay().getMock().getSecret()), sign)) {
            auditService.record("SECURITY", "PAY_NOTIFY_BAD_SIGN", null, null, ip, "{\"paymentNo\":\"" + paymentNo + "\"}");
            throw new PayNotifyException("INVALID_SIGN");
        }
        Boolean fresh = redis.opsForValue().setIfAbsent(RedisKeys.payNonce(nonce), "1", Duration.ofMinutes(10));
        if (!Boolean.TRUE.equals(fresh)) {
            auditService.record("SECURITY", "PAY_NOTIFY_REPLAY", null, null, ip, "{\"paymentNo\":\"" + paymentNo + "\"}");
            throw new PayNotifyException("REPLAYED");
        }
        PaymentRecord payment = paymentMapper.findByNo(paymentNo);
        if (payment == null || payment.getOrderNo() == null || !payment.getOrderNo().equals(orderNo)) {
            throw new PayNotifyException("PAYMENT_NOT_FOUND");
        }
        OrderRecord order = orderMapper.findById(payment.getOrderId());
        if (order == null || payment.getAmount() == null || order.getAmount() == null
                || amount != payment.getAmount() || amount != order.getAmount()) {
            auditService.record("SECURITY", "PAY_NOTIFY_AMOUNT_MISMATCH", null, payment.getBuyerId(), ip,
                    "{\"paymentNo\":\"" + paymentNo + "\",\"amount\":" + amount + "}");
            throw new PayNotifyException("AMOUNT_MISMATCH");
        }
        if ("SUCCESS".equals(payment.getStatus())) {
            return;
        }
        if ("FAIL".equals(status)) {
            orderTx.failPayment(paymentNo);
            paymentMapper.bumpNotify(paymentNo, "FAIL");
            return;
        }
        OrderTx.PayOutcome outcome = orderTx.applyPaid(payment, tradeNo);
        paymentMapper.bumpNotify(paymentNo, outcome.name());
        if (outcome == OrderTx.PayOutcome.LATE_REFUND) {
            auditService.record("BUSINESS", "PAYMENT_LATE_REFUND", null, payment.getBuyerId(), ip,
                    "{\"paymentNo\":\"" + paymentNo + "\",\"orderId\":" + payment.getOrderId() + "}");
            return;
        }
        if (outcome == OrderTx.PayOutcome.APPLIED) {
            auditService.record("BUSINESS", "PAYMENT_SUCCESS", null, payment.getBuyerId(), ip,
                    "{\"paymentNo\":\"" + paymentNo + "\",\"orderId\":" + payment.getOrderId() + ",\"amount\":" + amount + "}");
            OrderRecord paid = orderMapper.findById(payment.getOrderId());
            if (paid != null && paid.getConversationId() != null) {
                conversationService.system(paid.getConversationId(), "买家已付款");
            }
            if (paid != null) {
                notificationService.notify(paid.getSellerId(), "ORDER_PAID", "买家已付款", paid.getGoodsTitle(), "ORDER", paid.getId());
                imHub.pushAll(List.of(paid.getBuyerId(), paid.getSellerId()),
                        Map.of("type", "ORDER_STATUS", "orderId", paid.getId(), "status", "PAID"));
            }
        }
    }

    private Map<String, Object> view(PaymentRecord payment) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("paymentNo", payment.getPaymentNo());
        data.put("orderId", payment.getOrderId());
        data.put("orderNo", payment.getOrderNo());
        data.put("amount", payment.getAmount());
        data.put("channel", payment.getChannel());
        data.put("status", payment.getStatus());
        data.put("expireAt", Times.at(payment.getExpireAt()));
        data.put("cashier", Map.of("confirmPath", "/api/pay/mock/" + payment.getPaymentNo() + "/confirm"));
        return data;
    }

    private static String text(Map<String, String> body, String key) {
        if (body == null) {
            return null;
        }
        String value = body.get(key);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
