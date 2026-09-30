package com.campus.ershou.common.campus;

public class BuildingView {

    private Long id;
    private String name;
    private Long areaId;
    private String areaName;
    private String type;
    private Boolean meetupPoint;
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getMeetupPoint() {
        return meetupPoint;
    }

    public void setMeetupPoint(Boolean meetupPoint) {
        this.meetupPoint = meetupPoint;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
