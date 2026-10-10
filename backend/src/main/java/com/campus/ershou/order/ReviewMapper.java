package com.campus.ershou.order;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Insert("""
            INSERT INTO reviews (order_id, goods_id, reviewer_id, reviewee_id, score, content)
            VALUES (#{orderId}, #{goodsId}, #{reviewerId}, #{revieweeId}, #{score}, #{content})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReviewRecord review);

    @Select("""
            SELECT id, order_id, goods_id, reviewer_id, reviewee_id, score, content, created_at
            FROM reviews
            WHERE order_id = #{orderId}
            ORDER BY id ASC
            """)
    List<ReviewRecord> listByOrder(long orderId);

    @Select("""
            SELECT r.id, r.order_id, r.goods_id, r.reviewer_id, r.reviewee_id, r.score, r.content, r.created_at,
                   u.nickname AS reviewer_nickname
            FROM reviews r
            INNER JOIN users u ON u.id = r.reviewer_id
            WHERE r.reviewee_id = #{userId}
            ORDER BY r.id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<ReviewRecord> listForUser(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM reviews WHERE reviewee_id = #{userId}")
    long countForUser(long userId);

    @Select("SELECT CAST(ROUND(AVG(score), 2) AS CHAR) FROM reviews WHERE reviewee_id = #{userId}")
    String averageScore(long userId);
}
