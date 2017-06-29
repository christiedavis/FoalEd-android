package com.abc.foaled.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import com.abc.foaled.activities.AddNewHorseActivity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 *
 * Made by Brendan :)
 */
public class DatePickerFragment extends DialogFragment {
	boolean time = false;

	DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

	private String date;
	private DatePickerDialog.OnDateSetListener listener;

	public static DatePickerFragment newInstance(String date, DatePickerDialog.OnDateSetListener listener) {
		DatePickerFragment dialog = new DatePickerFragment();
		dialog.date = date;
		dialog.listener = listener;
		dialog.setRetainInstance(true);
		return dialog;
	}

	/**
	 * Sets whether this fragment should display the time fragment afterwards, or not. Default is false
	 *
	 * @param time True to display time dialog as well, false is default
	 */
	public void displayTimeDialog(boolean time) {
		this.time = time;
	}

	/**
	 * If there is already a date set in the Text View, parse the string
	 * and pass the relevant values in the DatePickerDialog creation. Or
	 * else just use today's date values
	 *
	 * @param savedInstanceState The bundle of data shared by the creating activity
	 * @return The DatePickerDialog to show the user
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		DateTime current = DateTime.now();

		//current date details
		int year = current.getYear();
		int month = current.getMonthOfYear()-1;
		int day = current.getDayOfMonth();

		String stringDate = day + "/" + (month + 1) + "/" + year;

		//if the passed in date is not
		if (!stringDate.equals(date)) {

			DateTime parsedDate = dateFormatter.parseDateTime(date);
			year = parsedDate.getYear();
			month = parsedDate.getMonthOfYear()-1;
			day = parsedDate.getDayOfMonth();

		}

		//sets the datepicker dialog to have a max of today, and min of 50 years ago
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
		dialog.getDatePicker().setMaxDate(current.getMillis());
		dialog.getDatePicker().setMinDate(current.minusYears(50).getMillis());
		return dialog;
	}

/*	*//**
	 * Sets the Date of Birth TextView to the value specified in the
	 * DateTimePicker Dialog FragmentExtender. This is the onTimeSet() callback
	 * method, and gets called when the user chooses the date
	 *
	 * @param view  The DatePicker view that was being interacted with
	 * @param year  The year selected
	 * @param month The month selected
	 * @param day   The day selected
	 *//*
	public void onDateSet(DatePicker view, int year, int month, int day) {
		if (time) {
			DialogFragment dialog = new TimePickerFragment();
			Bundle b = new Bundle();
			b.putInt("year", year);
			b.putInt("month", month);
			b.putInt("day", day);
			b.putInt("textBox", textBox.getId());
			dialog.setArguments(b);

			dialog.setRetainInstance(true);
			dialog.show(getFragmentManager(), "timePicker");
		} else
			textBox.setText(day + "/" + (month + 1) + "/" + year);
	}*/


}