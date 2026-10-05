package com.campus.ershou.common.campus;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface BuildingMapper {

    @Select("""
            SELECT b.id, b.name, b.area_id, a.name AS area_name, b.type, b.meetup_point, b.sort
            FROM buildings b
            INNER JOIN areas a ON a.id = b.area_id
            WHERE b.enabled = 1
              AND (#{type} IS NULL OR b.type = #{type})
              AND (#{meetupOnly} = FALSE OR b.meetup_point = 1)
            ORDER BY b.sort ASC, b.id ASC
            """)
    List<BuildingView> listEnabled(@Param("type") String type, @Param("meetupOnly") boolean meetupOnly);

    @Select("""
            SELECT b.id, b.name, b.area_id, a.name AS area_name, b.type, b.meetup_point, b.sort
            FROM buildings b
            INNER JOIN areas a ON a.id = b.area_id
            WHERE b.id = #{id} AND b.enabled = 1
            """)
    BuildingView findEnabledById(long id);

    @Select("""
            <script>
            SELECT b.id, b.name, b.area_id, a.name AS area_name, b.type, b.meetup_point, b.sort
            FROM buildings b
            INNER JOIN areas a ON a.id = b.area_id
            WHERE b.id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
            </script>
            """)
    List<BuildingView> findByIds(@Param("ids") Collection<Long> ids);
}
