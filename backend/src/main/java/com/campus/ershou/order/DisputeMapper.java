package com.campus.ershou.order;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DisputeMapper {

    String COLUMNS = """
            id, order_id, opener_id, reason, description, status, result, remark, created_at, resolved_at
            """;

    @Insert("""
            INSERT INTO disputes (order_id, opener_id, reason, description, status)
            VALUES (#{orderId}, #{openerId}, #{reason}, #{description}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DisputeRecord dispute);

    @Insert("INSERT INTO dispute_files (dispute_id, file_id, sort) VALUES (#{disputeId}, #{fileId}, #{sort})")
    int insertFile(@Param("disputeId") long disputeId, @Param("fileId") String fileId, @Param("sort") int sort);

    @Select("SELECT " + COLUMNS + " FROM disputes WHERE order_id = #{orderId} AND status = 'OPEN' ORDER BY id DESC LIMIT 1")
    DisputeRecord findOpen(long orderId);

    @Select("SELECT " + COLUMNS + " FROM disputes WHERE id = #{id}")
    DisputeRecord findById(long id);

    @Select("SELECT " + COLUMNS + " FROM disputes WHERE order_id = #{orderId} ORDER BY id DESC LIMIT 1")
    DisputeRecord findLatest(long orderId);

    @Select("SELECT file_id FROM dispute_files WHERE dispute_id = #{disputeId} ORDER BY sort ASC")
    List<String> listFiles(long disputeId);

    @Update("""
            UPDATE disputes
            SET status = 'WITHDRAWN', resolved_at = CURRENT_TIMESTAMP(3)
            WHERE id = #{id} AND status = 'OPEN' AND opener_id = #{openerId}
            """)
    int withdraw(@Param("id") long id, @Param("openerId") long openerId);

    @Update("""
            UPDATE disputes
            SET status = 'RESOLVED', result = #{result}, remark = #{remark}, resolved_at = CURRENT_TIMESTAMP(3)
            WHERE id = #{id} AND status = 'OPEN'
            """)
    int resolve(@Param("id") long id, @Param("result") String result, @Param("remark") String remark);

    @Select("SELECT COUNT(*) FROM disputes WHERE status = 'OPEN'")
    long countOpen();

    @Select("""
            <script>
            SELECT
            """ + COLUMNS + """
            FROM disputes
            WHERE 1 = 1
            <if test="status != null">AND status = #{status}</if>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<DisputeRecord> adminList(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("""
            <script>
            SELECT COUNT(*) FROM disputes WHERE 1 = 1
            <if test="status != null">AND status = #{status}</if>
            </script>
            """)
    long adminCount(@Param("status") String status);
}
