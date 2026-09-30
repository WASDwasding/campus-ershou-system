package com.campus.ershou.user;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("""
            SELECT id, student_no_hash, student_no_masked, username, nickname, email, password_hash,
                   avatar_file_id, bio, dorm_building_id, status, verify_status, ban_reason, ban_until,
                   token_version, created_at, updated_at
            FROM users
            WHERE id = #{id}
            """)
    UserEntity findById(long id);

    @Select("""
            SELECT id, student_no_hash, student_no_masked, username, nickname, email, password_hash,
                   avatar_file_id, bio, dorm_building_id, status, verify_status, ban_reason, ban_until,
                   token_version, created_at, updated_at
            FROM users
            WHERE student_no_hash = #{studentNoHash}
            """)
    UserEntity findByStudentNoHash(String studentNoHash);

    @Select("""
            SELECT id, student_no_hash, student_no_masked, username, nickname, email, password_hash,
                   avatar_file_id, bio, dorm_building_id, status, verify_status, ban_reason, ban_until,
                   token_version, created_at, updated_at
            FROM users
            WHERE username = #{username}
            """)
    UserEntity findByUsername(String username);

    @Select("""
            SELECT id, student_no_hash, student_no_masked, username, nickname, email, password_hash,
                   avatar_file_id, bio, dorm_building_id, status, verify_status, ban_reason, ban_until,
                   token_version, created_at, updated_at
            FROM users
            WHERE email = #{email}
            """)
    UserEntity findByEmail(String email);

    @Insert("""
            INSERT INTO users (
                student_no_hash, student_no_masked, username, nickname, email, password_hash,
                status, verify_status, token_version
            ) VALUES (
                #{studentNoHash}, #{studentNoMasked}, #{username}, #{nickname}, #{email}, #{passwordHash},
                #{status}, #{verifyStatus}, #{tokenVersion}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserEntity user);

    @Update("UPDATE users SET token_version = token_version + 1 WHERE id = #{id}")
    int bumpTokenVersion(long id);
}
