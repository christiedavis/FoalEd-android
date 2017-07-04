package com.abc.foaled.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.helpers.DateTimeHelper;
import com.abc.foaled.models.Horse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HorsePregnancyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HorsePregnancyFragment extends Fragment {

	public static final String FRAGMENT_TAG = "CURRENT_PREGNANCY_FRAGMENT";
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
			TextView conceptionDate = (TextView) view.findViewById(R.id.conceptionDate);
			conceptionDate.setText(this.horse.getCurrentBirth().getConception().toString(DateTimeHelper.DATE_FORMATTER));

			TextView birthDate = (TextView) view.findViewById(R.id.birthDate);
			birthDate.setText(this.horse.getCurrentBirth().getConception().plusDays(340).toString(DateTimeHelper.DATE_FORMATTER));

			TextView siresName = (TextView) view.findViewById(R.id.horseName);
			siresName.setText(this.horse.getCurrentBirth().getSire());
		}
		// Inflate the layout for this fragment
		return view;

	}

}
