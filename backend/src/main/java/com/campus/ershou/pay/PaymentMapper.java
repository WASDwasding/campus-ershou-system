package com.campus.ershou.pay;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PaymentMapper {

    String COLUMNS = """
            id, payment_no, order_id, order_no, buyer_id, amount, channel, status, reason,
            gateway_trade_no, gateway_result, notify_attempts, expire_at, created_at
            """;

    @Insert("""
            INSERT INTO payments (payment_no, order_id, order_no, buyer_id, amount, channel, status, expire_at)
            VALUES (#{paymentNo}, #{orderId}, #{orderNo}, #{buyerId}, #{amount}, #{channel}, #{status}, #{expireAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PaymentRecord payment);

    @Select("SELECT " + COLUMNS + " FROM payments WHERE payment_no = #{paymentNo}")
    PaymentRecord findByNo(String paymentNo);

    @Select("""
            SELECT
            """ + COLUMNS + """
            FROM payments
            WHERE order_id = #{orderId} AND status = 'PENDING' AND expire_at > CURRENT_TIMESTAMP(3)
            ORDER BY id DESC LIMIT 1
            """)
    PaymentRecord findOpen(long orderId);

    @Select("SELECT " + COLUMNS + " FROM payments WHERE order_id = #{orderId} ORDER BY id DESC")
    List<PaymentRecord> listByOrder(long orderId);

    @Update("""
            UPDATE payments SET status = 'PAID'
            WHERE payment_no = #{paymentNo} AND status = 'PENDING'
            """)
    int markPaid(String paymentNo);

    @Update("""
            UPDATE payments SET status = 'CANCELLED', reason = #{reason}
            WHERE order_id = #{orderId} AND status = 'PENDING'
            """)
    int closePending(@Param("orderId") long orderId, @Param("reason") String reason);

    @Update("""
            UPDATE payments SET status = 'MOCK_REFUNDED', reason = #{reason}
            WHERE payment_no = #{paymentNo} AND status IN ('PENDING', 'CANCELLED')
            """)
    int markMockRefunded(@Param("paymentNo") String paymentNo, @Param("reason") String reason);

    @Update("""
            UPDATE payments SET status = 'MOCK_REFUNDED', reason = #{reason}
            WHERE order_id = #{orderId} AND status = 'PAID'
            """)
    int refundPaid(@Param("orderId") long orderId, @Param("reason") String reason);

    @Update("""
            UPDATE payments SET gateway_result = #{result}, notify_attempts = notify_attempts + 1
            WHERE payment_no = #{paymentNo}
            """)
    int bumpNotify(@Param("paymentNo") String paymentNo, @Param("result") String result);
}
