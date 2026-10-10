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

    @Update("""
            UPDATE users
            SET verify_status = #{verifyStatus}
            WHERE id = #{id} AND verify_status = 'PENDING'
            """)
    int updateVerifyStatusIfPending(@Param("id") long id, @Param("verifyStatus") String verifyStatus);

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

    @Update("""
            UPDATE users
            SET status = 'BANNED', ban_reason = #{reason}, ban_until = #{until}, token_version = token_version + 1
            WHERE id = #{id} AND status = 'ACTIVE'
            """)
    int ban(@Param("id") long id, @Param("reason") String reason, @Param("until") java.time.LocalDateTime until);

    @Update("""
            UPDATE users
            SET status = 'ACTIVE', ban_reason = NULL, ban_until = NULL
            WHERE id = #{id} AND status = 'BANNED'
            """)
    int unban(long id);

    @Select("""
            SELECT id, student_no_hash, student_no_masked, username, nickname, email, password_hash,
                   avatar_file_id, bio, dorm_building_id, status, verify_status, ban_reason, ban_until,
                   token_version, created_at, updated_at
            FROM users
            WHERE dev_openid = #{devOpenid}
            """)
    UserEntity findByDevOpenid(String devOpenid);

    @Insert("""
            INSERT INTO users (
                student_no_hash, student_no_masked, username, nickname, email, password_hash,
                status, verify_status, token_version, dev_openid
            ) VALUES (
                #{studentNoHash}, #{studentNoMasked}, #{username}, #{nickname}, #{email}, #{passwordHash},
                #{status}, #{verifyStatus}, #{tokenVersion}, #{devOpenid}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertDev(UserEntity user);

    @Select("""
            <script>
            SELECT id, student_no_masked, username, nickname, email, avatar_file_id, bio, dorm_building_id,
                   status, verify_status, ban_reason, ban_until, created_at
            FROM users
            WHERE 1 = 1
            <if test="status != null">AND status = #{status}</if>
            <if test="verifyStatus != null">AND verify_status = #{verifyStatus}</if>
            <if test="keyword != null">
              AND (username LIKE #{keyword} ESCAPE '\\\\' OR nickname LIKE #{keyword} ESCAPE '\\\\' OR email LIKE #{keyword} ESCAPE '\\\\')
            </if>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<UserEntity> adminSearch(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("verifyStatus") String verifyStatus,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
            <script>
            SELECT COUNT(*) FROM users
            WHERE 1 = 1
            <if test="status != null">AND status = #{status}</if>
            <if test="verifyStatus != null">AND verify_status = #{verifyStatus}</if>
            <if test="keyword != null">
              AND (username LIKE #{keyword} ESCAPE '\\\\' OR nickname LIKE #{keyword} ESCAPE '\\\\' OR email LIKE #{keyword} ESCAPE '\\\\')
            </if>
            </script>
            """)
    long adminCount(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("verifyStatus") String verifyStatus
    );
}
