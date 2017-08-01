package com.abc.foaled.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.abc.foaled.R;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.models.Horse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HorseDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HorseDetailsFragment extends Fragment {

	public static final String FRAGMENT_TAG = "horseDetailsFragment";
	private DatabaseHelper helper;

	private Horse horse;
	public HorseDetailsFragment() {
		// Required empty public constructor
	}

	public static HorseDetailsFragment newInstance(Horse h, DatabaseHelper helper) {
		HorseDetailsFragment fragment = new HorseDetailsFragment();
		fragment.horse = h;
		fragment.helper = helper;
		fragment.setRetainInstance(true);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_horse_details, container, false);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();

		if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT) {

			//shuffles the general notes card to the bottom, and the current pregnancy card to the top
			FrameLayout pregnancyContainer = (FrameLayout) view.findViewById(R.id.pregnancyCard);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) pregnancyContainer.getLayoutParams();
			params.removeRule(RelativeLayout.BELOW);
			pregnancyContainer.setLayoutParams(params);

			FrameLayout generalNotesContainer = (FrameLayout) view.findViewById(R.id.generalNotesCard);
			params = (RelativeLayout.LayoutParams) generalNotesContainer.getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.previousPregnanciesCard);
			generalNotesContainer.setLayoutParams(params);

			//Adds the pregnancy card to the horse
			HorsePregnancyFragment fragment = HorsePregnancyFragment.newInstance(horse);
			transaction.replace(R.id.pregnancyCard, fragment, HorsePregnancyFragment.FRAGMENT_TAG);
		}

		if (horse.isFemale()) {
			HorseBirthNotesFragment fragment = HorseBirthNotesFragment.newInstance(horse, helper);
			transaction.replace(R.id.previousPregnanciesCard, fragment, HorseBirthNotesFragment.TAG);
		}

		HorseNoteFragment fragment = HorseNoteFragment.newInstance(horse);
		transaction.replace(R.id.generalNotesCard, fragment, HorseNoteFragment.TAG);

		transaction.commit();
		return view;
	}
}
