package com.abc.foaled.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.database.ORMBaseActivity;
import com.abc.foaled.fragments.HorseDetailsFragment;
import com.abc.foaled.fragments.MilestonesListFragment;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;
import com.abc.foaled.R;
import com.abc.foaled.models.Milestone;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.j256.ormlite.misc.TransactionManager;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import static com.abc.foaled.helpers.DateTimeHelper.DATE_FORMATTER;

public class HorseDetailActivity extends ORMBaseActivity<DatabaseHelper> {

	private static final int HORSE_EDIT_REQUEST_CODE = 31;

	private Horse horse;
	private int horseID;

	private PopupWindow currPopupWindow;
	private String sire;
	private String currDate = "";
	private CoordinatorLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Builds the horse from the passed through horseID, or if re-building on screen rotate
		//  then it gets the info from the bundle
		if (savedInstanceState != null && savedInstanceState.containsKey(Horse.HORSE_ID))
			horseID = savedInstanceState.getInt(Horse.HORSE_ID, 0);
		else
			horseID = getIntent().getIntExtra(Horse.HORSE_ID, 0);

		//If no horse ID passed through to this activity, throw error
		if (horseID == 0)
			throw new IllegalArgumentException("No HorseID added when starting this activity");

		//retrieve relevant horse
		horse = getHelper().getHorseDataDao().queryForId(horseID);

		//Checks if milestone is to be completed, and updates the right milestone if it is
		int milestoneID = getIntent().getIntExtra(Milestone.MILESTONE_ID, 0);
		if (milestoneID != 0)
			getHelper().getMilestonesDataDao().update(horse.completeMilestone(milestoneID));

