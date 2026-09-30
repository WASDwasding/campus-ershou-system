package com.campus.ershou.common.meta;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetaService {

    public Map<String, Object> meta() {
        Map<String, Object> enums = new LinkedHashMap<>();
        enums.put("goodsCondition", List.of(
                item("NEW", "全新"),
                item("LIKE_NEW", "几乎全新"),
                item("GOOD", "轻微使用痕迹"),
                item("FAIR", "明显使用痕迹"),
                item("POOR", "有瑕疵但可用")
        ));
        enums.put("goodsStatus", List.of(
                item("ON_SALE", "在售"),
                item("RESERVED", "已预约"),
                item("SOLD", "已售出"),
                item("OFF_SHELF", "已下架"),
                item("REMOVED", "管理员下架"),
                item("DELETED", "已删除")
        ));
        enums.put("orderStatus", List.of(
                item("PENDING_PAY", "待支付"),
                item("PAID", "待面交"),
                item("COMPLETED", "已完成"),
                item("CANCELLED", "已取消"),
                item("DISPUTED", "申诉中")
        ));
        enums.put("paymentStatus", List.of(
                item("CREATED", "待付款"),
                item("SUCCESS", "已付款"),
                item("FAILED", "付款失败"),
                item("CLOSED", "已关闭"),
                item("REFUNDED", "已退款")
        ));
        enums.put("userStatus", List.of(
                item("ACTIVE", "正常"),
                item("BANNED", "已封禁")
        ));
        enums.put("verifyStatus", List.of(
                item("UNVERIFIED", "未认证"),
                item("PENDING", "审核中"),
                item("VERIFIED", "已认证"),
                item("REJECTED", "已驳回")
        ));
        enums.put("buildingType", List.of(
                item("DORM", "宿舍"),
                item("TEACHING", "教学楼"),
                item("CANTEEN", "食堂"),
                item("LIBRARY", "图书馆"),
                item("GATE", "校门"),
                item("OTHER", "其他")
        ));

        Map<String, Object> limits = new LinkedHashMap<>();
        limits.put("payTimeoutMinutes", 15);
        limits.put("maxPendingPayOrders", 3);
        limits.put("priceMin", 1);
        limits.put("priceMax", 9999900);
        limits.put("titleMin", 2);
        limits.put("titleMax", 30);
        limits.put("descriptionMax", 500);
        limits.put("tradeNoteMax", 100);
        limits.put("goodsImageMaxCount", 9);
        limits.put("goodsImageMaxBytes", 5_242_880);
        limits.put("nicknameMax", 16);
        limits.put("bioMax", 60);
        limits.put("messageMax", 500);
        limits.put("meetupPlaceMax", 40);
        limits.put("meetupAheadDays", 14);
        limits.put("reviewWindowDays", 30);
        limits.put("reviewContentMax", 200);
        limits.put("passwordMin", 8);
        limits.put("passwordMax", 64);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("enums", enums);
        data.put("limits", limits);
        return data;
    }

    private static Map<String, String> item(String value, String label) {
        return Map.of("value", value, "label", label);
    }
}
