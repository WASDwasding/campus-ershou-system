package com.campus.ershou.report;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.goods.GoodsMapper;
import com.campus.ershou.goods.GoodsRow;
import com.campus.ershou.im.ConversationService;
import com.campus.ershou.im.MessageRecord;
import com.campus.ershou.im.ConversationMapper;
import com.campus.ershou.im.NotificationService;
import com.campus.ershou.order.OrderRecord;
import com.campus.ershou.order.OrderMapper;
import com.campus.ershou.user.AdminUserService;
import com.campus.ershou.user.UserMapper;
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
    private final GoodsMapper goodsMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ConversationMapper conversationMapper;
    private final ConversationService conversationService;
    private final FileService fileService;
    private final RateLimitService rateLimitService;
    private final NotificationService notificationService;
    private final AdminUserService adminUserService;
    private final com.campus.ershou.goods.GoodsAdminService goodsAdminService;
    private final AuditService auditService;

    public ReportService(
            ReportMapper reportMapper,
            GoodsMapper goodsMapper,
            UserMapper userMapper,
            OrderMapper orderMapper,
            ConversationMapper conversationMapper,
            ConversationService conversationService,
            FileService fileService,
            RateLimitService rateLimitService,
            NotificationService notificationService,
            AdminUserService adminUserService,
            com.campus.ershou.goods.GoodsAdminService goodsAdminService,
            AuditService auditService
    ) {
        this.reportMapper = reportMapper;
        this.goodsMapper = goodsMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.conversationMapper = conversationMapper;
        this.conversationService = conversationService;
        this.fileService = fileService;
        this.rateLimitService = rateLimitService;
        this.notificationService = notificationService;
        this.adminUserService = adminUserService;
        this.goodsAdminService = goodsAdminService;
        this.auditService = auditService;
    }

    @Transactional
    public Map<String, Object> create(long userId, String targetType, Long targetId, String reason, String description, List<String> evidence) {
        rateLimitService.check("report", String.valueOf(userId), 10, Duration.ofDays(1));
        String type = targetType == null ? "" : targetType.trim().toUpperCase();
        String cleanReason = reason == null ? "" : reason.trim().toUpperCase();
        if (!TARGETS.contains(type)) {
            throw ApiException.validation(List.of(new FieldErrorItem("targetType", "只能是 GOODS、USER 或 MESSAGE")));
        }
        if (targetId == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("targetId", "请选择举报对象")));
        }
        if (!REASONS.contains(cleanReason)) {
            throw ApiException.validation(List.of(new FieldErrorItem("reason", "举报原因不合法")));
        }
        String text = description == null ? "" : description.trim();
        if (text.codePointCount(0, text.length()) > 300) {
            throw ApiException.validation(List.of(new FieldErrorItem("description", "说明最多 300 字")));
        }
        List<String> files = evidence == null ? List.of() : evidence;
        if (files.size() > 6) {
            throw ApiException.validation(List.of(new FieldErrorItem("evidenceFileIds", "最多 6 张")));
        }
        for (String fileId : files) {
            fileService.requireUsable(fileId, userId, "EVIDENCE");
        }
        String snapshot = assertVisible(userId, type, targetId);
        if (reportMapper.countRecent(userId, type, targetId) > 0) {
            throw ApiException.of(ErrorCode.DUPLICATE_REPORT);
        }
        ReportRecord report = new ReportRecord();
        report.setReporterId(userId);
        report.setTargetType(type);
        report.setTargetId(targetId);
        report.setReason(cleanReason);
        report.setDescription(text.isEmpty() ? null : text);
        report.setStatus("PENDING");
        report.setMessageSnapshot(snapshot);
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
        if (normalizedStatus != null && !Set.of("PENDING", "HANDLED").contains(normalizedStatus.toUpperCase())) {
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

    public Map<String, Object> handle(long adminId, long id, String action, Integer banDays, String reason, String ip) {
        String normalized = action == null ? "" : action.trim().toUpperCase();
        if (!ACTIONS.contains(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("action", "处理动作不合法")));
        }
        ReportRecord report = reportMapper.findById(id);
        if (report == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (!"PENDING".equals(report.getStatus())) {
            throw ApiException.of(ErrorCode.REPORT_ALREADY_HANDLED);
        }
        if ("REMOVE_GOODS".equals(normalized)) {
            if (!"GOODS".equals(report.getTargetType())) {
                throw ApiException.validation(List.of(new FieldErrorItem("action", "只有商品举报可以下架")));
            }
            String text = reason == null || reason.isBlank() ? "举报成立" : reason.trim();
            goodsAdminService.remove(adminId, report.getTargetId(), text, ip);
        } else if ("BAN_USER".equals(normalized)) {
            long targetUser = reportedUser(report);
            String text = reason == null || reason.isBlank() ? "举报成立" : reason.trim();
            adminUserService.ban(adminId, targetUser, text, banDays, ip);
        } else if ("WARN".equals(normalized)) {
            long targetUser = reportedUser(report);
            notificationService.notify(targetUser, "REPORT_WARN", "请注意社区规范", "你的内容被举报，请遵守平台规则。", "REPORT", report.getId());
        }
        if (reportMapper.handle(id, normalized) != 1) {
            throw ApiException.of(ErrorCode.REPORT_ALREADY_HANDLED);
        }
        auditService.record("ADMIN", "ADMIN_REPORT_HANDLE", adminId, report.getReporterId(), ip,
                "{\"reportId\":" + id + ",\"action\":\"" + normalized + "\"}");
        return adminDetail(id);
    }

    public long countPending() {
        return reportMapper.countPending();
    }

    private String assertVisible(long userId, String type, long targetId) {
        return switch (type) {
            case "GOODS" -> {
                GoodsRow goods = goodsMapper.findById(targetId);
                if (goods == null || !Set.of("ON_SALE", "RESERVED", "SOLD").contains(goods.getStatus())) {
                    throw ApiException.of(ErrorCode.NOT_FOUND);
                }
                if (goods.getSellerId() != null && goods.getSellerId() == userId) {
                    throw ApiException.of(ErrorCode.FORBIDDEN);
                }
                yield null;
            }
            case "USER" -> {
                if (targetId == userId || userMapper.findById(targetId) == null) {
                    throw targetId == userId ? ApiException.of(ErrorCode.FORBIDDEN) : ApiException.of(ErrorCode.NOT_FOUND);
                }
                yield null;
            }
            default -> {
                MessageRecord message = conversationMapper.findMessage(targetId);
                if (message == null || message.getConversationId() == null) {
                    throw ApiException.of(ErrorCode.NOT_FOUND);
                }
                conversationService.requireMember(message.getConversationId(), userId);
                if (message.getSenderId() != null && message.getSenderId() == userId) {
                    throw ApiException.of(ErrorCode.FORBIDDEN);
                }
                yield message.getRawContent() == null ? message.getContent() : message.getRawContent();
            }
        };
    }

    private long reportedUser(ReportRecord report) {
        return switch (report.getTargetType()) {
            case "USER" -> report.getTargetId();
            case "GOODS" -> {
                GoodsRow goods = goodsMapper.findById(report.getTargetId());
                if (goods == null || goods.getSellerId() == null) {
                    throw ApiException.of(ErrorCode.NOT_FOUND);
                }
                yield goods.getSellerId();
            }
            case "MESSAGE" -> {
                MessageRecord message = conversationMapper.findMessage(report.getTargetId());
                if (message == null || message.getSenderId() == null) {
                    throw ApiException.of(ErrorCode.NOT_FOUND);
                }
                yield message.getSenderId();
            }
            default -> throw ApiException.of(ErrorCode.CONFLICT);
        };
    }

    private Map<String, Object> mineView(ReportRecord report) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", report.getId());
        data.put("targetType", report.getTargetType());
        data.put("targetId", report.getTargetId());
        data.put("reason", report.getReason());
        data.put("description", report.getDescription());
        data.put("status", report.getStatus());
        data.put("action", report.getAction());
        data.put("createdAt", Times.at(report.getCreatedAt()));
        data.put("handledAt", Times.at(report.getHandledAt()));
        return data;
    }

    private Map<String, Object> adminView(ReportRecord report, boolean withSnapshot) {
        Map<String, Object> data = mineView(report);
        data.put("reporterId", report.getReporterId());
        data.put("evidenceFileIds", reportMapper.listFiles(report.getId()));
        if (withSnapshot) {
            data.put("messageSnapshot", report.getMessageSnapshot());
        }
        return data;
    }

    private static String blank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
