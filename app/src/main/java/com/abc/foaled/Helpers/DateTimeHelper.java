package com.abc.foaled.Helpers;

/**
 * Created by christie on 17/02/17.
 */

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DateTimeHelper {

    public static Period getCurrentAge(DateTime birthDate) {

        DateTime currentDate = new DateTime();
        Period difference = new Period(birthDate, currentDate);

        return difference;
    }

    public static String printPeriod(Period period) {

        PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
                .appendYears()
                .appendSuffix(" year", " years")
                .appendSeparator(" - ")
                .appendMonths()
                .appendSuffix(" month", " months")
                .appendSeparator(" - ")
                .appendWeeks()
                .appendSuffix(" week", " weeks")
                .appendSeparator(" - ")
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparator(" - ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .toFormatter();

        return daysHoursMinutes.print(period);

    }
}
