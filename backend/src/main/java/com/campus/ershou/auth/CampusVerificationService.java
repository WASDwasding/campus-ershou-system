package com.campus.ershou.auth;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.auth.dto.CampusVerificationRequest;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.campus.ershou.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CampusVerificationService {

    private final CampusVerificationMapper verificationMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final FileService fileService;
    private final BuildingMapper buildingMapper;

    public CampusVerificationService(
            CampusVerificationMapper verificationMapper,
            UserMapper userMapper,
            UserService userService,
            FileService fileService,
            BuildingMapper buildingMapper
    ) {
        this.verificationMapper = verificationMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.fileService = fileService;
        this.buildingMapper = buildingMapper;
    }

    public Map<String, Object> current(long userId) {
        UserEntity user = userService.requireUser(userId);
        return toView(user, verificationMapper.findLatest(userId));
    }

    @Transactional
    public Map<String, Object> submit(long userId, CampusVerificationRequest request) {
        UserEntity user = userService.requireUser(userId);
        if ("VERIFIED".equals(user.getVerifyStatus())) {
            throw ApiException.of(ErrorCode.ALREADY_VERIFIED);
        }
        if ("PENDING".equals(user.getVerifyStatus())) {
            throw ApiException.of(ErrorCode.VERIFICATION_PENDING);
        }
        BuildingView dorm = userService.requireDorm(request.dormBuildingId());
        fileService.requireUsable(request.cardImageFileId(), userId, "VERIFICATION");
        CampusVerification verification = new CampusVerification();
        verification.setUserId(userId);
        verification.setDormBuildingId(dorm.getId());
        verification.setCardFileId(request.cardImageFileId());
        verification.setStatus("PENDING");
        verificationMapper.insert(verification);
        user.setVerifyStatus("PENDING");
        user.setDormBuildingId(dorm.getId());
        userMapper.updateVerification(user);
        return toView(userMapper.findById(userId), verificationMapper.findLatest(userId));
    }

    private Map<String, Object> toView(UserEntity user, CampusVerification latest) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", user.getVerifyStatus());
        data.put("rejectReason", latest == null ? null : latest.getRejectReason());
        data.put("submittedAt", latest == null || latest.getCreatedAt() == null
                ? null
                : latest.getCreatedAt().atOffset(ZoneOffset.ofHours(8)));
        BuildingView dorm = null;
        if (user.getDormBuildingId() != null) {
            BuildingView found = buildingMapper.findEnabledById(user.getDormBuildingId());
            if (found != null && "DORM".equals(found.getType())) {
                dorm = found;
            }
        }
        data.put("dormBuilding", dorm == null ? null : Map.of("id", dorm.getId(), "name", dorm.getName()));
        return data;
    }
}
