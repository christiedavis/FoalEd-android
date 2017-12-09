package com.abc.foaled.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.NoteActivity;
import com.abc.foaled.helpers.DateTimeHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;

public class HorsePregnancyFragment extends Fragment {

	public static final String FRAGMENT_TAG = "CURRENT_PREGNANCY_FRAGMENT";

	private static final int GENERAL_NOTE_EDIT = 1;

	private Horse horse;
	public HorsePregnancyFragment() {
		// Required empty public constructor
	}

	public static HorsePregnancyFragment newInstance(Horse h) {
		HorsePregnancyFragment fragment = new HorsePregnancyFragment();
		fragment.setRetainInstance(true);
		fragment.horse = h;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_horse_pregnancy, container, false);

		if (view instanceof LinearLayout) {
			TextView conceptionDate = (TextView) view.findViewById(R.id.notificationCheckbox1);
			conceptionDate.setText(this.horse.getCurrentBirth().getConception().toString(DateTimeHelper.DATE_FORMATTER));

			TextView birthDate = (TextView) view.findViewById(R.id.notificationCheckbox2);
			birthDate.setText(this.horse.getCurrentBirth().getConception().plusDays(340).toString(DateTimeHelper.DATE_FORMATTER));

			TextView siresName = (TextView) view.findViewById(R.id.notificationCheckbox3);
			siresName.setText(this.horse.getCurrentBirth().getSire());

			TextView notes = (TextView) view.findViewById(R.id.notificationCheckbox4);
			boolean noNotes = horse.getCurrentBirth().getNotes().isEmpty();
			String note = noNotes ? "No notes yet" : horse.getCurrentBirth().getNotes();
			notes.setText(note);

			Button button = (Button) view.findViewById(R.id.viewNotes);
			button.setText(noNotes ? "Add a note" : "View notes");


			view.findViewById(R.id.viewNotes).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), NoteActivity.class);
					intent.putExtra(Birth.BIRTH_ID, horse.getCurrentBirth().getId());
					startActivityForResult(intent, GENERAL_NOTE_EDIT);
				}
			});
		}
		return view;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GENERAL_NOTE_EDIT && resultCode == Activity.RESULT_OK)
			getActivity().recreate();
	}

}
