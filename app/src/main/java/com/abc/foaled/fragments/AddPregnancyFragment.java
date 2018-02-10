package com.abc.foaled.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abc.foaled.R;

import org.joda.time.DateTime;

import static com.abc.foaled.helpers.DateTimeHelper.DATE_FORMATTER;

public class AddPregnancyFragment extends Fragment {



    public static AddPregnancyFragment newInstance() {
		AddPregnancyFragment fragment = new AddPregnancyFragment();
		fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_pregnancy, container, false);
        TextView dateText = view.findViewById(R.id.notificationCheckbox1);


        String date = DateTime.now().toString(DATE_FORMATTER);
        dateText.setText(date);

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = new DatePickerFragment();

                Log.d("datepicker", "got here");
                dialog.displayTimeDialog(false);
                dialog.setRetainInstance(true);
                dialog.show(getActivity().getFragmentManager(), "datePicker");
            }
        });

        return view;
    }
}
