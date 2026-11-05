package com.campus.ershou.order;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.util.Randoms;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.goods.GoodsMapper;
import com.campus.ershou.goods.GoodsRow;
import com.campus.ershou.pay.PaymentMapper;
import com.campus.ershou.pay.PaymentNotifyMapper;
import com.campus.ershou.pay.PaymentRecord;
import com.campus.ershou.user.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderTx {

    public static final String LATE_PAY_REASON = "取消或释放后的迟到支付";

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderMapper orderMapper;
    private final GoodsMapper goodsMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentNotifyMapper paymentNotifyMapper;
    private final DisputeMapper disputeMapper;
    private final UserMapper userMapper;

    public OrderTx(
            OrderMapper orderMapper,
            GoodsMapper goodsMapper,
            PaymentMapper paymentMapper,
            PaymentNotifyMapper paymentNotifyMapper,
            DisputeMapper disputeMapper,
            UserMapper userMapper
    ) {
        this.orderMapper = orderMapper;
        this.goodsMapper = goodsMapper;
        this.paymentMapper = paymentMapper;
        this.paymentNotifyMapper = paymentNotifyMapper;
        this.disputeMapper = disputeMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public OrderRecord create(OrderRecord order) {
        if (goodsMapper.reserve(order.getGoodsId()) != 1) {
            throw ApiException.of(ErrorCode.GOODS_NOT_AVAILABLE);
        }
        try {
            orderMapper.insert(order);
        } catch (DuplicateKeyException ex) {
            String text = String.valueOf(ex.getMostSpecificCause() == null ? ex.getMessage() : ex.getMostSpecificCause().getMessage());
            if (text.contains("uk_orders_no")) {
                throw ex;
            }
            throw ApiException.of(ErrorCode.GOODS_NOT_AVAILABLE);
        }
        PaymentRecord payment = new PaymentRecord();
        payment.setPaymentNo("P" + STAMP.format(LocalDateTime.now(Times.SHANGHAI)) + Randoms.digits(4));
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setBuyerId(order.getBuyerId());
        payment.setAmount(order.getAmount());
        payment.setChannel("MOCK");
        payment.setStatus("PENDING");
        payment.setExpireAt(order.getExpireAt());
        try {
            paymentMapper.insert(payment);
        } catch (DuplicateKeyException ex) {
            throw ex;
        }
        orderMapper.insertEvent(order.getId(), null, "PENDING_PAY", "BUYER", order.getBuyerId());
        return order;
    }

    @Transactional
    public void cancelUnpaid(OrderRecord order, String by, Long actorId, String reason) {
        if (orderMapper.cancel(order.getId(), "PENDING_PAY", "CANCELLED", by, reason) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        if (goodsMapper.releaseReserved(order.getGoodsId()) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        paymentMapper.closePending(order.getId(), reason);
        orderMapper.insertEvent(order.getId(), "PENDING_PAY", "CANCELLED", actorType(by), actorId);
    }

    @Transactional
    public PayOutcome applySuccess(PaymentRecord payment, String notifyNo, int amount) {
        if (paymentNotifyMapper.findByNo(notifyNo) != null) {
            return PayOutcome.ALREADY;
        }
        OrderRecord order = orderMapper.findById(payment.getOrderId());
        if (order == null) {
            throw new com.campus.ershou.pay.PayNotifyException("PAYMENT_NOT_FOUND");
        }
        GoodsRow goods = goodsMapper.findById(order.getGoodsId());
        OrderRecord active = orderMapper.findActiveByGoods(order.getGoodsId());
        boolean occupied = goods != null
                && "RESERVED".equals(goods.getStatus())
                && active != null
                && active.getId().equals(order.getId());
        boolean canAdopt = "PENDING_PAY".equals(order.getStatus())
                && "PENDING".equals(payment.getStatus())
                && occupied
                && payment.getAmount() != null
                && order.getAmount() != null
                && amount == payment.getAmount()
                && amount == order.getAmount();
        if (canAdopt) {
            if (paymentMapper.markPaid(payment.getPaymentNo()) != 1
                    || orderMapper.markPaid(order.getId()) != 1
                    || goodsMapper.markMeetup(order.getGoodsId()) != 1) {
                throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
            }
            paymentNotifyMapper.insert(payment.getId(), notifyNo, amount, true, null);
            orderMapper.insertEvent(order.getId(), "PENDING_PAY", "PAID", "SYSTEM", null);
            return PayOutcome.APPLIED;
        }
        boolean stillThisOrder = active != null && active.getId().equals(order.getId());
        boolean late = "CANCELLED".equals(order.getStatus()) || !stillThisOrder;
        if (late) {
            int changed = paymentMapper.markMockRefunded(payment.getPaymentNo(), LATE_PAY_REASON);
            paymentNotifyMapper.insert(payment.getId(), notifyNo, amount, false, LATE_PAY_REASON);
            return changed == 1 ? PayOutcome.LATE : PayOutcome.ALREADY;
        }
        paymentNotifyMapper.insert(payment.getId(), notifyNo, amount, false, null);
        return PayOutcome.IGNORED;
    }

    @Transactional
    public void recordNotify(long paymentId, String notifyNo, int amount, boolean adopted, String reason) {
        if (paymentNotifyMapper.findByNo(notifyNo) != null) {
            return;
        }
        paymentNotifyMapper.insert(paymentId, notifyNo, amount, adopted, reason);
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
            if (goodsMapper.markSoldFrom(order.getGoodsId(), "MEETUP") != 1) {
                throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
            }
            orderMapper.insertEvent(order.getId(), "PAID", "COMPLETED", buyer ? "BUYER" : "SELLER",
                    buyer ? order.getBuyerId() : order.getSellerId());
            return true;
        }
        return false;
    }

    @Transactional
    public DisputeRecord openDispute(OrderRecord order, long userId, String reason, String description, List<String> files) {
        if (orderMapper.markDisputed(order.getId()) != 1 || goodsMapper.markDisputed(order.getGoodsId()) != 1) {
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
        if (orderMapper.reopenPaid(order.getId()) != 1 || goodsMapper.reopenMeetup(order.getGoodsId()) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        orderMapper.insertEvent(order.getId(), "DISPUTED", "PAID",
                order.getBuyerId().equals(dispute.getOpenerId()) ? "BUYER" : "SELLER", dispute.getOpenerId());
    }

    @Transactional
    public void resolveReject(OrderRecord order, DisputeRecord dispute, String remark) {
        if (disputeMapper.resolve(dispute.getId(), "REJECTED", "REJECT", remark) != 1) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        if (orderMapper.reopenPaid(order.getId()) != 1 || goodsMapper.reopenMeetup(order.getGoodsId()) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        orderMapper.insertEvent(order.getId(), "DISPUTED", "PAID", "ADMIN", null);
    }

    @Transactional
    public void resolveComplete(OrderRecord order, DisputeRecord dispute, String remark) {
        if (order.getBuyerHandoverAt() == null || order.getSellerHandoverAt() == null) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        if (disputeMapper.resolve(dispute.getId(), "RESOLVED", "COMPLETE", remark) != 1) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        if (orderMapper.completeFromDispute(order.getId()) != 1
                || goodsMapper.markSoldFrom(order.getGoodsId(), "DISPUTED") != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        orderMapper.insertEvent(order.getId(), "DISPUTED", "COMPLETED", "ADMIN", null);
    }

    @Transactional
    public void resolveCancel(OrderRecord order, DisputeRecord dispute, String remark, boolean fraud) {
        if (disputeMapper.resolve(dispute.getId(), "RESOLVED", "CANCEL", remark) != 1) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        if (orderMapper.cancelFrom(order.getId(), "DISPUTED", remark) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        paymentMapper.refundPaid(order.getId(), remark);
        if (fraud) {
            if (goodsMapper.offShelfDisputed(order.getGoodsId()) != 1) {
                throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
            }
            userMapper.banPublish(order.getSellerId());
        } else if (goodsMapper.releaseDisputed(order.getGoodsId()) != 1) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        orderMapper.insertEvent(order.getId(), "DISPUTED", "CANCELLED", "ADMIN", null);
    }

    public enum PayOutcome {
        APPLIED, ALREADY, LATE, IGNORED
    }

    private static String actorType(String by) {
        if ("BUYER".equals(by) || "SELLER".equals(by) || "ADMIN".equals(by)) {
            return by;
        }
        return "SYSTEM";
    }
}
