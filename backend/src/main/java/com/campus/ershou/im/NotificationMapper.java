package com.campus.ershou.im;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Insert("""
            INSERT INTO notifications (user_id, type, title, content, ref_type, ref_id)
            VALUES (#{userId}, #{type}, #{title}, #{content}, #{refType}, #{refId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Select("""
            SELECT COUNT(*)
            FROM notifications
            WHERE user_id = #{userId} AND read_at IS NULL
            """)
    long countUnread(long userId);

    @Select("""
            <script>
            SELECT id, user_id, type, title, content, ref_type, ref_id, read_at, created_at
            FROM notifications
            WHERE user_id = #{userId}
            <if test="unreadOnly == true">AND read_at IS NULL</if>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<Notification> list(
            @Param("userId") long userId,
            @Param("unreadOnly") boolean unreadOnly,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM notifications
            WHERE user_id = #{userId}
            <if test="unreadOnly == true">AND read_at IS NULL</if>
            </script>
            """)
    long count(@Param("userId") long userId, @Param("unreadOnly") boolean unreadOnly);

    @Update("""
            UPDATE notifications
            SET read_at = CURRENT_TIMESTAMP(3)
            WHERE user_id = #{userId} AND read_at IS NULL
            """)
    int markAllRead(long userId);

    @Update("""
            <script>
            UPDATE notifications
            SET read_at = CURRENT_TIMESTAMP(3)
            WHERE user_id = #{userId}
              AND read_at IS NULL
              AND id IN
              <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
            </script>
            """)
    int markRead(@Param("userId") long userId, @Param("ids") List<Long> ids);
}
