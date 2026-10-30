package com.atlasrh.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class DateUtils {

    private DateUtils() {}

    public static int calculateBusinessDays(LocalDate startDate, LocalDate endDate) {
        int businessDays = 0;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                businessDays++;
            }
            current = current.plusDays(1);
        }
        return businessDays;
    }
}