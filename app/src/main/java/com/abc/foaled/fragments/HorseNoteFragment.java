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

import com.abc.foaled.activities.NoteActivity;
import com.abc.foaled.models.Horse;
import com.abc.foaled.R;

/**
 * A fragment representing a list of Items.
 */
public class HorseNoteFragment extends Fragment {

	public static final String TAG = "horse-general-note-fragment";

	private static final int GENERAL_NOTE_EDIT = 1;
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

		LinearLayout baseLayout = (LinearLayout) inflater.inflate(R.layout.fragment_horse_note, container, false);

	    baseLayout.findViewById(R.id.cv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), NoteActivity.class);
				intent.putExtra(Horse.HORSE_ID, horse.getHorseID());
				startActivityForResult(intent, GENERAL_NOTE_EDIT);
			}
		});

		TextView noteView = (TextView) baseLayout.findViewById(R.id.horseNotes);
		String note = horse.getNotes().isEmpty() ? "Click here to add notes" : horse.getNotes();
		noteView.setText(note);
        return baseLayout;
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == GENERAL_NOTE_EDIT && resultCode == Activity.RESULT_OK)
	        getActivity().recreate();
	}
}