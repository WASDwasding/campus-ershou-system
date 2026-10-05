package com.campus.ershou.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class UpdateProfileRequest {

    private String nickname;
    private String avatarFileId;
    private String bio;
    private Long dormBuildingId;
    private boolean nicknamePresent;
    private boolean avatarPresent;
    private boolean bioPresent;
    private boolean dormPresent;

    public String getNickname() {
        return nickname;
    }

    @JsonSetter("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.nicknamePresent = true;
    }

    public String getAvatarFileId() {
        return avatarFileId;
    }

    @JsonSetter("avatarFileId")
    public void setAvatarFileId(String avatarFileId) {
        this.avatarFileId = avatarFileId;
        this.avatarPresent = true;
    }

    public String getBio() {
        return bio;
    }

    @JsonSetter("bio")
    public void setBio(String bio) {
        this.bio = bio;
        this.bioPresent = true;
    }

    public Long getDormBuildingId() {
        return dormBuildingId;
    }

    @JsonSetter("dormBuildingId")
    public void setDormBuildingId(Long dormBuildingId) {
        this.dormBuildingId = dormBuildingId;
        this.dormPresent = true;
    }

    @JsonIgnore
    public boolean isNicknamePresent() {
        return nicknamePresent;
    }

    @JsonIgnore
    public boolean isAvatarPresent() {
        return avatarPresent;
    }

    @JsonIgnore
    public boolean isBioPresent() {
        return bioPresent;
    }

    @JsonIgnore
    public boolean isDormPresent() {
        return dormPresent;
    }
}
