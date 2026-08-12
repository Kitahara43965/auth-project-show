package com.example.myauthapp.support.util;

import java.time.LocalDateTime;

public final class TestTimeUtils {

    private TestTimeUtils() {
    }

    public static LocalDateTime startOfMonth(LocalDateTime dateTime) {
        return dateTime
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay();
    }

    public static LocalDateTime startOfNextMonth(LocalDateTime dateTime) {
        return dateTime
                .plusMonths(1)
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay();
    }
}