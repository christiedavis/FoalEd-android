package com.abc.foaled.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.helpers.DateTimeHelper;
import com.abc.foaled.models.Birth;

public class EmptyHorseFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Boolean favourite = false;
    private DatabaseHelper helper;

    public EmptyHorseFragment() {
        // Required empty public constructor
    }

    public static EmptyHorseFragment newInstance(DatabaseHelper helper) {
        EmptyHorseFragment fragment = new EmptyHorseFragment();
        fragment.helper = helper;
        fragment.setRetainInstance(true);
        return fragment;
    }
    public void setAsFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_empty_horse, container, false);

        TextView textbox = (TextView) view.findViewById(R.id.empty_horse_text_view);
        if (favourite) {
            textbox.setText("You have no horses favourited - click the star in the horse view to add to favourites");
        } else {
            textbox.setText("You have no horses /n /nClick the purple button to add a horse");
        }
        return view;
    }

     public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}