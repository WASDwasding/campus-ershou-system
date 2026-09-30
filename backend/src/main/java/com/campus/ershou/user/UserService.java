package com.campus.ershou.user;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.util.Masking;
import com.campus.ershou.user.dto.MeResponse;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final BuildingMapper buildingMapper;

    public UserService(UserMapper userMapper, BuildingMapper buildingMapper) {
        this.userMapper = userMapper;
        this.buildingMapper = buildingMapper;
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
                null,
                user.getBio(),
                user.getStatus(),
                user.getVerifyStatus(),
                dorm,
                user.getCreatedAt() == null ? null : user.getCreatedAt().atOffset(ZoneOffset.ofHours(8))
        );
    }
}
