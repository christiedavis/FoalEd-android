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

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;

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

		expListView = (ExpandableListView) view.findViewById(R.id.lvExp);


		List<Birth> births = new ArrayList<>();

		try {
			births = helper.getBirthsDataDao().queryBuilder().where().eq("mare_id", horse.getHorseID()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		List<String> years = new ArrayList<>();
		Map<String, String> map = new LinkedHashMap<>();

		if (!births.isEmpty()) {

			for (Birth b : births) {

				if (horse != b.getMare()) {
					years.add(b.getYearOfBirth());
					map.put(b.getYearOfBirth(), b.getNotes());
				}
			}


			listAdapter = new HorseNoteAdaptor(getContext(), years, map);
			expListView.setAdapter(listAdapter);
/*			for (final Birth b : horse.getBirths()) {

				CardView cv = (CardView) inflater.inflate(R.layout.note, baseLayout, false);
				cv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), NoteActivity.class);
						intent.putExtra("birthID", b.getId());
						startActivity(intent);
					}
				});
				((TextView) cv.findViewById(R.id.horse_note_card_view_note)).setText(b.getNotes());
				baseLayout.addView(cv);

			}*/
		} else {
			((TextView) view.findViewById(R.id.prevPregnanciesTitle)).setText("No previous pregnancies");
		}
		return view;
	}

}
