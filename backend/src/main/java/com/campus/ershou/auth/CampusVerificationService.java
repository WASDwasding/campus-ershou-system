package com.campus.ershou.auth;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.util.StudentNoHasher;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.auth.dto.CampusVerificationRequest;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.campus.ershou.user.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CampusVerificationService {

    private final CampusVerificationMapper verificationMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final FileService fileService;
    private final BuildingMapper buildingMapper;
    private final StudentNoHasher studentNoHasher;
    private final RateLimitService rateLimitService;

    public CampusVerificationService(
            CampusVerificationMapper verificationMapper,
            UserMapper userMapper,
            UserService userService,
            FileService fileService,
            BuildingMapper buildingMapper,
            StudentNoHasher studentNoHasher,
            RateLimitService rateLimitService
    ) {
        this.verificationMapper = verificationMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.fileService = fileService;
        this.buildingMapper = buildingMapper;
        this.studentNoHasher = studentNoHasher;
        this.rateLimitService = rateLimitService;
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
        String studentNo = request.studentNo();
        String hash = studentNoHasher.hash(studentNo);
        if (user.getStudentNoHash() != null && !user.getStudentNoHash().isBlank() && !user.getStudentNoHash().equals(hash)) {
            throw ApiException.validation(List.of(new FieldErrorItem("studentNo", "须与注册学号一致")));
        }
        UserEntity owner = userMapper.findByStudentNoHash(hash);
        if (owner != null && !owner.getId().equals(userId)) {
            throw ApiException.of(ErrorCode.STUDENT_NO_TAKEN);
        }
        BuildingView dorm = userService.requireDorm(request.dormBuildingId());
        fileService.requireUsable(request.cardImageFileId(), userId, "VERIFICATION");
        rateLimitService.check("verify-submit", String.valueOf(userId), 3, Duration.ofDays(1));
        if (user.getStudentNoHash() == null || user.getStudentNoHash().isBlank()) {
            user.setStudentNoHash(hash);
            user.setStudentNoMasked(studentNoHasher.mask(studentNo));
            if (userMapper.bindStudentNo(userId, hash, user.getStudentNoMasked()) != 1) {
                throw ApiException.of(ErrorCode.STUDENT_NO_TAKEN);
            }
        }
        CampusVerification verification = new CampusVerification();
        verification.setUserId(userId);
        verification.setRealName(request.realName());
        verification.setStudentNo(studentNo);
        verification.setCollege(request.college());
        verification.setDormBuildingId(dorm.getId());
        verification.setCardFileId(request.cardImageFileId());
        verification.setStatus("PENDING");
        try {
            verificationMapper.insert(verification);
        } catch (DuplicateKeyException ex) {
            throw ApiException.of(ErrorCode.STUDENT_NO_TAKEN);
        }
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
