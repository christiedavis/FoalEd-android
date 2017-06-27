package com.abc.foaled.helpers;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.util.Date;
import java.util.Calendar;

public class DateTimeHelper {

	/**
	 * Calculates the age of a horse and returns the string representation of the age
	 *
	 * 1 year old
	 * 2 months old
	 * 3 days old
	 * less than a minute old
	 *
	 * @param birth Horses date of birth
	 * @return String representing the horses age
	 */
    public static String getCurrentAgeString(DateTime birth) {

        DateTime now = DateTime.now();

        if (birth.isAfterNow()) {
            throw new IllegalArgumentException("Can't be born in the future");
        }


	    Years years = Years.yearsBetween(birth, now);
	    if (years.getYears() > 0) {
		    if (years.getYears() > 1)
		    	return years.getYears() + " years old";
		    return "1 year old";
	    }

	    Months months = Months.monthsBetween(birth, now);
	    if (months.getMonths() > 0) {
		    if (months.getMonths() > 1)
		    	return months.getMonths() + " months old";
		    return "1 month old";
	    }

	    Weeks weeks = Weeks.weeksBetween(birth, now);
	    if (weeks.getWeeks() > 0) {
		    if (weeks.getWeeks() > 1)
		    	return weeks.getWeeks() + " weeks old";
		    return "1 week old";
	    }

	    Days days = Days.daysBetween(birth, now);
	    if (days.getDays() > 0) {
		    if (days.getDays() > 1)
		    	return days.getDays() + " days old";
		    return "1 day old";
	    }

	    Hours hours = Hours.hoursBetween(birth, now);
	    if (hours.getHours() > 0) {
		    if (hours.getHours() > 1)
			    return hours.getHours() + " hours old";
		    return "1 hour old";
	    }

	    Minutes minutes = Minutes.minutesBetween(birth, now);
	    if (minutes.getMinutes() > 0) {
		    if (minutes.getMinutes() > 1)
			    return minutes.getMinutes() + " minutes old";
		    return "1 minute old";
	    }
	    return "Less than a minute old";
    }

    public static int getAgeInYears(DateTime birth) {
		return Years.yearsBetween(birth, DateTime.now()).getYears();
	}

	public static int getAgeInMonths(DateTime birth) {
		return Months.monthsBetween(birth, DateTime.now()).getMonths();
	}

	public static int getAgeInDays(DateTime birth) {
		return Days.daysBetween(birth, DateTime.now()).getDays();
	}


    public static int getYear(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }
}