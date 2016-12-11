package utils;

import java.util.Calendar;
import java.util.Date;

public class TimeFormatter {
    private static final int MINUTE_SECONDS = 60;
    private static final int HOUR_SECONDS = 60 * MINUTE_SECONDS;
    private static final int DAY_SECONDS = (HOUR_SECONDS * 24);
    private static final int MONTH_SECONDS = (DAY_SECONDS * 30);
    private static final int YEAR_SECONDS = (MONTH_SECONDS * 12);

    public static String getTimeFormatted(Date time) {
        StringBuilder result = new StringBuilder();

        long seconds = (Calendar.getInstance().getTimeInMillis()/1000 - time.getTime()/1000);

        if (seconds < 60) {
            result.append("agora");
        } else {
            long years = seconds / YEAR_SECONDS;
            seconds -= years * YEAR_SECONDS;
            long months = seconds / MONTH_SECONDS;
            seconds -= months / MONTH_SECONDS;
            long days = seconds / DAY_SECONDS;
            seconds -= days * DAY_SECONDS;
            long hours = seconds / HOUR_SECONDS;
            seconds -= hours * HOUR_SECONDS;
            long minutes = seconds / MINUTE_SECONDS;

            if (years > 0) {
                result.append(years);
                result.append("a ");
            }
            if (months > 0) {
                result.append(months);
                result.append("m ");
            }
            if (days > 0) {
                result.append(days);
                result.append("d ");
            }
            if (hours > 0) {
                result.append(hours);
                result.append("h ");
            }
            if (minutes > 0) {
                result.append(minutes);
                result.append("min");
            }
        }

        return result.toString();
    }
}
