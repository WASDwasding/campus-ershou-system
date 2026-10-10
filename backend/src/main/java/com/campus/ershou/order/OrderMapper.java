package com.campus.ershou.order;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    String COLUMNS = """
            id, order_no, buyer_id, seller_id, goods_id, status, amount, quantity, goods_title,
            goods_cover_file_id, conversation_id, meetup_building_id, meetup_place, meetup_time,
            buyer_handover_at, seller_handover_at, cancel_by, cancel_reason, expire_at, paid_at,
            completed_at, cancelled_at, created_at, updated_at
            """;

    @Insert("""
            INSERT INTO orders (
                order_no, buyer_id, seller_id, goods_id, status, amount, quantity, goods_title,
                goods_cover_file_id, conversation_id, expire_at
            ) VALUES (
                #{orderNo}, #{buyerId}, #{sellerId}, #{goodsId}, #{status}, #{amount}, #{quantity}, #{goodsTitle},
                #{goodsCoverFileId}, #{conversationId}, #{expireAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderRecord order);

    @Select("SELECT " + COLUMNS + " FROM orders WHERE id = #{id}")
    OrderRecord findById(long id);

    @Select("SELECT " + COLUMNS + " FROM orders WHERE order_no = #{orderNo}")
    OrderRecord findByNo(String orderNo);

    @Select("SELECT " + COLUMNS + " FROM orders WHERE goods_id = #{goodsId} AND status IN ('PENDING_PAY', 'PAID', 'DISPUTED') ORDER BY id DESC LIMIT 1")
    OrderRecord findActiveByGoods(long goodsId);

    @Select("SELECT " + COLUMNS + " FROM orders WHERE status = 'PENDING_PAY' AND (buyer_id = #{userId} OR seller_id = #{userId})")
    List<OrderRecord> listPendingOfUser(long userId);

    @Select("""
            SELECT COUNT(*) FROM orders
            WHERE buyer_id = #{buyerId} AND status = 'PENDING_PAY'
            """)
    int countPendingPay(long buyerId);

    @Select("""
            SELECT COUNT(*) FROM orders
            WHERE buyer_id = #{buyerId} AND status = #{status}
            """)
    int countBuyerStatus(@Param("buyerId") long buyerId, @Param("status") String status);

    @Select("""
            SELECT COUNT(*) FROM orders
            WHERE seller_id = #{sellerId} AND status IN ('PENDING_PAY', 'PAID', 'DISPUTED')
            """)
    int countSellerActive(long sellerId);

    @Select("""
            SELECT COUNT(*) FROM orders
            WHERE buyer_id = #{buyerId}
              AND (status = 'PENDING_PAY' OR (status = 'PAID' AND buyer_handover_at IS NULL))
            """)
    int countBuyerTodos(long buyerId);

    @Update("""
            UPDATE orders
            SET status = #{to},
                cancel_by = #{cancelBy},
                cancel_reason = #{cancelReason},
                cancelled_at = CURRENT_TIMESTAMP(3)
            WHERE id = #{id} AND status = #{from}
            """)
    int cancel(@Param("id") long id, @Param("from") String from, @Param("to") String to,
               @Param("cancelBy") String cancelBy, @Param("cancelReason") String cancelReason);

    @Update("""
            UPDATE orders SET status = 'PAID', paid_at = CURRENT_TIMESTAMP(3)
            WHERE id = #{id} AND status = 'PENDING_PAY'
            """)
    int markPaid(long id);

    @Update("""
            UPDATE orders
            SET meetup_building_id = #{buildingId}, meetup_place = #{place}, meetup_time = #{time}
            WHERE id = #{id} AND status = 'PAID'
            """)
    int updateMeetup(@Param("id") long id, @Param("buildingId") long buildingId,
                     @Param("place") String place, @Param("time") LocalDateTime time);

    @Update("""
            UPDATE orders SET buyer_handover_at = IFNULL(buyer_handover_at, CURRENT_TIMESTAMP(3))
            WHERE id = #{id} AND status = 'PAID' AND buyer_id = #{userId}
            """)
    int confirmBuyer(@Param("id") long id, @Param("userId") long userId);

    @Update("""
            UPDATE orders SET seller_handover_at = IFNULL(seller_handover_at, CURRENT_TIMESTAMP(3))
            WHERE id = #{id} AND status = 'PAID' AND seller_id = #{userId}
            """)
    int confirmSeller(@Param("id") long id, @Param("userId") long userId);

    @Update("""
            UPDATE orders SET status = 'COMPLETED', completed_at = CURRENT_TIMESTAMP(3)
            WHERE id = #{id} AND status = 'PAID'
              AND buyer_handover_at IS NOT NULL AND seller_handover_at IS NOT NULL
            """)
    int completeIfBothConfirmed(long id);

    @Update("""
            UPDATE orders SET status = 'DISPUTED' WHERE id = #{id} AND status = 'PAID'
            """)
    int markDisputed(long id);

    @Update("""
            UPDATE orders SET status = 'PAID' WHERE id = #{id} AND status = 'DISPUTED'
            """)
    int reopenPaid(long id);

    @Update("""
            UPDATE orders SET status = 'COMPLETED', completed_at = CURRENT_TIMESTAMP(3)
            WHERE id = #{id} AND status = 'DISPUTED'
            """)
    int completeFromDispute(long id);

    @Update("""
            UPDATE orders
            SET status = 'CANCELLED', cancel_by = 'ADMIN', cancel_reason = #{reason},
                cancelled_at = CURRENT_TIMESTAMP(3)
            WHERE id = #{id} AND status = #{from}
            """)
    int cancelFrom(@Param("id") long id, @Param("from") String from, @Param("reason") String reason);

    @Select("SELECT " + COLUMNS + " FROM orders WHERE status = 'PENDING_PAY' AND expire_at <= CURRENT_TIMESTAMP(3) LIMIT 50")
    List<OrderRecord> listExpired();

    @Insert("""
            INSERT INTO order_events (order_id, from_status, to_status, actor_type, actor_id)
            VALUES (#{orderId}, #{fromStatus}, #{toStatus}, #{actorType}, #{actorId})
            """)
    int insertEvent(@Param("orderId") long orderId, @Param("fromStatus") String fromStatus,
                     @Param("toStatus") String toStatus, @Param("actorType") String actorType,
                     @Param("actorId") Long actorId);

    @Select("""
            SELECT id, order_id, from_status, to_status, actor_type, actor_id, created_at
            FROM order_events WHERE order_id = #{orderId} ORDER BY id ASC
            """)
    List<OrderEvent> listEvents(long orderId);

    @Select("""
            <script>
            SELECT
            """ + COLUMNS + """
            FROM orders
            WHERE
            <choose>
              <when test="role == 'SELLER'">seller_id = #{userId}</when>
              <otherwise>buyer_id = #{userId}</otherwise>
            </choose>
            <if test="statuses != null">
              AND status IN
              <foreach collection="statuses" item="st" open="(" separator="," close=")">#{st}</foreach>
            </if>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<OrderRecord> listMine(@Param("userId") long userId, @Param("role") String role,
                               @Param("statuses") List<String> statuses,
                               @Param("offset") int offset, @Param("limit") int limit);

    @Select("""
            <script>
            SELECT COUNT(*) FROM orders
            WHERE
            <choose>
              <when test="role == 'SELLER'">seller_id = #{userId}</when>
              <otherwise>buyer_id = #{userId}</otherwise>
            </choose>
            <if test="statuses != null">
              AND status IN
              <foreach collection="statuses" item="st" open="(" separator="," close=")">#{st}</foreach>
            </if>
            </script>
            """)
    long countMine(@Param("userId") long userId, @Param("role") String role, @Param("statuses") List<String> statuses);

    @Select("SELECT " + COLUMNS + " FROM orders WHERE goods_id = #{goodsId} AND buyer_id = #{buyerId} AND status IN ('PENDING_PAY','PAID','DISPUTED') LIMIT 1")
    OrderRecord findActiveForBuyer(@Param("goodsId") long goodsId, @Param("buyerId") long buyerId);

    @Select("""
            <script>
            SELECT
            """ + COLUMNS + """
            FROM orders
            WHERE 1 = 1
            <if test="status != null">AND status = #{status}</if>
            <if test="orderNo != null">AND order_no = #{orderNo}</if>
            <if test="buyerId != null">AND buyer_id = #{buyerId}</if>
            <if test="sellerId != null">AND seller_id = #{sellerId}</if>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<OrderRecord> adminList(@Param("status") String status, @Param("orderNo") String orderNo,
                                @Param("buyerId") Long buyerId, @Param("sellerId") Long sellerId,
                                @Param("offset") int offset, @Param("limit") int limit);

    @Select("""
            <script>
            SELECT COUNT(*) FROM orders WHERE 1 = 1
            <if test="status != null">AND status = #{status}</if>
            <if test="orderNo != null">AND order_no = #{orderNo}</if>
            <if test="buyerId != null">AND buyer_id = #{buyerId}</if>
            <if test="sellerId != null">AND seller_id = #{sellerId}</if>
            </script>
            """)
    long adminCount(@Param("status") String status, @Param("orderNo") String orderNo,
                     @Param("buyerId") Long buyerId, @Param("sellerId") Long sellerId);

    @Select("SELECT " + COLUMNS + " FROM orders WHERE goods_id = #{goodsId} AND status = 'DISPUTED' LIMIT 1")
    OrderRecord findDisputedByGoods(long goodsId);

    @Select("SELECT " + COLUMNS + " FROM orders WHERE goods_id = #{goodsId} AND status IN ('PENDING_PAY','PAID') ORDER BY id DESC LIMIT 1")
    OrderRecord findOpenByGoods(long goodsId);
}
