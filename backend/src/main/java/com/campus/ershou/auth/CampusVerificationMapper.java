package com.campus.ershou.auth;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CampusVerificationMapper {

    @Select("""
            SELECT id, user_id, dorm_building_id, card_file_id, status, reject_reason, created_at, reviewed_at, reviewed_by
            FROM campus_verifications
            WHERE user_id = #{userId}
            ORDER BY id DESC
            LIMIT 1
            """)
    CampusVerification findLatest(long userId);

    @Select("""
            SELECT id, user_id, dorm_building_id, card_file_id, status, reject_reason, created_at, reviewed_at, reviewed_by
            FROM campus_verifications
            WHERE id = #{id}
            """)
    CampusVerification findById(long id);

    @Select("""
            SELECT id, user_id, dorm_building_id, card_file_id, status, reject_reason, created_at, reviewed_at, reviewed_by
            FROM campus_verifications
            WHERE user_id = #{userId}
            ORDER BY id DESC
            """)
    List<CampusVerification> listByUser(long userId);

    @Select("""
            <script>
            SELECT id, user_id, dorm_building_id, card_file_id, status, reject_reason, created_at, reviewed_at, reviewed_by
            FROM campus_verifications
            <where>
                <if test="status != null">status = #{status}</if>
            </where>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<CampusVerification> list(
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM campus_verifications
            <where>
                <if test="status != null">status = #{status}</if>
            </where>
            </script>
            """)
    long count(@Param("status") String status);

    @Insert("""
            INSERT INTO campus_verifications (user_id, dorm_building_id, card_file_id, status)
            VALUES (#{userId}, #{dormBuildingId}, #{cardFileId}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CampusVerification verification);

    @Update("""
            UPDATE campus_verifications
            SET status = #{status},
                reject_reason = #{rejectReason},
                reviewed_at = CURRENT_TIMESTAMP(3),
                reviewed_by = #{reviewedBy}
            WHERE id = #{id} AND status = 'PENDING'
            """)
    int reviewIfPending(CampusVerification verification);
}
