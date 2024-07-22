package io.github.secondbrainplanner;

import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {

    public static Calendar getGermanCalendar() {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar;
    }
}
