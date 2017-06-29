package com.abc.foaled.fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment {


	DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

	private String passedInTime;
	private TimePickerDialog.OnTimeSetListener listener;

	public static TimePickerFragment newInstance(String time, TimePickerDialog.OnTimeSetListener listener) {
		TimePickerFragment dialog = new TimePickerFragment();
		dialog.passedInTime = time;
		dialog.listener = listener;
		dialog.setRetainInstance(true);
		return dialog;
	}

	/**
	 * @param savedInstanceState The bundle of data shared by the creating activity
	 * @return The TimePickerDialog to show the user
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		DateTime current = DateTime.now();

		int minute = current.getMinuteOfHour();
		int hour = current.getHourOfDay();

		String _minute = (minute < 10) ? "0" + minute : "" + minute;
		String _hour = (hour < 10) ? "0" + hour : "" + hour;
		String parsedTime = _hour + ":" + _minute;

		if (!parsedTime.equals(passedInTime)) {

			DateTime parsedDate = timeFormatter.parseDateTime(passedInTime);
			minute = parsedDate.getMinuteOfHour();
			hour = parsedDate.getHourOfDay();

		}

		return new TimePickerDialog(getActivity(), listener, hour, minute, true);
	}
}