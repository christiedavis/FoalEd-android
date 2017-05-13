package com.abc.foaled.Fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.abc.foaled.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.SimpleTimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    TextView textBox;
    int day, month, year;

    /**
     * This is used to provide the destination of the value chosen
     * @param view The Text View the chosen date is to be put into
     */
    public void setViewResult (TextView view) {
        textBox = view;
    }

    /**
     *
     * @param savedInstanceState The bundle of data shared by the creating activity
     * @return The TimePickerDialog to show the user
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //if this dialog is a result from choose the date
        if (getArguments() != null
                && getArguments().containsKey("textBox")) {

            day = getArguments().getInt("day");
            month = getArguments().getInt("month");
            year = getArguments().getInt("year");

            textBox = (TextView) getActivity().findViewById(getArguments().getInt("textBox"));
        }
        int minute, hour;
        Calendar cal = Calendar.getInstance();
        minute = cal.get(Calendar.MINUTE);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    /**
     * Sets the Date of Birth TextView to the value specified in the
     * DateTimePicker Dialog FragmentExtender. This is the onTimeSet() callback
     * method, and gets called when the user chooses the date
     *
     * @param view The TimePicker view that was being interacted with
     * @param hour The hour selected
     * @param minute The minute selected
     */
    public void onTimeSet(TimePicker view, int hour, int minute) {
        String _minute = (minute < 10) ? "0"+minute : ""+minute;
        String _hour = (hour < 10) ? "0"+hour : ""+hour;
        textBox.setText(day+"/"+(month+1)+"/"+year+" - "+_hour+":"+_minute);
    }
}