package com.abc.foaled.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.NoteActivity;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HorseBirthNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HorseBirthNotesFragment extends Fragment {

	public static final String TAG = "horse-birth-notes-fragment-tag";
	private static final int EDIT_BIRTH_NOTE = 1;
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

		LinearLayout addTo = (LinearLayout) view.findViewById(R.id.prevPregnancies); //linear layout within the cardview

		List<Birth> births = new ArrayList<>();

		try {
			births = helper.getBirthsDataDao().queryBuilder().where().eq("mare_id", horse.getHorseID()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}


		if (births.isEmpty()) {
			view.setVisibility(View.GONE);
		} else if (births.size() == 1 && births.get(0).getMare().getHorseID() == horse.getHorseID() && horse.getCurrentBirth() != null) {
			view.setVisibility(View.GONE);
		} else {
			for (final Birth b : births) {
				if (horse.getCurrentBirth() != null && horse.getCurrentBirth().getId() == b.getId())
					continue;

				View toAdd = inflater.inflate(R.layout.expandable_layout, addTo, false);
				((TextView) toAdd.findViewById(R.id.layout_header)).setText(b.getYearOfBirth());
				((TextView) toAdd.findViewById(R.id.note)).setText(b.getNotes());

				toAdd.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((ExpandableLayout) v.findViewById(R.id.expandable_layout)).toggle();
					}
				});

				toAdd.findViewById(R.id.editPregnancyNote).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), NoteActivity.class);
						intent.putExtra(Birth.BIRTH_ID, b.getId());
						startActivityForResult(intent, EDIT_BIRTH_NOTE);
					}
				});
				addTo.addView(toAdd);

			}
		}
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT_BIRTH_NOTE && resultCode == Activity.RESULT_OK)
			getActivity().recreate();
	}

}
