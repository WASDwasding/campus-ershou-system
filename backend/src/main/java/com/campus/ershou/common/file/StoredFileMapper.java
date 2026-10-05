package com.campus.ershou.common.file;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StoredFileMapper {

    @Select("""
            SELECT id, owner_id, purpose, public_access, created_at
            FROM stored_files
            WHERE id = #{id}
            """)
    StoredFile findById(String id);

    @Insert("""
            INSERT INTO stored_files (id, owner_id, purpose, public_access)
            VALUES (#{id}, #{ownerId}, #{purpose}, #{publicAccess})
            """)
    int insert(StoredFile file);
}
