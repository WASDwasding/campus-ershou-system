package com.campus.ershou.common.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Paging {

    private Paging() {
    }

    public static int[] page(Integer page, Integer size) {
        int p = page == null ? 1 : page;
        int s = size == null ? 20 : size;
        if (p < 1) {
            throw ApiException.validation(List.of(new FieldErrorItem("page", "页码从 1 开始")));
        }
        if (s < 1 || s > 50) {
            throw ApiException.validation(List.of(new FieldErrorItem("size", "每页 1–50 条")));
        }
        return new int[]{p, s};
    }

    public static Map<String, Object> of(List<?> list, int page, int size, long total) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", list);
        data.put("page", page);
        data.put("size", size);
        data.put("total", total);
        data.put("hasMore", (long) (page - 1) * size + list.size() < total);
        return data;
    }
}
