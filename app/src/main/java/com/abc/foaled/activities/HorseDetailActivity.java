package com.abc.foaled.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.database.ORMBaseActivity;
import com.abc.foaled.fragments.DatePickerFragment;
import com.abc.foaled.fragments.HorseDetailsFragment;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;
import com.abc.foaled.R;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.j256.ormlite.misc.TransactionManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.SQLException;
import java.util.concurrent.Callable;

public class HorseDetailActivity extends ORMBaseActivity<DatabaseHelper>
	implements DatePickerDialog.OnDateSetListener {

	Horse horse;
	int horseID;

	private PopupWindow currPopupWindow;
	private String sire;
	private String currDate = "";
	private CoordinatorLayout layout;
	DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
	DateTimeFormatter dateAndTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy - HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		if (savedInstanceState != null && savedInstanceState.containsKey("horseID"))
			horseID = savedInstanceState.getInt(Horse.HORSE_ID, 0);
		else
			horseID = getIntent().getIntExtra(Horse.HORSE_ID, 0);

		horse = getHelper().getHorseDataDao().queryForId(horseID);

		if (savedInstanceState != null && savedInstanceState.containsKey("sire")) {
			sire = savedInstanceState.getString("sire");
		}

		if (savedInstanceState != null && savedInstanceState.containsKey("date"))
			currDate = savedInstanceState.getString("date");

		setup();
	}

	/**
	 * Inflates the custom menu items in to the menu on the toolbar
	 *
	 * @param menu The menu to inflate my items in to
	 * @return True if we were able to inflate it
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.horse_detail, menu);

		int star = horse.isFavourite() ? R.mipmap.star_white : R.mipmap.star_hollow_white;
		menu.findItem(R.id.favourite_action).setIcon(star);
		return true;
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		NavUtils.navigateUpFromSameTask(this);
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (currPopupWindow != null && currPopupWindow.isShowing()) {
			EditText et = (EditText) currPopupWindow.getContentView().findViewById(R.id.horseName);
			savedInstanceState.putString("sire", et.getText().toString());
			TextView date = (TextView) currPopupWindow.getContentView().findViewById(R.id.conceptionDate);
			savedInstanceState.putString("date", date.getText().toString());
		}

		savedInstanceState.putInt(Horse.HORSE_ID, horseID);
	}

	@Override
	public void onStop() {
		if (currPopupWindow != null) {
			currPopupWindow.dismiss();
			currPopupWindow = null;
		}
		super.onStop();
	}


	public void setup() {

		setContentView(R.layout.activity_horse_detail);

		layout = (CoordinatorLayout) findViewById(R.id.horse_detail_screen);
		if (Build.VERSION.SDK_INT > 22)
			layout.getForeground().setAlpha(0);

		if (horse.isFemale() && horse.getStatus() == Horse.HORSE_STATUS.DORMANT)
			findViewById(R.id.add_pregnancy).setVisibility(View.VISIBLE);

		if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT)
			findViewById(R.id.give_birth).setVisibility(View.VISIBLE);

		setUpHeader();


		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		HorseDetailsFragment fragment = HorseDetailsFragment.newInstance(horse);
		transaction.replace(R.id.horseDetails,  fragment, HorseDetailsFragment.FRAGMENT_TAG).commit();


		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle(horse.getName());
		}

	}

	/**
	 * sets up the image view and top three buttons
	 */
	private void setUpHeader() {
		//---------SET UP PHOTO------------
		ImageView horsePhoto = (ImageView) findViewById(R.id.horse_photo);
		if (horse.getImagePath().isEmpty()) {
			int photo = horse.getStatus() == Horse.HORSE_STATUS.FOAL ? R.drawable.default_foal : R.drawable.default_horse;
			horsePhoto.setImageResource(photo);
		} else
			horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.getImagePath(), 300, 300));

		if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT) {
			TextView pregnancyStatus = (TextView) findViewById(R.id.horse_status);
			pregnancyStatus.setText(getString(R.string.pregnancy_left_time, horse.getCurrentBirth().getBirthDurationAsString()));
		}

		//age
		TextView age = (TextView) findViewById(R.id.age);
		age.setText(getString(R.string.horse_age, horse.getAge()));
		//gender
		TextView gender = (TextView) findViewById(R.id.sex);
		gender.setText(horse.isFemale() ? "Female" : "Male");
		//status
		TextView status = (TextView) findViewById(R.id.pregnantStatus);
		status.setText(horse.getStatusString());
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addNewPregnancy(View v) {

		FloatingActionsMenu fab = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
		fab.collapse();

		View popupView = getLayoutInflater().inflate(R.layout.fragment_add_pregnancy, null);
		if (sire != null)
			((EditText) popupView.findViewById(R.id.horseName)).setText(sire);
		if (!currDate.isEmpty())
			((TextView) popupView.findViewById(R.id.conceptionDate)).setText(currDate);
		else
			((TextView) popupView.findViewById(R.id.conceptionDate)).setText(DateTime.now().toString(dateFormatter));

		currPopupWindow = new PopupWindow(popupView,
				ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

		currPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.horse_detail_screen);
				if (Build.VERSION.SDK_INT > 22)
					layout.getForeground().setAlpha(0);
			}
		});
		currPopupWindow.setAnimationStyle(R.style.Animation);
		layout.post(new Runnable() {
			@Override
			public void run() {
				currPopupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
				if (Build.VERSION.SDK_INT > 22)
					layout.getForeground().setAlpha(220);
			}
		});

	}


	//Toggles favouring horse
	public void favouriteAction(MenuItem item) {
		//Updates horse
		horse.toggleFavourite();
		getHelper().getHorseDataDao().update(horse);

		//Updates the star on the view
		int star = horse.isFavourite() ? R.mipmap.star_white : R.mipmap.star_hollow_white;
		item.setIcon(ContextCompat.getDrawable(this, star));
	}


	/**
	 * Deletes the horse we are currently looking at
	 *
	 * @param v The view that led us here
	 */
	public void deleteHorse(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog;
		final Context context = this;

		dialog = builder.setMessage("Are you sure you want to delete this horse? You cannot retrieve this information!")
				.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						//Do this database transaction in case we accidentally delete more than one horse
						try {
							TransactionManager.callInTransaction(getConnectionSource(),
									new Callable<Void>() {
										public Void call() throws Exception {
											Birth b = horse.getCurrentBirth();
											if (getHelper().getHorseDataDao().delete(horse) != 1)
												throw new SQLException("0 or greater than 1 horse were to be deleted");

											if (getHelper().getBirthsDataDao().delete(b) != 1)
												throw new SQLException("0 or greater than 1 birth were to be deleted, while we need it to be 1");
											return null;
										}
									});
							//finish the activity
							Toast.makeText(context, "Horse successfully deleted", Toast.LENGTH_LONG).show();
							finish();
						} catch (SQLException e) {
							Toast.makeText(context, "Unable to delete this horse", Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}

					}
				}).setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
			}
		});
		dialog.show();

	}

	public void cancel(View v) {
		currPopupWindow.dismiss();
		currPopupWindow = null;
	}

	public void makePregnant(View view) {

		view = view.getRootView();

		String conceptionDateString = ((TextView) view.findViewById(R.id.conceptionDate)).getText().toString();
		DateTime conceptionDate = dateFormatter.parseDateTime(conceptionDateString);

		String sire = ((TextView) view.findViewById(R.id.horseName)).getText().toString();

		Birth birth = new Birth(horse, sire, conceptionDate, conceptionDate.plusDays(R.integer.days_to_birth));

		horse.getBirths().add(birth);
		horse.setCurrentBirth(birth);
		getHelper().getHorseDataDao().update(horse);

		cancel(view);
		recreate();
	}

	public void selectDate(View view) {
		String date = ((TextView) view).getText().toString();

		DateTime conceptionMin = DateTime.now().minusYears(1);
		DialogFragment dialog = DatePickerFragment.newInstance(date, this, conceptionMin);
		dialog.show(getFragmentManager(), "conceptionDatePicker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		int textViewID = R.id.conceptionDate;

		TextView dateField = (TextView) currPopupWindow.getContentView().findViewById(textViewID);
		String parsedDob = day + "/" + (month + 1) + "/" + year;
		dateField.setText(parsedDob);
	}

	public void giveBirth(View view) {
		Birth b = horse.getCurrentBirth();
		b.setBirthTime(DateTime.now());

		//name = horse's foal //birth = current horses birth object //true for female //null for notes //foal status //no image
		Horse foal = new Horse(horse.getName()+"'s foal", horse.getCurrentBirth(), true, null, Horse.HORSE_STATUS.FOAL, null);
		getHelper().getHorseDataDao().assignEmptyForeignCollection(foal, "milestones");
		getHelper().getHorseDataDao().create(foal);
		foal.createMilestones(this);

		b.setHorse(foal);
		getHelper().getBirthsDataDao().update(b);

		horse.setCurrentBirth(null);
		horse.setStatus(Horse.HORSE_STATUS.DORMANT);
		getHelper().getHorseDataDao().update(horse);

		FloatingActionsMenu fab = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
		fab.collapse();

		finish();
	}

	public void deleteBirth(Birth b) {

	}

}