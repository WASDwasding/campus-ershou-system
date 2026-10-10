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
            id, payment_no, order_id, order_no, buyer_id, amount, channel, status,
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
            WHERE order_id = #{orderId} AND status = 'CREATED' AND expire_at > CURRENT_TIMESTAMP(3)
            ORDER BY id DESC LIMIT 1
            """)
    PaymentRecord findOpen(long orderId);

    @Select("SELECT " + COLUMNS + " FROM payments WHERE order_id = #{orderId} ORDER BY id DESC")
    List<PaymentRecord> listByOrder(long orderId);

    @Update("""
            UPDATE payments SET status = 'SUCCESS', gateway_trade_no = #{tradeNo}
            WHERE payment_no = #{paymentNo} AND status = 'CREATED'
            """)
    int markSuccess(@Param("paymentNo") String paymentNo, @Param("tradeNo") String tradeNo);

    @Update("UPDATE payments SET status = 'FAILED' WHERE payment_no = #{paymentNo} AND status = 'CREATED'")
    int markFailed(String paymentNo);

    @Update("UPDATE payments SET status = 'CLOSED' WHERE order_id = #{orderId} AND status = 'CREATED'")
    int closeCreated(long orderId);

    @Update("UPDATE payments SET status = 'REFUNDED' WHERE order_id = #{orderId} AND status = 'SUCCESS'")
    int refundSuccess(long orderId);

    @Update("""
            UPDATE payments
            SET status = 'REFUNDED', gateway_trade_no = #{tradeNo}
            WHERE payment_no = #{paymentNo} AND status = 'CLOSED'
            """)
    int refundClosed(@Param("paymentNo") String paymentNo, @Param("tradeNo") String tradeNo);

    @Update("""
            UPDATE payments SET gateway_result = #{result}, notify_attempts = notify_attempts + 1
            WHERE payment_no = #{paymentNo}
            """)
    int bumpNotify(@Param("paymentNo") String paymentNo, @Param("result") String result);
}
