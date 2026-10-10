package com.campus.ershou.auth;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.file.FileUrls;
import com.campus.ershou.common.util.Masking;
import com.campus.ershou.im.NotificationService;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminVerificationService {

    private static final Set<String> STATUSES = Set.of("PENDING", "APPROVED", "REJECTED");

    private final CampusVerificationMapper verificationMapper;
    private final UserMapper userMapper;
    private final BuildingMapper buildingMapper;
    private final FileUrls fileUrls;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public AdminVerificationService(
            CampusVerificationMapper verificationMapper,
            UserMapper userMapper,
            BuildingMapper buildingMapper,
            FileUrls fileUrls,
            NotificationService notificationService,
            AuditService auditService
    ) {
        this.verificationMapper = verificationMapper;
        this.userMapper = userMapper;
        this.buildingMapper = buildingMapper;
        this.fileUrls = fileUrls;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    public long countPending() {
        return verificationMapper.count("PENDING");
    }

    public Map<String, Object> list(String status, Integer page, Integer size) {
        String normalized = normalizeStatus(status);
        int[] paging = Paging.page(page, size);
        int offset = (paging[0] - 1) * paging[1];
        List<CampusVerification> rows = verificationMapper.list(normalized, offset, paging[1]);
        long total = verificationMapper.count(normalized);
        Lookup lookup = lookup(rows);
        List<Map<String, Object>> list = rows.stream()
                .map(row -> toSummary(row, lookup))
                .toList();
        return Paging.of(list, paging[0], paging[1], total);
    }

    public Map<String, Object> detail(long id) {
        CampusVerification verification = verificationMapper.findById(id);
        if (verification == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        return toDetail(verification);
    }

    @Transactional
    public Map<String, Object> approve(long adminId, long id, String ip) {
        CampusVerification verification = review(adminId, id, "APPROVED", null, ip, "ADMIN_VERIFY_APPROVE");
        notificationService.notifyVerificationApproved(verification.getUserId());
        return toDetail(verificationMapper.findById(id));
    }

    @Transactional
    public Map<String, Object> reject(long adminId, long id, String reason, String ip) {
        CampusVerification verification = review(adminId, id, "REJECTED", reason, ip, "ADMIN_VERIFY_REJECT");
        notificationService.notifyVerificationRejected(verification.getUserId(), reason);
        return toDetail(verificationMapper.findById(id));
    }

    private CampusVerification review(
            long adminId,
            long id,
            String status,
            String reason,
            String ip,
            String event
    ) {
        CampusVerification current = verificationMapper.findById(id);
        if (current == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        CampusVerification update = new CampusVerification();
        update.setId(id);
        update.setStatus(status);
        update.setRejectReason(reason);
        update.setReviewedBy(adminId);
        if (verificationMapper.reviewIfPending(update) != 1) {
            throw ApiException.of(ErrorCode.VERIFICATION_NOT_PENDING);
        }
        String userStatus = "APPROVED".equals(status) ? "VERIFIED" : "REJECTED";
        if (userMapper.updateVerifyStatusIfPending(current.getUserId(), userStatus) != 1) {
            throw ApiException.of(ErrorCode.VERIFICATION_NOT_PENDING);
        }
        auditService.verificationReviewed(event, adminId, current.getUserId(), id, ip);
        current.setStatus(status);
        current.setRejectReason(reason);
        current.setReviewedBy(adminId);
        return current;
    }

    private Map<String, Object> toDetail(CampusVerification verification) {
        Lookup lookup = lookup(List.of(verification));
        Map<String, Object> data = toSummary(verification, lookup);
        UserEntity user = lookup.users.get(verification.getUserId());
        Map<String, Object> userView = userView(user);
        userView.put("verifyStatus", user == null ? null : user.getVerifyStatus());
        data.put("user", userView);
        long exp = fileUrls.privateExpiryEpoch();
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("fileId", verification.getCardFileId());
        card.put("url", fileUrls.privateUrl(verification.getCardFileId(), "", exp));
        card.put("thumbUrl", fileUrls.privateUrl(verification.getCardFileId(), "thumb", exp));
        data.put("cardImage", card);
        List<Map<String, Object>> history = verificationMapper.listByUser(verification.getUserId()).stream()
                .filter(item -> !item.getId().equals(verification.getId()))
                .map(AdminVerificationService::toHistory)
                .toList();
        data.put("history", history);
        return data;
    }

    private Map<String, Object> toSummary(CampusVerification verification, Lookup lookup) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", verification.getId());
        data.put("status", verification.getStatus());
        data.put("rejectReason", verification.getRejectReason());
        data.put("user", userView(lookup.users.get(verification.getUserId())));
        data.put("dormBuilding", buildingView(lookup.buildings.get(verification.getDormBuildingId())));
        data.put("submittedAt", at(verification.getCreatedAt()));
        data.put("reviewedAt", at(verification.getReviewedAt()));
        data.put("reviewedBy", verification.getReviewedBy());
        return data;
    }

    private static Map<String, Object> toHistory(CampusVerification verification) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", verification.getId());
        data.put("status", verification.getStatus());
        data.put("rejectReason", verification.getRejectReason());
        data.put("submittedAt", at(verification.getCreatedAt()));
        data.put("reviewedAt", at(verification.getReviewedAt()));
        return data;
    }

    private Lookup lookup(List<CampusVerification> rows) {
        Set<Long> userIds = rows.stream().map(CampusVerification::getUserId).collect(Collectors.toSet());
        Set<Long> buildingIds = rows.stream().map(CampusVerification::getDormBuildingId).collect(Collectors.toSet());
        Map<Long, UserEntity> users = userIds.isEmpty()
                ? Map.of()
                : userMapper.findByIds(userIds).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));
        Map<Long, BuildingView> buildings = buildingIds.isEmpty()
                ? Map.of()
                : buildingMapper.findByIds(buildingIds).stream().collect(Collectors.toMap(BuildingView::getId, Function.identity()));
        return new Lookup(users, buildings);
    }

    private static Map<String, Object> userView(UserEntity user) {
        Map<String, Object> data = new LinkedHashMap<>();
        if (user == null) {
            return data;
        }
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("studentNoMasked", user.getStudentNoMasked());
        data.put("emailMasked", Masking.email(user.getEmail()));
        return data;
    }

    private static Map<String, Object> buildingView(BuildingView building) {
        if (building == null) {
            return null;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", building.getId());
        data.put("name", building.getName());
        return data;
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!STATUSES.contains(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("status", "状态不合法")));
        }
        return normalized;
    }

    private static OffsetDateTime at(LocalDateTime time) {
        return time == null ? null : time.atOffset(ZoneOffset.ofHours(8));
    }

    private record Lookup(Map<Long, UserEntity> users, Map<Long, BuildingView> buildings) {
    }
}
