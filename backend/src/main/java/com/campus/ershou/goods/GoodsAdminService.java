package com.campus.ershou.goods;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.order.OrderRecord;
import com.campus.ershou.order.OrderService;
import com.campus.ershou.order.OrderTx;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsAdminService {

    private static final Set<String> STATUSES = Set.of("ON_SALE", "RESERVED", "SOLD", "OFF_SHELF", "REMOVED", "DELETED");

    private final GoodsMapper goodsMapper;
    private final OrderService orderService;
    private final OrderTx orderTx;
    private final AuditService auditService;

    public GoodsAdminService(
            GoodsMapper goodsMapper,
            OrderService orderService,
            OrderTx orderTx,
            AuditService auditService
    ) {
        this.goodsMapper = goodsMapper;
        this.orderService = orderService;
        this.orderTx = orderTx;
        this.auditService = auditService;
    }

    public Map<String, Object> list(String status, String keyword, Long sellerId, Integer page, Integer size) {
        String normalized = null;
        if (status != null && !status.isBlank()) {
            normalized = status.trim().toUpperCase();
            if (!STATUSES.contains(normalized)) {
                throw ApiException.validation(List.of(new FieldErrorItem("status", "商品状态不合法")));
            }
        }
        String like = keyword == null || keyword.isBlank()
                ? null
                : "%" + keyword.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        int[] paging = Paging.page(page, size);
        List<Map<String, Object>> list = new ArrayList<>();
        for (GoodsRow goods : goodsMapper.adminList(normalized, like, sellerId, (paging[0] - 1) * paging[1], paging[1])) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", goods.getId());
            item.put("sellerId", goods.getSellerId());
            item.put("title", goods.getTitle());
            item.put("price", goods.getPrice());
            item.put("status", goods.getStatus());
            item.put("removedReason", goods.getRemovedReason());
            list.add(item);
        }
        return Paging.of(list, paging[0], paging[1], goodsMapper.adminCount(normalized, like, sellerId));
    }

    public Map<String, Object> remove(long adminId, long goodsId, String reason, String ip) {
        String text = reason == null ? "" : reason.trim();
        int length = text.codePointCount(0, text.length());
        if (length < 2 || length > 100) {
            throw ApiException.validation(List.of(new FieldErrorItem("reason", "原因 2–100 字")));
        }
        GoodsRow goods = goodsMapper.findById(goodsId);
        if (goods == null || "DELETED".equals(goods.getStatus())) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        OrderRecord disputed = orderService.findActive(goodsId);
        if (disputed != null && "DISPUTED".equals(disputed.getStatus())) {
            throw ApiException.of(ErrorCode.GOODS_IN_DISPUTE);
        }
        String from = goods.getStatus();
        if ("RESERVED".equals(from) && disputed != null) {
            if ("PENDING_PAY".equals(disputed.getStatus())) {
                orderTx.cancelUnpaid(disputed, "ADMIN", adminId, text);
                from = "ON_SALE";
            } else if ("PAID".equals(disputed.getStatus())) {
                orderTx.cancelPaid(disputed, "ADMIN", adminId, text);
                from = "OFF_SHELF";
            }
        }
        if (!"ON_SALE".equals(from) && !"OFF_SHELF".equals(from)) {
            throw ApiException.of(ErrorCode.CONFLICT);
        }
        if (goodsMapper.removeFrom(goodsId, from, text) != 1) {
            throw ApiException.of(ErrorCode.CONFLICT);
        }
        auditService.record("ADMIN", "ADMIN_GOODS_REMOVE", adminId, goods.getSellerId(), ip,
                "{\"goodsId\":" + goodsId + "}");
        return Map.of("id", goodsId, "status", "REMOVED");
    }

    public Map<String, Object> restore(long adminId, long goodsId, String ip) {
        GoodsRow goods = goodsMapper.findById(goodsId);
        if (goods == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (goodsMapper.restoreRemoved(goodsId) != 1) {
            throw ApiException.of(ErrorCode.CONFLICT);
        }
        auditService.record("ADMIN", "ADMIN_GOODS_RESTORE", adminId, goods.getSellerId(), ip, "{\"goodsId\":" + goodsId + "}");
        return Map.of("id", goodsId, "status", "OFF_SHELF");
    }
}
