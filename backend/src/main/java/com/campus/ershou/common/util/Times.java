package com.campus.ershou.common.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class Times {

    public static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");
    public static final ZoneOffset OFFSET = ZoneOffset.ofHours(8);

    private Times() {
    }

    public static OffsetDateTime at(LocalDateTime time) {
        return time == null ? null : time.atOffset(OFFSET);
    }

    public static LocalDateTime toShanghai(OffsetDateTime time) {
        return time == null ? null : time.atZoneSameInstant(SHANGHAI).toLocalDateTime();
    }
}
