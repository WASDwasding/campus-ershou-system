package com.campus.ershou.report;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.common.file.FileUrls;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.goods.GoodsAdminService;
import com.campus.ershou.goods.GoodsRow;
import com.campus.ershou.goods.GoodsService;
import com.campus.ershou.im.ConversationService;
import com.campus.ershou.im.MessageRecord;
import com.campus.ershou.im.ConversationMapper;
import com.campus.ershou.im.NotificationService;
import com.campus.ershou.order.OrderRecord;
import com.campus.ershou.order.OrderMapper;
import com.campus.ershou.user.AdminUserService;
import com.campus.ershou.user.UserMapper;
import com.campus.ershou.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ReportService {

    private static final Set<String> TARGETS = Set.of("GOODS", "USER", "MESSAGE");
    private static final Set<String> REASONS = Set.of("FAKE", "FRAUD", "HARASSMENT", "ILLEGAL", "OTHER");
    private static final Set<String> ACTIONS = Set.of("DISMISS", "WARN", "REMOVE_GOODS", "BAN_USER");

    private final ReportMapper reportMapper;
    private final GoodsService goodsService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ConversationMapper conversationMapper;
    private final ConversationService conversationService;
    private final FileService fileService;
    private final FileUrls fileUrls;
    private final RateLimitService rateLimitService;
    private final NotificationService notificationService;
    private final AdminUserService adminUserService;
    private final com.campus.ershou.goods.GoodsAdminService goodsAdminService;
    private final AuditService auditService;

    public ReportService(
            ReportMapper reportMapper,
            GoodsService goodsService,
            UserService userService,
            UserMapper userMapper,
            OrderMapper orderMapper,
            ConversationMapper conversationMapper,
            ConversationService conversationService,
            FileService fileService,
            FileUrls fileUrls,
            RateLimitService rateLimitService,
            NotificationService notificationService,
            AdminUserService adminUserService,
            com.campus.ershou.goods.GoodsAdminService goodsAdminService,
            AuditService auditService
    ) {
        this.reportMapper = reportMapper;
        this.goodsService = goodsService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.conversationMapper = conversationMapper;
        this.conversationService = conversationService;
        this.fileService = fileService;
        this.fileUrls = fileUrls;
        this.rateLimitService = rateLimitService;
        this.notificationService = notificationService;
        this.adminUserService = adminUserService;
        this.goodsAdminService = goodsAdminService;
        this.auditService = auditService;
    }

    @Transactional
    public Map<String, Object> create(long userId, Long goodsId, String description, List<String> evidence) {
        if (!"VERIFIED".equals(userService.requireUser(userId).getVerifyStatus())) {
            throw ApiException.of(ErrorCode.NOT_VERIFIED);
        }
        if (goodsId == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("goodsId", "请选择商品")));
        }
        String text = description == null ? "" : description.trim();
        int length = text.codePointCount(0, text.length());
        if (length < 1 || length > 300) {
            throw ApiException.validation(List.of(new FieldErrorItem("description", "说明长度为 1–300")));
        }
        List<String> files = evidence == null ? List.of() : evidence;
        if (files.size() > 3) {
            throw ApiException.validation(List.of(new FieldErrorItem("evidenceFileIds", "最多 3 张")));
        }
        GoodsRow goods = goodsService.findVisible(userId, goodsId);
        if (goods == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (goods.getSellerId() != null && goods.getSellerId() == userId) {
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        for (String fileId : files) {
            fileService.requireUsable(fileId, userId, "EVIDENCE");
        }
        if (reportMapper.countPendingOf(userId, goodsId) > 0) {
            throw ApiException.of(ErrorCode.DUPLICATE_REPORT);
        }
        rateLimitService.check("report", String.valueOf(userId), 10, Duration.ofDays(1));
        ReportRecord report = new ReportRecord();
        report.setReporterId(userId);
        report.setTargetType("GOODS");
        report.setTargetId(goodsId);
        report.setDescription(text);
        report.setStatus("PENDING");
        reportMapper.insert(report);
        for (int i = 0; i < files.size(); i++) {
            reportMapper.insertFile(report.getId(), files.get(i), i);
        }
        return mineView(reportMapper.findById(report.getId()));
    }

    public Map<String, Object> mine(long userId, Integer page, Integer size) {
        int[] paging = Paging.page(page, size);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReportRecord report : reportMapper.listMine(userId, (paging[0] - 1) * paging[1], paging[1])) {
            list.add(mineView(report));
        }
        return Paging.of(list, paging[0], paging[1], reportMapper.countMine(userId));
    }

    public Map<String, Object> adminList(String status, String targetType, Integer page, Integer size) {
        String normalizedStatus = blank(status);
        String normalizedType = blank(targetType);
        if (normalizedStatus != null && !Set.of("PENDING", "DISMISSED", "REMOVED").contains(normalizedStatus.toUpperCase())) {
            throw ApiException.validation(List.of(new FieldErrorItem("status", "举报状态不合法")));
        }
        if (normalizedType != null && !TARGETS.contains(normalizedType.toUpperCase())) {
            throw ApiException.validation(List.of(new FieldErrorItem("targetType", "对象类型不合法")));
        }
        int[] paging = Paging.page(page, size);
        String statusValue = normalizedStatus == null ? null : normalizedStatus.toUpperCase();
        String typeValue = normalizedType == null ? null : normalizedType.toUpperCase();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReportRecord report : reportMapper.adminList(statusValue, typeValue, (paging[0] - 1) * paging[1], paging[1])) {
            list.add(adminView(report, false));
        }
        return Paging.of(list, paging[0], paging[1], reportMapper.adminCount(statusValue, typeValue));
    }

    public Map<String, Object> adminDetail(long id) {
        ReportRecord report = reportMapper.findById(id);
        if (report == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        return adminView(report, true);
    }

    @Transactional
    public Map<String, Object> handle(long adminId, long id, String action, String reason, String ip) {
        String normalized = action == null ? "" : action.trim().toUpperCase();
        if (!"DISMISS".equals(normalized) && !"REMOVE_GOODS".equals(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("action", "只能是 REMOVE_GOODS 或 DISMISS")));
        }
        String text = reason == null ? "" : reason.trim();
        int length = text.codePointCount(0, text.length());
        if (length < 2 || length > 100) {
            throw ApiException.validation(List.of(new FieldErrorItem("reason", "理由 2–100 字")));
        }
        ReportRecord report = reportMapper.findById(id);
        if (report == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (!"PENDING".equals(report.getStatus())) {
            throw ApiException.of(ErrorCode.REPORT_ALREADY_HANDLED);
        }
        String status = "DISMISS".equals(normalized) ? "DISMISSED" : "REMOVED";
        if ("REMOVE_GOODS".equals(normalized)) {
            goodsAdminService.remove(adminId, report.getTargetId(), text, ip);
        }
        if (reportMapper.handle(id, status, normalized, text) != 1) {
            throw ApiException.of(ErrorCode.REPORT_ALREADY_HANDLED);
        }
        notificationService.notify(report.getReporterId(), "REPORT_HANDLED", "举报已处理", text, "REPORT", id);
        auditService.record("ADMIN", "ADMIN_REPORT_HANDLE", adminId, report.getReporterId(), ip,
                "{\"reportId\":" + id + ",\"action\":\"" + normalized + "\"}");
        return adminDetail(id);
    }

    public long countPending() {
        return reportMapper.countPending();
    }

    private Map<String, Object> mineView(ReportRecord report) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", report.getId());
        data.put("goodsId", report.getTargetId());
        data.put("description", report.getDescription());
        data.put("reason", report.getHandleReason());
        data.put("status", report.getStatus());
        data.put("action", report.getAction());
        data.put("createdAt", Times.at(report.getCreatedAt()));
        data.put("handledAt", Times.at(report.getHandledAt()));
        return data;
    }

    private Map<String, Object> adminView(ReportRecord report, boolean withSnapshot) {
        Map<String, Object> data = mineView(report);
        data.put("reporterId", report.getReporterId());
        List<String> evidenceIds = reportMapper.listFiles(report.getId());
        data.put("evidenceFileIds", evidenceIds);
        if (withSnapshot) {
            List<Map<String, Object>> evidence = new ArrayList<>();
            for (String fileId : evidenceIds) {
                if (fileId != null && !fileId.isBlank()) {
                    evidence.add(fileUrls.privateImage(fileId));
                }
            }
            data.put("evidence", evidence);
            data.put("messageSnapshot", report.getMessageSnapshot());
        }
        return data;
    }

    private static String blank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
