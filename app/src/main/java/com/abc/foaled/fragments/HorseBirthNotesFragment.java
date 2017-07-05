package com.abc.foaled.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.NoteActivity;
import com.abc.foaled.adaptors.HorseNoteAdaptor;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HorseBirthNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HorseBirthNotesFragment extends Fragment {

	public static final String TAG = "horse-birth-notes-fragment-tag";

	private Horse horse;
	private DatabaseHelper helper;

	public static HorseBirthNotesFragment newInstance(Horse horse, DatabaseHelper helper) {
		HorseBirthNotesFragment fragment = new HorseBirthNotesFragment();
		fragment.horse = horse;
		fragment.helper = helper;
		fragment.setRetainInstance(true);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_horse_birth_notes, container, false);

		CardView addTo = (CardView) view.findViewById(R.id.cv);

		List<Birth> births = new ArrayList<>();

		try {
			births = helper.getBirthsDataDao().queryBuilder().where().eq("mare_id", horse.getHorseID()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}


		if (births.isEmpty() || (births.size() == 1 && births.get(0).getMare().getHorseID() == horse.getHorseID())) {
			((TextView) view.findViewById(R.id.prevPregnanciesTitle)).setText("No previous pregnancies");
			addTo.setVisibility(View.GONE);
		} else {
			for (Birth b : births) {
				if (b.getMare().getHorseID() != horse.getHorseID()) {
					View toAdd = inflater.inflate(R.layout.expandable_textview, addTo, false);
					((ExpandableTextView) toAdd.findViewById(R.id.expandable_text_view)).setText(b.getYearOfBirth() + "\n" + b.getNotes());
					addTo.addView(toAdd);
				}
			}
		}
		return view;
	}

}
