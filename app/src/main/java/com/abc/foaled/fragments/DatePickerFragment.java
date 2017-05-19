package com.abc.foaled.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 *
 * Made by Brendan :)
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView textBox;
    boolean time = false;

    /**
     * This is used to provide the destination of the value chosen
     * @param view The Text View the chosen date is to be put into
     */
    public void setViewResult (TextView view) {
        textBox = view;
    }

	/**
	 * Sets whether this fragment should display the time fragment afterwards, or not. Default is false
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


        int year, month, day;
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        if (textBox != null) {
            String date = textBox.getText().toString();

            if (date.equals("")) {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date d;
                try {
                    d = format.parse(date);
                    c.setTime(d);
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
/*                    String[] dateArray = date.split("/");

                    year = Integer.parseInt(dateArray[2]);
                    month = Integer.parseInt(dateArray[1]) - 1;
                    day = Integer.parseInt(dateArray[0]);*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //sets the datepicker dialog to have a max of today, and min of 50 years ago
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            c.add(Calendar.YEAR, -50);
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
            return dialog;
        }
        else {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
    }

    /**
     * Sets the Date of Birth TextView to the value specified in the
     * DateTimePicker Dialog FragmentExtender. This is the onTimeSet() callback
     * method, and gets called when the user chooses the date
     *
     * @param view The DatePicker view that was being interacted with
     * @param year The year selected
     * @param month The month selected
     * @param day The day selected
     */
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
            textBox.setText(day+"/"+(month+1)+"/"+year);
    }
}