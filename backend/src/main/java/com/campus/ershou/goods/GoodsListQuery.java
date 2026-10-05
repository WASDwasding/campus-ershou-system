package com.campus.ershou.goods;

public class GoodsListQuery {

    private String keyword;
    private Long categoryId;
    private String condition;
    private Integer minPrice;
    private Integer maxPrice;
    private Long meetupBuildingId;
    private Long areaId;
    private String sort;
    private Long dormBuildingId;
    private Long dormAreaId;
    private int limit;
    private int offset;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getMeetupBuildingId() {
        return meetupBuildingId;
    }

    public void setMeetupBuildingId(Long meetupBuildingId) {
        this.meetupBuildingId = meetupBuildingId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Long getDormBuildingId() {
        return dormBuildingId;
    }

    public void setDormBuildingId(Long dormBuildingId) {
        this.dormBuildingId = dormBuildingId;
    }

    public Long getDormAreaId() {
        return dormAreaId;
    }

    public void setDormAreaId(Long dormAreaId) {
        this.dormAreaId = dormAreaId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
