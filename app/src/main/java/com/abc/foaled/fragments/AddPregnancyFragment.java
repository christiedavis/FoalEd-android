package com.abc.foaled.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abc.foaled.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddPregnancyFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public AddPregnancyFragment() {
        // Required empty public constructor
    }

    public static AddPregnancyFragment newInstance() {
		AddPregnancyFragment fragment = new AddPregnancyFragment();
		fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_pregnancy, container, false);
        TextView dateText = (TextView) view.findViewById(R.id.conceptionDate);


        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime());
        dateText.setText(date);

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = new DatePickerFragment();

                dialog.displayTimeDialog(false);
                dialog.setRetainInstance(true);
                dialog.show(getActivity().getFragmentManager(), "datePicker");
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAddPregnancyFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAddPregnancyFragmentInteraction(Uri uri);
    }
}
