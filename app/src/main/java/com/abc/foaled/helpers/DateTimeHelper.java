package com.abc.foaled.helpers;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Calendar;

public class DateTimeHelper {

    public static int getCurrentAge(DateTime birthDateTime) {

        Calendar now = Calendar.getInstance();

        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(birthDateTime.toDate());

        if (birthDate.after(now)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
        int year1 = now.get(Calendar.YEAR);
        int year2 = birthDate.get(Calendar.YEAR);
        int age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = birthDate.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = birthDate.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }

        return age;
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