package com.abc.foaled.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.abc.foaled.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class NumberPickerFragment extends DialogFragment {


    NumberPicker picker;
    private String age;
    private NumberPicker.OnValueChangeListener listener;

    public static NumberPickerFragment newInstance(String age, NumberPicker.OnValueChangeListener listener) {
        NumberPickerFragment dialog = new NumberPickerFragment();
        dialog.age = age;
        dialog.listener = listener;
        dialog.setRetainInstance(true);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //TODO: this doesn't work :(
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_number_picker, null);
        builder.setView(v);

        picker = (NumberPicker) v.findViewById(R.id.numberPicker);
        picker.setMaxValue(0);
        picker.setMinValue(40);
        return builder.create();
    }
}