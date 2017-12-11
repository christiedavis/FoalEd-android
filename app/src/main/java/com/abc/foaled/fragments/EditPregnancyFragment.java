package com.abc.foaled.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.helpers.DateTimeHelper;
import com.abc.foaled.models.Birth;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPregnancyFragment extends Fragment {

	public static final String TAG = "edit-pregnancy-fragment-tag";

	private Birth birth;
	private DatabaseHelper helper;

	public EditPregnancyFragment newInstance(Birth b, DatabaseHelper helper) {
		EditPregnancyFragment fragment = new EditPregnancyFragment();
		fragment.birth = b;
		fragment.helper = helper;
		fragment.setRetainInstance(true);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_edit_pregnancy, container, false);

		TextView conceptionDate = (TextView) view.findViewById(R.id.notificationCheckbox1);
		TextView siresName = (TextView) view.findViewById(R.id.siresName);

		conceptionDate.setText(birth.getConception().toString(DateTimeHelper.DATE_FORMATTER));
		siresName.setText(birth.getSire());

		//set the button on click listeners
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((HorseDetailActivity) getActivity()).cancel();
			}
		});
		view.findViewById(R.id.editPregnancy).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView conceptionDate = (TextView) v.getRootView().findViewById(R.id.notificationCheckbox1);
				TextView siresName = (TextView) v.getRootView().findViewById(R.id.siresName);

				birth.setConception(DateTimeHelper.DATE_FORMATTER.parseDateTime(conceptionDate.getText().toString()));
				birth.setSire(siresName.getText().toString());
			}
		});
		view.findViewById(R.id.deletePregnancy).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("Are you sure you want to delete this pregnancy?")
						.setNegativeButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								helper.getBirthsDataDao().delete(birth);
							}
						})
						.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).show();
			}
		});

		return view;
	}
}
