package com.campus.ershou.admin;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AuditLogMapper {

    @Insert("""
            INSERT INTO audit_logs (category, event, admin_id, user_id, ip, detail)
            VALUES (#{category}, #{event}, #{adminId}, #{userId}, #{ip}, #{detail})
            """)
    int insert(
            @Param("category") String category,
            @Param("event") String event,
            @Param("adminId") Long adminId,
            @Param("userId") Long userId,
            @Param("ip") String ip,
            @Param("detail") String detail
    );

    @Select("""
            <script>
            SELECT id, category, event, admin_id, user_id, ip, detail, created_at
            FROM audit_logs
            WHERE 1 = 1
            <if test="category != null">AND category = #{category}</if>
            <if test="event != null">AND event = #{event}</if>
            <if test="userId != null">AND user_id = #{userId}</if>
            <if test="ip != null">AND ip = #{ip}</if>
            <if test="from != null">AND created_at &gt;= #{from}</if>
            <if test="to != null">AND created_at &lt; #{to}</if>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<AuditLog> search(
            @Param("category") String category,
            @Param("event") String event,
            @Param("userId") Long userId,
            @Param("ip") String ip,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
            <script>
            SELECT COUNT(*) FROM audit_logs
            WHERE 1 = 1
            <if test="category != null">AND category = #{category}</if>
            <if test="event != null">AND event = #{event}</if>
            <if test="userId != null">AND user_id = #{userId}</if>
            <if test="ip != null">AND ip = #{ip}</if>
            <if test="from != null">AND created_at &gt;= #{from}</if>
            <if test="to != null">AND created_at &lt; #{to}</if>
            </script>
            """)
    long count(
            @Param("category") String category,
            @Param("event") String event,
            @Param("userId") Long userId,
            @Param("ip") String ip,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
