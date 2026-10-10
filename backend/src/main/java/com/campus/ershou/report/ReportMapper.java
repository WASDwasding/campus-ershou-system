package com.campus.ershou.report;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReportMapper {

    String COLUMNS = """
            id, reporter_id, target_type, target_id, reason, description, status, action,
            message_snapshot, created_at, handled_at
            """;

    @Insert("""
            INSERT INTO reports (reporter_id, target_type, target_id, reason, description, status, message_snapshot)
            VALUES (#{reporterId}, #{targetType}, #{targetId}, #{reason}, #{description}, #{status}, #{messageSnapshot})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReportRecord report);

    @Insert("INSERT INTO report_files (report_id, file_id, sort) VALUES (#{reportId}, #{fileId}, #{sort})")
    int insertFile(@Param("reportId") long reportId, @Param("fileId") String fileId, @Param("sort") int sort);

    @Select("SELECT " + COLUMNS + " FROM reports WHERE id = #{id}")
    ReportRecord findById(long id);

    @Select("""
            SELECT COUNT(*) FROM reports
            WHERE reporter_id = #{reporterId} AND target_type = #{targetType} AND target_id = #{targetId}
              AND created_at > DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 24 HOUR)
            """)
    int countRecent(@Param("reporterId") long reporterId, @Param("targetType") String targetType, @Param("targetId") long targetId);

    @Select("SELECT file_id FROM report_files WHERE report_id = #{reportId} ORDER BY sort ASC")
    List<String> listFiles(long reportId);

    @Select("SELECT COUNT(*) FROM reports WHERE status = 'PENDING'")
    long countPending();

    @Select("""
            SELECT
            """ + COLUMNS + """
            FROM reports
            WHERE reporter_id = #{reporterId}
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<ReportRecord> listMine(@Param("reporterId") long reporterId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM reports WHERE reporter_id = #{reporterId}")
    long countMine(long reporterId);

    @Select("""
            <script>
            SELECT
            """ + COLUMNS + """
            FROM reports
            WHERE 1 = 1
            <if test="status != null">AND status = #{status}</if>
            <if test="targetType != null">AND target_type = #{targetType}</if>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<ReportRecord> adminList(@Param("status") String status, @Param("targetType") String targetType,
                                 @Param("offset") int offset, @Param("limit") int limit);

    @Select("""
            <script>
            SELECT COUNT(*) FROM reports WHERE 1 = 1
            <if test="status != null">AND status = #{status}</if>
            <if test="targetType != null">AND target_type = #{targetType}</if>
            </script>
            """)
    long adminCount(@Param("status") String status, @Param("targetType") String targetType);

    @Update("""
            UPDATE reports
            SET status = 'HANDLED', action = #{action}, handled_at = CURRENT_TIMESTAMP(3)
            WHERE id = #{id} AND status = 'PENDING'
            """)
    int handle(@Param("id") long id, @Param("action") String action);
}
