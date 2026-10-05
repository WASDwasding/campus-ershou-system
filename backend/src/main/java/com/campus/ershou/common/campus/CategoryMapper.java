package com.campus.ershou.common.campus;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
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

    @Select("""
            SELECT id, name, icon, sort
            FROM categories
            WHERE id = #{id} AND enabled = 1
            """)
    CategoryView findEnabledById(long id);

    @Select("""
            <script>
            SELECT id, name, icon, sort
            FROM categories
            WHERE id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
            </script>
            """)
    List<CategoryView> findByIds(@Param("ids") Collection<Long> ids);
}
