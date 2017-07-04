package com.abc.foaled.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abc.foaled.R;
import com.abc.foaled.models.Horse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HorseDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HorseDetailsFragment extends Fragment {

	public static final String FRAGMENT_TAG = "horseDetailsFragment";

	private Horse horse;
	public HorseDetailsFragment() {
		// Required empty public constructor
	}

	public static HorseDetailsFragment newInstance(Horse h) {
		HorseDetailsFragment fragment = new HorseDetailsFragment();
		fragment.horse = h;
		fragment.setRetainInstance(true);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_horse_details, container, false);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();

		if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT) {
			HorsePregnancyFragment fragment = HorsePregnancyFragment.newInstance(horse);
			transaction.replace(R.id.pregnancyCard, fragment, HorsePregnancyFragment.FRAGMENT_TAG);
		}



		transaction.commit();
		return view;
	}

}
