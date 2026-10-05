package com.campus.ershou.auth;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CampusVerificationMapper {

    @Select("""
            SELECT id, user_id, dorm_building_id, card_file_id, status, reject_reason, created_at, reviewed_at
            FROM campus_verifications
            WHERE user_id = #{userId}
            ORDER BY id DESC
            LIMIT 1
            """)
    CampusVerification findLatest(long userId);

    @Insert("""
            INSERT INTO campus_verifications (user_id, dorm_building_id, card_file_id, status)
            VALUES (#{userId}, #{dormBuildingId}, #{cardFileId}, #{status})
            """)
    int insert(CampusVerification verification);
}
