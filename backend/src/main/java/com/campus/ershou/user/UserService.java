package com.campus.ershou.user;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.common.util.ContactInfo;
import com.campus.ershou.common.util.Masking;
import com.campus.ershou.user.dto.MeResponse;
import com.campus.ershou.user.dto.UpdateProfileRequest;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final BuildingMapper buildingMapper;
    private final FileService fileService;

    public UserService(UserMapper userMapper, BuildingMapper buildingMapper, FileService fileService) {
        this.userMapper = userMapper;
        this.buildingMapper = buildingMapper;
        this.fileService = fileService;
    }

    public MeResponse getMe(long userId) {
        UserEntity user = userMapper.findById(userId);
        if (user == null) {
            throw ApiException.of(ErrorCode.UNAUTHORIZED);
        }
        return toMe(user);
    }

    public MeResponse toMe(UserEntity user) {
        MeResponse.DormBuildingView dorm = null;
        if (user.getDormBuildingId() != null) {
            BuildingView building = buildingMapper.findEnabledById(user.getDormBuildingId());
            if (building != null) {
                dorm = new MeResponse.DormBuildingView(building.getId(), building.getName());
            }
        }
        return new MeResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                Masking.email(user.getEmail()),
                user.getStudentNoMasked(),
                fileService.publicUrlIfOwned(user.getAvatarFileId()),
                user.getBio(),
                user.getStatus(),
                user.getVerifyStatus(),
                dorm,
                user.getCreatedAt() == null ? null : user.getCreatedAt().atOffset(ZoneOffset.ofHours(8))
        );
    }

    public MeResponse updateProfile(long userId, UpdateProfileRequest request) {
        UserEntity user = requireUser(userId);
        if (request.isNicknamePresent()) {
            String nickname = request.getNickname() == null ? "" : request.getNickname().trim();
            if (nickname.isEmpty() || nickname.length() > 16) {
                throw ApiException.validation(List.of(new FieldErrorItem("nickname", "昵称长度为 1–16")));
            }
            if (ContactInfo.contains(nickname)) {
                throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
            }
            user.setNickname(nickname);
        }
        if (request.isBioPresent()) {
            String bio = request.getBio() == null ? "" : request.getBio().trim();
            if (bio.length() > 60) {
                throw ApiException.validation(List.of(new FieldErrorItem("bio", "简介最多 60 字")));
            }
            if (ContactInfo.contains(bio)) {
                throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
            }
            user.setBio(bio.isEmpty() ? null : bio);
        }
        if (request.isAvatarPresent()) {
            if (request.getAvatarFileId() == null || request.getAvatarFileId().isBlank()) {
                user.setAvatarFileId(null);
            } else {
                fileService.requireUsable(request.getAvatarFileId().trim(), userId, "AVATAR");
                user.setAvatarFileId(request.getAvatarFileId().trim());
            }
        }
        if (request.isDormPresent()) {
            if (request.getDormBuildingId() == null) {
                user.setDormBuildingId(null);
            } else {
                BuildingView building = requireDorm(request.getDormBuildingId());
                user.setDormBuildingId(building.getId());
            }
        }
        userMapper.updateProfile(user);
        return getMe(userId);
    }

    public BuildingView requireDorm(long buildingId) {
        BuildingView building = buildingMapper.findEnabledById(buildingId);
        if (building == null || !"DORM".equals(building.getType())) {
            throw ApiException.validation(List.of(new FieldErrorItem("dormBuildingId", "宿舍楼不存在")));
        }
        return building;
    }

    public UserEntity requireUser(long userId) {
        UserEntity user = userMapper.findById(userId);
        if (user == null) {
            throw ApiException.of(ErrorCode.UNAUTHORIZED);
        }
        return user;
    }
}
