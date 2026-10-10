package com.campus.ershou.common.campus;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminCatalog {
}

@Mapper
interface AdminCatalogMapper {

    @Select("SELECT id, name, icon, sort, enabled FROM categories ORDER BY sort ASC, id ASC")
    List<CatalogRow> categories();

    @Select("SELECT id FROM categories WHERE id = #{id}")
    Long categoryId(long id);

    @Insert("INSERT INTO categories (name, icon, sort, enabled) VALUES (#{name}, #{icon}, #{sort}, #{enabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCategory(CatalogRow row);

    @Update("UPDATE categories SET name = #{name}, icon = #{icon}, sort = #{sort}, enabled = #{enabled} WHERE id = #{id}")
    int updateCategory(CatalogRow row);

    @Select("""
            SELECT b.id, b.name, b.area_id, a.name AS area_name, b.type, b.meetup_point, b.sort, b.enabled
            FROM buildings b
            INNER JOIN areas a ON a.id = b.area_id
            ORDER BY b.sort ASC, b.id ASC
            """)
    List<CatalogRow> buildings();

    @Select("SELECT id FROM areas WHERE id = #{id}")
    Long areaId(long id);

    @Insert("""
            INSERT INTO buildings (name, area_id, type, meetup_point, sort, enabled)
            VALUES (#{name}, #{areaId}, #{type}, #{meetupPoint}, #{sort}, #{enabled})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertBuilding(CatalogRow row);

    @Update("""
            UPDATE buildings
            SET name = #{name}, area_id = #{areaId}, type = #{type}, meetup_point = #{meetupPoint},
                sort = #{sort}, enabled = #{enabled}
            WHERE id = #{id}
            """)
    int updateBuilding(CatalogRow row);
}

class CatalogRow {
    private Long id;
    private String name;
    private String icon;
    private Integer sort;
    private Boolean enabled;
    private Long areaId;
    private String areaName;
    private String type;
    private Boolean meetupPoint;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getMeetupPoint() { return meetupPoint; }
    public void setMeetupPoint(Boolean meetupPoint) { this.meetupPoint = meetupPoint; }
}

@Service
class AdminCatalogService {

    private static final Set<String> BUILDING_TYPES = Set.of("DORM", "TEACHING", "CANTEEN", "LIBRARY", "GATE", "OTHER");

    private final AdminCatalogMapper mapper;

    AdminCatalogService(AdminCatalogMapper mapper) {
        this.mapper = mapper;
    }

    public Map<String, Object> categories() {
        return Map.of("list", mapper.categories().stream().map(AdminCatalogService::category).toList());
    }

    public Map<String, Object> createCategory(String name, String icon, Integer sort, Boolean enabled) {
        CatalogRow row = categoryRow(null, name, icon, sort, enabled);
        mapper.insertCategory(row);
        return category(row);
    }

    public Map<String, Object> updateCategory(long id, String name, String icon, Integer sort, Boolean enabled) {
        if (mapper.categoryId(id) == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        CatalogRow row = categoryRow(id, name, icon, sort, enabled);
        mapper.updateCategory(row);
        return category(row);
    }

    public Map<String, Object> buildings() {
        return Map.of("list", mapper.buildings().stream().map(AdminCatalogService::building).toList());
    }

    public Map<String, Object> createBuilding(String name, Long areaId, String type, Boolean meetupPoint, Integer sort, Boolean enabled) {
        CatalogRow row = buildingRow(null, name, areaId, type, meetupPoint, sort, enabled);
        mapper.insertBuilding(row);
        row.setAreaName(null);
        return building(row);
    }

    public Map<String, Object> updateBuilding(long id, String name, Long areaId, String type, Boolean meetupPoint, Integer sort, Boolean enabled) {
        CatalogRow row = buildingRow(id, name, areaId, type, meetupPoint, sort, enabled);
        if (mapper.updateBuilding(row) != 1) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        return building(row);
    }

    private CatalogRow categoryRow(Long id, String name, String icon, Integer sort, Boolean enabled) {
        String cleanName = requireText(name, "name", 1, 32);
        String cleanIcon = icon == null ? "" : icon.trim();
        if (cleanIcon.length() > 64) {
            throw ApiException.validation(List.of(new FieldErrorItem("icon", "图标键最多 64 字")));
        }
        CatalogRow row = new CatalogRow();
        row.setId(id);
        row.setName(cleanName);
        row.setIcon(cleanIcon);
        row.setSort(sort == null ? 0 : sort);
        row.setEnabled(enabled == null || enabled);
        return row;
    }

    private CatalogRow buildingRow(Long id, String name, Long areaId, String type, Boolean meetupPoint, Integer sort, Boolean enabled) {
        if (areaId == null || mapper.areaId(areaId) == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("areaId", "区域不存在")));
        }
        String cleanType = type == null ? "" : type.trim().toUpperCase();
        if (!BUILDING_TYPES.contains(cleanType)) {
            throw ApiException.validation(List.of(new FieldErrorItem("type", "楼栋类型不合法")));
        }
        CatalogRow row = new CatalogRow();
        row.setId(id);
        row.setName(requireText(name, "name", 1, 32));
        row.setAreaId(areaId);
        row.setType(cleanType);
        row.setMeetupPoint(Boolean.TRUE.equals(meetupPoint));
        row.setSort(sort == null ? 0 : sort);
        row.setEnabled(enabled == null || enabled);
        return row;
    }

    private static Map<String, Object> category(CatalogRow row) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", row.getId());
        data.put("name", row.getName());
        data.put("icon", row.getIcon());
        data.put("sort", row.getSort());
        data.put("enabled", Boolean.TRUE.equals(row.getEnabled()));
        return data;
    }

    private static Map<String, Object> building(CatalogRow row) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", row.getId());
        data.put("name", row.getName());
        data.put("areaId", row.getAreaId());
        data.put("areaName", row.getAreaName());
        data.put("type", row.getType());
        data.put("meetupPoint", Boolean.TRUE.equals(row.getMeetupPoint()));
        data.put("sort", row.getSort());
        data.put("enabled", row.getEnabled() == null || row.getEnabled());
        return data;
    }

    private static String requireText(String value, String field, int min, int max) {
        String text = value == null ? "" : value.trim();
        int length = text.codePointCount(0, text.length());
        if (length < min || length > max) {
            throw ApiException.validation(List.of(new FieldErrorItem(field, "长度 " + min + "–" + max + " 字")));
        }
        return text;
    }
}
