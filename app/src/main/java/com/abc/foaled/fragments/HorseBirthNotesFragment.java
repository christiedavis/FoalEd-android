package com.abc.foaled.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.NoteActivity;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HorseBirthNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HorseBirthNotesFragment extends Fragment {

	private Horse horse;


	public HorseBirthNotesFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param horse The horse we're getting the notes for
	 * @return A new instance of fragment HorseBirthNotesFragment.
	 */
	public static HorseBirthNotesFragment newInstance(Horse horse) {
		HorseBirthNotesFragment fragment = new HorseBirthNotesFragment();
		fragment.horse = horse;
		Bundle args = new Bundle();
		fragment.setArguments(args);
		fragment.setRetainInstance(true);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		LinearLayout baseLayout = (LinearLayout) inflater.inflate(R.layout.note_layout, container, false);
		((TextView) baseLayout.findViewById(R.id.card_header)).setText("Previous Pregnancies");



		if (!horse.getBirths().isEmpty()) {
			for (final Birth b : horse.getBirths()) {

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

			}
		} else {
			((TextView) baseLayout.findViewById(R.id.card_header)).setText("No Previous Pregnancies");
		}


		return baseLayout;
	}

}
