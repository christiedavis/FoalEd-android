package com.abc.foaled.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.foaled.activities.NoteActivity;
import com.abc.foaled.models.Horse;
import com.abc.foaled.R;

/**
 * A fragment representing a list of Items.
 */
public class HorseNoteFragment extends Fragment {

	private Horse horse;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HorseNoteFragment() {
    }

    @SuppressWarnings("unused")
    public static HorseNoteFragment newInstance(Horse h) {
        HorseNoteFragment fragment = new HorseNoteFragment();
		fragment.horse = h;
        fragment.setArguments(new Bundle());
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

		LinearLayout baseLayout = (LinearLayout) inflater.inflate(R.layout.note_layout, container, false);
		((TextView) baseLayout.findViewById(R.id.card_header)).setText("Notes");
		CardView cv = (CardView) inflater.inflate(R.layout.note, baseLayout, false);
		cv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), NoteActivity.class);
				intent.putExtra("horseID", horse.getHorseID());
				startActivity(intent);
			}
		});

		String note = horse.getNotes().isEmpty() ? "Click here to add notes" : horse.getNotes();
		((TextView) cv.findViewById(R.id.horse_note_card_view_note)).setText(note);
		baseLayout.addView(cv);
        return baseLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}