		//Gets the sire from bundle. Used to deal with rotation
		//TODO investigate just getting the data from the Horse object instead
		if (savedInstanceState != null && savedInstanceState.containsKey("sire"))
			sire = savedInstanceState.getString("sire");

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
			EditText et = currPopupWindow.getContentView().findViewById(R.id.notificationCheckbox3);
			savedInstanceState.putString("sire", et.getText().toString());
			TextView date = currPopupWindow.getContentView().findViewById(R.id.notificationCheckbox1);
			savedInstanceState.putString("date", date.getText().toString());
		}
		savedInstanceState.putInt(Horse.HORSE_ID, horseID);
		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * If activity is stopped for any reason, close the pop-up window
	 */
	@Override
	public void onStop() {
		if (currPopupWindow != null) {
			currPopupWindow.dismiss();
			currPopupWindow = null;
		}
		super.onStop();
	}

	/**
	 * Populates all the data into the views on this screen
	 */
	private void setup() {

		setContentView(R.layout.activity_horse_detail);

		layout = findViewById(R.id.horse_detail_screen);
		//Makes the screen clear, in case we are re-building screen after the pop-up window was still showing
		//TODO perhaps move the below alpha check to the onStop method instead?
		if (Build.VERSION.SDK_INT > 22)
			layout.getForeground().setAlpha(0);

		if (horse.isFemale() && horse.getStatus() == Horse.HORSE_STATUS.DORMANT)
			findViewById(R.id.add_pregnancy).setVisibility(View.VISIBLE);

		if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT)
			findViewById(R.id.give_birth).setVisibility(View.VISIBLE);

		setUpHeader();

		//Builds a fragment to go on the screen that will have all the correct data
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		HorseDetailsFragment fragment = HorseDetailsFragment.newInstance(horse, getHelper());
		transaction.replace(R.id.horseDetails, fragment, HorseDetailsFragment.FRAGMENT_TAG);

		//


		if (horse.getStatus() == Horse.HORSE_STATUS.FOAL) {
			ArrayList<Milestone> list = new ArrayList<>(horse.getMilestones());
			MilestonesListFragment milestonesListFragment = MilestonesListFragment.newInstance(list, getHelper().getMilestonesDataDao());

			transaction.replace(R.id.milestoneListFrameLayout, milestonesListFragment, MilestonesListFragment.FRAGMENT_TAG);
		}

		transaction.commit();

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle(horse.getName());
		}
	}

	/**
	 * Sets up the image view and top three tags
	 */
	private void setUpHeader() {
		//---------SET UP PHOTO------------
		ImageView horsePhoto = findViewById(R.id.horse_photo);
		if (horse.getImagePath().isEmpty()) {
			int photo = horse.getStatus() == Horse.HORSE_STATUS.FOAL ? R.drawable.default_foal : R.drawable.default_horse;
			horsePhoto.setImageResource(photo);
		} else
			horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.getImagePath(), 300, 300));

		if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT) {
			TextView pregnancyStatus = findViewById(R.id.horse_status);
			pregnancyStatus.setText(getString(R.string.pregnancy_left_time, horse.getCurrentBirth().getBirthDurationAsString()));
		}

		//age
		TextView age = findViewById(R.id.age);
		age.setText(getString(R.string.horse_age, horse.getAge()));
		//gender
		TextView gender = findViewById(R.id.sex);
		gender.setText(horse.isFemale() ? "Female" : "Male");
		//status
		TextView status = findViewById(R.id.pregnantStatus);
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

	/**
	 * Used to enter details to add a pregnancy to the current horse
	 *
	 * @param v The view that was clicked to get here
	 */
	public void showAddNewPregnancyPopupWindow(View v) {

		FloatingActionsMenu fab = findViewById(R.id.multiple_actions);
		fab.collapse();

		//Creates a pop-up view and populates it
		View popupView = getLayoutInflater().inflate(R.layout.fragment_add_pregnancy, (ViewGroup) v.getRootView());
		if (sire != null)
			((EditText) popupView.findViewById(R.id.notificationCheckbox3)).setText(sire);

		if (!currDate.isEmpty())
			((TextView) popupView.findViewById(R.id.notificationCheckbox1)).setText(currDate);
		else
			((TextView) popupView.findViewById(R.id.notificationCheckbox1)).setText(DateTime.now().toString(DATE_FORMATTER));

		currPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

		currPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				CoordinatorLayout layout = findViewById(R.id.horse_detail_screen);
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

	/**
	 * Makes the current horse pregnant
	 *
	 * @param view The view that triggered this method call
	 */
	public void makePregnant(View view) {

		view = view.getRootView();

		//gets conception date
		String conceptionDateString = ((TextView) view.findViewById(R.id.notificationCheckbox1)).getText().toString();
		DateTime conceptionDate = DATE_FORMATTER.parseDateTime(conceptionDateString);

		//gets sire
		String sire = ((TextView) view.findViewById(R.id.notificationCheckbox3)).getText().toString();

		//makes birth
		Birth birth = new Birth(horse, sire, conceptionDate, conceptionDate.plusDays(getResources().getInteger(R.integer.days_to_birth)));
		getHelper().getBirthsDataDao().create(birth);

		//updates horse
		horse.setCurrentBirth(birth);
		getHelper().getHorseDataDao().update(horse);

		//rebuilds page
		cancel();
		recreate();
	}

	/**
	 * Edits the current horse
	 *
	 * @param view View that called this method
	 */
	public void savePregnancyEdit(View view) {

		view = view.getRootView();

		//gets conception date
		String conceptionDateString = ((TextView) view.findViewById(R.id.notificationCheckbox1)).getText().toString();
		DateTime conceptionDate = DATE_FORMATTER.parseDateTime(conceptionDateString);

		//gets sires
		String sire = ((TextView) view.findViewById(R.id.siresName)).getText().toString();

		//gets current birth object and updates
		Birth birth = horse.getCurrentBirth();
		birth.setConception(conceptionDate);
		birth.setSire(sire);

		getHelper().getBirthsDataDao().update(birth);

		cancel();
		recreate();
	}

	/**
	 * Toggles favouring horse
	 */
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
	 * @param v The view called this method
	 */
	@SuppressWarnings("UnusedParameters")
	public void deleteHorse(View v) {
		//TODO fix this to include deleting pregnant horse as well
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

	/**
	 * dismiss current popup window
	 */
	public void cancel() {
		if (currPopupWindow != null) {
			currPopupWindow.dismiss();
			currPopupWindow = null;
		}
	}


	/**
	 * Finish a pregnancy and create a new horse
	 *
	 * @param view View that called this method
	 */
	@SuppressWarnings("UnusedParameters")
	public void giveBirth(View view) {
		Birth b = horse.getCurrentBirth();
		b.setBirthTime(DateTime.now());

		//Creates the new foal and gives it milestones. Also updates the birth object to be attached to the new foal
		//name = horse's foal //birth = current horses birth object //true for female //null for notes //foal status //no image
		Horse foal = new Horse(horse.getName() + "'s foal", horse.getCurrentBirth(), true, null, Horse.HORSE_STATUS.FOAL, null);
		//TODO make a static field instead of hard-coding "milestones"
		getHelper().getHorseDataDao().assignEmptyForeignCollection(foal, "milestones");
		getHelper().getHorseDataDao().create(foal);
		foal.createMilestones(this);

		b.setHorse(foal);
		getHelper().getBirthsDataDao().update(b);

		horse.setCurrentBirth(null);
		horse.setStatus(Horse.HORSE_STATUS.DORMANT);
		getHelper().getHorseDataDao().update(horse);

		FloatingActionsMenu fab = findViewById(R.id.multiple_actions);
		fab.collapse();

		finish();
	}

	/**
	 * Edit a current pregnancy for a horse
	 *
	 * @param view View that called this method
	 */
	public void editPregnancy(View view) {

		//Creates and populates a pop-up window
		View popupView = getLayoutInflater().inflate(R.layout.fragment_edit_pregnancy, (ViewGroup) view.getRootView());
		if (horse.getCurrentBirth().getSire() != null)
			((EditText) popupView.findViewById(R.id.siresName)).setText(horse.getCurrentBirth().getSire());

		((TextView) popupView.findViewById(R.id.notificationCheckbox1)).setText(horse.getCurrentBirth().getConception().toString(DATE_FORMATTER));

		currPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

		currPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				CoordinatorLayout layout = findViewById(R.id.horse_detail_screen);
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

	/**
	 * Deletes the current pregnancy for the horse
	 *
	 * @param view View that called this method
	 */
	public void deletePregnancy(View view) {

		Birth birth = horse.getCurrentBirth();
		getHelper().getBirthsDataDao().delete(birth);

		horse.setCurrentBirth(null);
		horse.setStatus(Horse.HORSE_STATUS.DORMANT);
		getHelper().getHorseDataDao().update(horse);

		cancel();
		recreate();
	}

	/**
	 * Starts a new activity where you can edit the current horse's details
	 *
	 * @param view View that called this method
	 */
	public void editHorseDetails(View view) {
		FloatingActionsMenu menu = (FloatingActionsMenu) view.getParent();
		menu.collapse();

		Intent intent = new Intent(this, EditHorseActivity.class);
		intent.putExtra(Horse.HORSE_ID, horseID);
		startActivityForResult(intent, HORSE_EDIT_REQUEST_CODE);
	}

	/**
	 * Used to recreate the page when a successful edit of the horse occurred
	 *
	 * @param requestCode Custom code sent back from the closing activity
	 * @param resultCode  Code indicating the type of closure of the activity (cancelled, ok etc.)
	 * @param data        Data sent back from the closing activity
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == HORSE_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
			recreate();
		}
	}
}