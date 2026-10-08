package com.campus.ershou.admin;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {

    @Select("""
            SELECT id, username, email, password_hash, status, must_change_password, token_version, created_at, updated_at
            FROM admins
            WHERE id = #{id}
            """)
    AdminAccount findById(long id);

    @Select("""
            SELECT id, username, email, password_hash, status, must_change_password, token_version, created_at, updated_at
            FROM admins
            WHERE username = #{username}
            """)
    AdminAccount findByUsername(String username);

    @Insert("""
            INSERT INTO admins (username, email, password_hash, status, must_change_password, token_version)
            VALUES (#{username}, #{email}, #{passwordHash}, #{status}, #{mustChangePassword}, #{tokenVersion})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminAccount admin);

    @Update("UPDATE admins SET token_version = token_version + 1 WHERE id = #{id}")
    int bumpTokenVersion(long id);

    @Update("""
            UPDATE admins
            SET password_hash = #{passwordHash},
                must_change_password = 0,
                token_version = token_version + 1
            WHERE id = #{id}
            """)
    int updatePassword(AdminAccount admin);
}
