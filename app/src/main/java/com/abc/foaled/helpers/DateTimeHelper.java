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

    public static String getCurrentAge(DateTime birthDateTime) {

        DateTime now = DateTime.now();

        if (birthDateTime.isAfterNow()) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
//        int year1 = now.get(Calendar.YEAR); //current year
//        int year2 = birthDate.get(Calendar.YEAR); //birth year
//        int age = year1 - year2; //age in years
//
//	    int month

	    DateTime diff = new DateTime(now.compareTo(birthDateTime));

	    Years years = Years.yearsBetween(birthDateTime, now);
	    if (years.getYears() > 0) {
		    if (years.getYears() > 1)
		    	return years.getYears() + " years old";
		    return "1 year old";
	    }

	    Months months = Months.monthsBetween(birthDateTime, now);
	    if (months.getMonths() > 0) {
		    if (months.getMonths() > 1)
		    	return months.getMonths() + " months old";
		    return "1 month old";
	    }

	    Weeks weeks = Weeks.weeksBetween(birthDateTime, now);
	    if (weeks.getWeeks() > 0) {
		    if (weeks.getWeeks() > 1)
		    	return weeks.getWeeks() + " weeks old";
		    return "1 week old";
	    }

	    Days days = Days.daysBetween(birthDateTime, now);
	    if (days.getDays() > 0) {
		    if (days.getDays() > 1)
		    	return days.getDays() + " days old";
		    return "1 day old";
	    }

	    Hours hours = Hours.hoursBetween(birthDateTime, now);
	    if (hours.getHours() > 0) {
		    if (hours.getHours() > 1)
			    return hours.getHours() + " hours old";
		    return "1 hour old";
	    }

	    Minutes minutes = Minutes.minutesBetween(birthDateTime, now);
	    if (minutes.getMinutes() > 0) {
		    if (minutes.getMinutes() > 1)
			    return minutes.getMinutes() + " minutes old";
		    return "1 minute old";
	    }

	    return "Less than a minute old";


	    //---------TESTING OUT THE ABOVE CODE-------------//
	    //TODO if the above works, remove the below code

//	    int seconds2 = birth

/*        int month1 = now.get(Calendar.MONTH); //current month
        int month2 = birthDate.get(Calendar.MONTH); //birth month
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = birthDate.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }*/




//        return age;
    }

    public static String getAgeString(int age) {
        if (age == 0) {
            return "Foal";
        }

        //TODO: other states
        return Integer.toString(age);
    }

    public static int getYear(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }
}