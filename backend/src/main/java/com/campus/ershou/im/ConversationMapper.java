package com.campus.ershou.im;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ConversationMapper {

    @Select("""
            SELECT id, goods_id, buyer_id, seller_id, buyer_last_read, seller_last_read, last_message_at, created_at
            FROM conversations WHERE id = #{id}
            """)
    ConversationRecord findById(long id);

    @Select("""
            SELECT id, goods_id, buyer_id, seller_id, buyer_last_read, seller_last_read, last_message_at, created_at
            FROM conversations WHERE goods_id = #{goodsId} AND buyer_id = #{buyerId}
            """)
    ConversationRecord findByGoodsBuyer(@Param("goodsId") long goodsId, @Param("buyerId") long buyerId);

    @Insert("""
            INSERT INTO conversations (goods_id, buyer_id, seller_id)
            VALUES (#{goodsId}, #{buyerId}, #{sellerId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ConversationRecord conversation);

    @Select("""
            SELECT id, goods_id, buyer_id, seller_id, buyer_last_read, seller_last_read, last_message_at, created_at
            FROM conversations
            WHERE buyer_id = #{userId} OR seller_id = #{userId}
            ORDER BY IFNULL(last_message_at, created_at) DESC, id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<ConversationRecord> listForUser(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM conversations WHERE buyer_id = #{userId} OR seller_id = #{userId}")
    long countForUser(long userId);

    @Insert("""
            INSERT INTO messages (conversation_id, sender_id, type, content, raw_content, client_msg_id, masked)
            VALUES (#{conversationId}, #{senderId}, #{type}, #{content}, #{rawContent}, #{clientMsgId}, #{masked})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMessage(MessageRecord message);

    @Select("""
            SELECT id, conversation_id, sender_id, type, content, raw_content, client_msg_id, masked, created_at
            FROM messages
            WHERE conversation_id = #{conversationId} AND sender_id = #{senderId} AND client_msg_id = #{clientMsgId}
            """)
    MessageRecord findByClientMsg(@Param("conversationId") long conversationId, @Param("senderId") long senderId,
                                  @Param("clientMsgId") String clientMsgId);

    @Select("SELECT id, conversation_id, sender_id, type, content, raw_content, client_msg_id, masked, created_at FROM messages WHERE id = #{id}")
    MessageRecord findMessage(long id);

    @Select("""
            <script>
            SELECT id, conversation_id, sender_id, type, content, raw_content, client_msg_id, masked, created_at
            FROM messages
            WHERE conversation_id = #{conversationId}
            <if test="beforeId != null">AND id &lt; #{beforeId}</if>
            <if test="afterId != null">AND id &gt; #{afterId}</if>
            ORDER BY id
            <choose>
              <when test="afterId != null">ASC</when>
              <otherwise>DESC</otherwise>
            </choose>
            LIMIT #{limit}
            </script>
            """)
    List<MessageRecord> listMessages(@Param("conversationId") long conversationId, @Param("beforeId") Long beforeId,
                                     @Param("afterId") Long afterId, @Param("limit") int limit);

    @Update("UPDATE conversations SET buyer_last_read = #{messageId}, last_message_at = last_message_at WHERE id = #{id} AND buyer_id = #{userId}")
    int readBuyer(@Param("id") long id, @Param("userId") long userId, @Param("messageId") long messageId);

    @Update("UPDATE conversations SET seller_last_read = #{messageId} WHERE id = #{id} AND seller_id = #{userId}")
    int readSeller(@Param("id") long id, @Param("userId") long userId, @Param("messageId") long messageId);

    @Update("UPDATE conversations SET last_message_at = CURRENT_TIMESTAMP(3) WHERE id = #{id}")
    int touch(long id);

    @Select("""
            SELECT COUNT(*)
            FROM messages m
            INNER JOIN conversations c ON c.id = m.conversation_id
            WHERE (c.buyer_id = #{userId} OR c.seller_id = #{userId})
              AND (m.sender_id IS NULL OR m.sender_id != #{userId})
              AND m.id > IF(c.buyer_id = #{userId}, c.buyer_last_read, c.seller_last_read)
            """)
    int countUnread(long userId);
}
