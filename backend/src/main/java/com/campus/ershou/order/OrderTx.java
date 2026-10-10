package com.campus.ershou.order;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.goods.GoodsMapper;
import com.campus.ershou.im.ConversationRecord;
import com.campus.ershou.im.ConversationService;
import com.campus.ershou.pay.PaymentMapper;
import com.campus.ershou.pay.PaymentRecord;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderTx {

    private final OrderMapper orderMapper;
    private final GoodsMapper goodsMapper;
    private final PaymentMapper paymentMapper;
    private final DisputeMapper disputeMapper;
    private final ReviewMapper reviewMapper;
    private final ConversationService conversationService;

    public OrderTx(
            OrderMapper orderMapper,
            GoodsMapper goodsMapper,
            PaymentMapper paymentMapper,
            DisputeMapper disputeMapper,
            ReviewMapper reviewMapper,
            ConversationService conversationService
    ) {
        this.orderMapper = orderMapper;
        this.goodsMapper = goodsMapper;
        this.paymentMapper = paymentMapper;
        this.disputeMapper = disputeMapper;
        this.reviewMapper = reviewMapper;
        this.conversationService = conversationService;
    }

    @Transactional
    public OrderRecord create(OrderRecord order) {
        if (goodsMapper.reserve(order.getGoodsId()) != 1) {
            throw ApiException.of(ErrorCode.GOODS_NOT_AVAILABLE);
        }
        ConversationRecord conversation = conversationService.ensure(order.getGoodsId(), order.getBuyerId(), order.getSellerId());
        order.setConversationId(conversation.getId());
        try {
            orderMapper.insert(order);
        } catch (DuplicateKeyException ex) {
            String text = String.valueOf(ex.getMostSpecificCause() == null ? ex.getMessage() : ex.getMostSpecificCause().getMessage());
            if (text.contains("uk_orders_no")) {
                throw ex;
            }
            throw ApiException.of(ErrorCode.GOODS_NOT_AVAILABLE);
        }
        orderMapper.insertEvent(order.getId(), null, "PENDING_PAY", "BUYER", order.getBuyerId());
        return order;
    }

    @Transactional
    public void cancelUnpaid(OrderRecord order, String by, Long actorId, String reason) {
        if (orderMapper.cancel(order.getId(), "PENDING_PAY", "CANCELLED", by, reason) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        goodsMapper.releaseReserved(order.getGoodsId());
        paymentMapper.closeCreated(order.getId());
        orderMapper.insertEvent(order.getId(), "PENDING_PAY", "CANCELLED", actorType(by), actorId);
    }

    @Transactional
    public void cancelPaid(OrderRecord order, String by, Long actorId, String reason) {
        if (orderMapper.cancel(order.getId(), "PAID", "CANCELLED", by, reason) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        goodsMapper.offShelfReserved(order.getGoodsId());
        paymentMapper.refundSuccess(order.getId());
        orderMapper.insertEvent(order.getId(), "PAID", "CANCELLED", actorType(by), actorId);
    }

    @Transactional
    public PayOutcome applyPaid(PaymentRecord payment, String gatewayTradeNo) {
        int marked = paymentMapper.markSuccess(payment.getPaymentNo(), gatewayTradeNo);
        if (marked != 1) {
            PaymentRecord current = paymentMapper.findByNo(payment.getPaymentNo());
            if (current != null && "SUCCESS".equals(current.getStatus())) {
                return PayOutcome.ALREADY;
            }
            if (current != null && "CLOSED".equals(current.getStatus())
                    && paymentMapper.refundClosed(payment.getPaymentNo(), gatewayTradeNo) == 1) {
                return PayOutcome.LATE_REFUND;
            }
            return PayOutcome.IGNORED;
        }
        if (orderMapper.markPaid(payment.getOrderId()) != 1) {
            paymentMapper.refundSuccess(payment.getOrderId());
            return PayOutcome.LATE_REFUND;
        }
        orderMapper.insertEvent(payment.getOrderId(), "PENDING_PAY", "PAID", "SYSTEM", null);
        return PayOutcome.APPLIED;
    }

    @Transactional
    public void failPayment(String paymentNo) {
        paymentMapper.markFailed(paymentNo);
    }

    @Transactional
    public boolean confirmHandover(OrderRecord order, boolean buyer) {
        int updated = buyer
                ? orderMapper.confirmBuyer(order.getId(), order.getBuyerId())
                : orderMapper.confirmSeller(order.getId(), order.getSellerId());
        if (updated != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        if (orderMapper.completeIfBothConfirmed(order.getId()) == 1) {
            goodsMapper.markSold(order.getGoodsId());
            orderMapper.insertEvent(order.getId(), "PAID", "COMPLETED", buyer ? "BUYER" : "SELLER",
                    buyer ? order.getBuyerId() : order.getSellerId());
            return true;
        }
        return false;
    }

    @Transactional
    public void updateMeetup(long orderId, long buildingId, String place, java.time.LocalDateTime time) {
        if (orderMapper.updateMeetup(orderId, buildingId, place, time) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
    }

    @Transactional
    public DisputeRecord openDispute(OrderRecord order, long userId, String reason, String description, List<String> files) {
        if (orderMapper.markDisputed(order.getId()) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        DisputeRecord dispute = new DisputeRecord();
        dispute.setOrderId(order.getId());
        dispute.setOpenerId(userId);
        dispute.setReason(reason);
        dispute.setDescription(description);
        dispute.setStatus("OPEN");
        disputeMapper.insert(dispute);
        for (int i = 0; i < files.size(); i++) {
            disputeMapper.insertFile(dispute.getId(), files.get(i), i);
        }
        orderMapper.insertEvent(order.getId(), "PAID", "DISPUTED", order.getBuyerId() == userId ? "BUYER" : "SELLER", userId);
        return dispute;
    }

    @Transactional
    public void withdrawDispute(OrderRecord order, DisputeRecord dispute) {
        if (disputeMapper.withdraw(dispute.getId(), dispute.getOpenerId()) != 1) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        if (orderMapper.reopenPaid(order.getId()) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        orderMapper.insertEvent(order.getId(), "DISPUTED", "PAID",
                order.getBuyerId().equals(dispute.getOpenerId()) ? "BUYER" : "SELLER", dispute.getOpenerId());
    }

    @Transactional
    public void resolveComplete(OrderRecord order, DisputeRecord dispute, String remark) {
        if (disputeMapper.resolve(dispute.getId(), "COMPLETE", remark) != 1) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        if (orderMapper.completeFromDispute(order.getId()) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        goodsMapper.markSold(order.getGoodsId());
        orderMapper.insertEvent(order.getId(), "DISPUTED", "COMPLETED", "ADMIN", null);
    }

    @Transactional
    public void resolveRefund(OrderRecord order, DisputeRecord dispute, String remark) {
        if (disputeMapper.resolve(dispute.getId(), "REFUND", remark) != 1) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        if (orderMapper.cancelFrom(order.getId(), "DISPUTED", "管理员判退款") != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        goodsMapper.offShelfReserved(order.getGoodsId());
        paymentMapper.refundSuccess(order.getId());
        orderMapper.insertEvent(order.getId(), "DISPUTED", "CANCELLED", "ADMIN", null);
    }

    @Transactional
    public void review(ReviewRecord review) {
        try {
            reviewMapper.insert(review);
        } catch (DuplicateKeyException ex) {
            throw ApiException.of(ErrorCode.ALREADY_REVIEWED);
        }
    }

    public enum PayOutcome {
        APPLIED, ALREADY, LATE_REFUND, IGNORED
    }

    private static String actorType(String by) {
        if ("BUYER".equals(by) || "SELLER".equals(by) || "ADMIN".equals(by)) {
            return by;
        }
        return "SYSTEM";
    }
}
