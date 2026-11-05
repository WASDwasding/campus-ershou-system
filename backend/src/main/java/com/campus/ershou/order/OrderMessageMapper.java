package com.campus.ershou.order;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMessageMapper {

    @Insert("""
            INSERT INTO order_messages (order_id, sender_id, type, content, client_msg_id)
            VALUES (#{orderId}, #{senderId}, #{type}, #{content}, #{clientMsgId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderMessage message);

    @Select("""
            SELECT id, order_id, sender_id, type, content, client_msg_id, created_at
            FROM order_messages
            WHERE order_id = #{orderId} AND sender_id = #{senderId} AND client_msg_id = #{clientMsgId}
            """)
    OrderMessage findByClient(@Param("orderId") long orderId, @Param("senderId") long senderId, @Param("clientMsgId") String clientMsgId);

    @Select("""
            SELECT id, order_id, sender_id, type, content, client_msg_id, created_at
            FROM order_messages
            WHERE order_id = #{orderId}
            ORDER BY id DESC
            LIMIT #{limit}
            """)
    List<OrderMessage> latest(@Param("orderId") long orderId, @Param("limit") int limit);

    @Select("""
            SELECT id, order_id, sender_id, type, content, client_msg_id, created_at
            FROM order_messages
            WHERE order_id = #{orderId} AND id < #{beforeId}
            ORDER BY id DESC
            LIMIT #{limit}
            """)
    List<OrderMessage> before(@Param("orderId") long orderId, @Param("beforeId") long beforeId, @Param("limit") int limit);

    @Select("""
            SELECT id, order_id, sender_id, type, content, client_msg_id, created_at
            FROM order_messages
            WHERE order_id = #{orderId} AND id > #{afterId}
            ORDER BY id ASC
            LIMIT #{limit}
            """)
    List<OrderMessage> after(@Param("orderId") long orderId, @Param("afterId") long afterId, @Param("limit") int limit);

    @Select("SELECT MAX(id) FROM order_messages WHERE order_id = #{orderId}")
    Long maxId(long orderId);
}
