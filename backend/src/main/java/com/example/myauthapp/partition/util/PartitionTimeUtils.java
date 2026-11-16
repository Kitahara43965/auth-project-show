package com.example.myauthapp.partition.util;

import java.util.Objects;
import java.time.LocalDateTime;

public final class PartitionTimeUtils {

    private PartitionTimeUtils() {
    }

   public static LocalDateTime getMonthStart(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime must not be null");

        return localDateTime
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay();
    }

    public static LocalDateTime getNextMonthStart(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime must not be null");

        return localDateTime
                .plusMonths(1)
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay();
    }

}