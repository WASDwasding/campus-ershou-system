package com.campus.ershou.pay;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentNotifyMapper {

    @Select("""
            SELECT id, payment_id, notify_no, arrived_at, amount, adopted, reason
            FROM payment_notifies
            WHERE notify_no = #{notifyNo}
            """)
    PaymentNotify findByNo(String notifyNo);

    @Select("""
            SELECT id, payment_id, notify_no, arrived_at, amount, adopted, reason
            FROM payment_notifies
            WHERE payment_id = #{paymentId}
            ORDER BY id ASC
            """)
    List<PaymentNotify> listByPayment(long paymentId);

    @Insert("""
            INSERT INTO payment_notifies (payment_id, notify_no, amount, adopted, reason)
            VALUES (#{paymentId}, #{notifyNo}, #{amount}, #{adopted}, #{reason})
            """)
    int insert(@Param("paymentId") long paymentId, @Param("notifyNo") String notifyNo,
               @Param("amount") int amount, @Param("adopted") boolean adopted, @Param("reason") String reason);
}
