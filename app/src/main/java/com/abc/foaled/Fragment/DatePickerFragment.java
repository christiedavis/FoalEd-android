package com.abc.foaled.Fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView textBox;

    /**
     * This is used to provide the destination of the value chosen
     * @param view The Text View the chosen date is to be put into
     */
    public void setViewResult (TextView view) {
        textBox = view;
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
        if (textBox != null) {
            String date = textBox.getText().toString();

            if (date.equals("")) {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                String[] dateArray = date.split("/");

                year = Integer.parseInt(dateArray[2]);
                month = Integer.parseInt(dateArray[1]) - 1;
                day = Integer.parseInt(dateArray[0]);
            }

            //Create and return a new instance of TimePickerDialog
            return new DatePickerDialog(getActivity(), this, year, month, day);
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
     * DateTimePicker Dialog Fragment. This is the onTimeSet() callback
     * method, and gets called when the user chooses the date
     *
     * @param view The DatePicker view that was being interacted with
     * @param year The year selected
     * @param month The month selected
     * @param day The day selected
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (textBox != null)
            textBox.setText(day+"/"+ (month+1)+"/"+year);
    }
}