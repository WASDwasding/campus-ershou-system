package com.campus.ershou.user;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

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

    @Update("""
            UPDATE users
            SET nickname = #{nickname},
                bio = #{bio},
                avatar_file_id = #{avatarFileId},
                dorm_building_id = #{dormBuildingId}
            WHERE id = #{id}
            """)
    int updateProfile(UserEntity user);

    @Update("""
            UPDATE users
            SET verify_status = #{verifyStatus},
                dorm_building_id = #{dormBuildingId}
            WHERE id = #{id}
            """)
    int updateVerification(UserEntity user);

    @Select("""
            <script>
            SELECT id, student_no_hash, student_no_masked, username, nickname, email, password_hash,
                   avatar_file_id, bio, dorm_building_id, status, verify_status, ban_reason, ban_until,
                   token_version, created_at, updated_at
            FROM users
            WHERE id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
            </script>
            """)
    List<UserEntity> findByIds(@Param("ids") Collection<Long> ids);
}
