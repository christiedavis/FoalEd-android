package com.abc.foaled.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;

import org.joda.time.DateTime;

import static com.abc.foaled.helpers.DateTimeHelper.DATE_FORMATTER;


/**
 * A simple {@link Fragment} subclass.
 *
 * Made by Brendan :)
 */
public class DatePickerFragment extends DialogFragment {
	boolean time = false;

	private String date;
	private DatePickerDialog.OnDateSetListener listener;
	private DateTime minimumDate;

	public static DatePickerFragment newInstance(String date, DatePickerDialog.OnDateSetListener listener) {
		DatePickerFragment dialog = new DatePickerFragment();
		dialog.date = date;
		dialog.listener = listener;
		dialog.setRetainInstance(true);
		return dialog;
	}

	public static DatePickerFragment newInstance(String date, DatePickerDialog.OnDateSetListener listener, DateTime min) {
		DatePickerFragment dialog = new DatePickerFragment();
		dialog.date = date;
		dialog.listener = listener;
		dialog.minimumDate = min;
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

			DateTime parsedDate = DATE_FORMATTER.parseDateTime(date);
			year = parsedDate.getYear();
			month = parsedDate.getMonthOfYear()-1;
			day = parsedDate.getDayOfMonth();

		}

		//sets the datepicker dialog to have a max of today, and min of 50 years ago
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
		dialog.getDatePicker().setMaxDate(current.getMillis());
		if (minimumDate != null)
			dialog.getDatePicker().setMinDate(minimumDate.getMillis());
		else
			dialog.getDatePicker().setMinDate(current.minusYears(50).getMillis());
		return dialog;
	}


}