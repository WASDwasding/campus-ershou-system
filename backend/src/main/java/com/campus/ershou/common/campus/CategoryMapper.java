package com.campus.ershou.common.campus;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("""
            SELECT id, name, icon, sort
            FROM categories
            WHERE enabled = 1
            ORDER BY sort ASC, id ASC
            """)
    List<CategoryView> listEnabled();
}
