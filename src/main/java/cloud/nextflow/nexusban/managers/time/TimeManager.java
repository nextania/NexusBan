package cloud.nextflow.nexusban.managers.time;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

public class TimeManager extends NexusManager {
    public TimeManager(NexusBan nexusBan) {
        super(nexusBan, "Time Manager");
    }

    @Override
    public void register() throws ManagerException {

    }

    // to unban someone who is offline
    // if they aren't found in the list,

    public static String convertToDateTime(Instant instant) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());

        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(zonedDateTime);
    }

    private static String remainingTimeHelper(long value, String namePlural, String nameSingular, long nextValue) {
        if (value == 0) return "";
        boolean singular = value == 1;
        boolean nextTime = nextValue != 0;
        return value + " " + (singular ? nameSingular : namePlural) + (nextTime ? ", " : "");
    }

    public static String timeRemaining(Instant startDate, Instant endDate) {
        ZonedDateTime startDateZoned = startDate.atZone(ZoneId.systemDefault());
        ZonedDateTime endDateZoned = endDate.atZone(ZoneId.systemDefault());
        long centuries = ChronoUnit.CENTURIES.between(startDateZoned, endDateZoned);
        long decades = ChronoUnit.DECADES.between(startDateZoned, endDateZoned);
        long years = ChronoUnit.YEARS.between(startDateZoned, endDateZoned);
        long months = ChronoUnit.MONTHS.between(startDateZoned, endDateZoned);
        long weeks = ChronoUnit.WEEKS.between(startDateZoned, endDateZoned);
        long days = ChronoUnit.DAYS.between(startDateZoned, endDateZoned);
        long hours = ChronoUnit.HOURS.between(startDateZoned, endDateZoned);
        long minutes = ChronoUnit.MINUTES.between(startDateZoned, endDateZoned);
        long seconds = ChronoUnit.SECONDS.between(startDateZoned, endDateZoned);
        return remainingTimeHelper(centuries, "centuries", "century", decades) +
                remainingTimeHelper(decades, "decades", "decade", years) +
                remainingTimeHelper(years, "years", "year", months) +
                remainingTimeHelper(months, "months", "month", weeks) +
                remainingTimeHelper(weeks, "weeks", "week", days) +
                remainingTimeHelper(days, "days", "day", hours) +
                remainingTimeHelper(hours, "hours", "hour", minutes) +
                remainingTimeHelper(minutes, "minutes", "minute", seconds) +
                remainingTimeHelper(seconds, "seconds", "second", 0);
    }
}
