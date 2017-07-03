package com.abc.foaled.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.adaptors.MilestoneAdaptor;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.database.ORMBaseActivity;
import com.abc.foaled.fragments.DatePickerFragment;
import com.abc.foaled.fragments.HorseBirthNotesFragment;
import com.abc.foaled.fragments.HorseNoteFragment;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;
import com.abc.foaled.R;
import com.abc.foaled.notifications.NotificationPublisher;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import static android.view.View.GONE;

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
			horseID = savedInstanceState.getInt("HorseID");
		else
			horseID = getIntent().getIntExtra("HorseID", 0);

		horse = getHelper().getHorseDataDao().queryForId(horseID);

		if (savedInstanceState != null && savedInstanceState.containsKey("sire")) {
			sire = savedInstanceState.getString("sire");
		}

		if (savedInstanceState != null && savedInstanceState.containsKey("date"))
			currDate = savedInstanceState.getString("date");

		setup();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (currPopupWindow != null && currPopupWindow.isShowing()) {
			EditText et = (EditText) currPopupWindow.getContentView().findViewById(R.id.siresName);
			savedInstanceState.putString("sire", et.getText().toString());
			TextView date = (TextView) currPopupWindow.getContentView().findViewById(R.id.conceptionDate);
			savedInstanceState.putString("date", date.getText().toString());
		}

		savedInstanceState.putInt("horseID", horseID);
	}

	@Override
	public void onStop() {
		if (currPopupWindow != null) {
			currPopupWindow.dismiss();
			currPopupWindow = null;
		}
		super.onStop();
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

	public void setup() {




		setContentView(R.layout.activity_horse_detail);
		layout = (CoordinatorLayout) findViewById(R.id.horse_detail_screen);

		if (Build.VERSION.SDK_INT > 22)
			if (sire == null) {
				layout.getForeground().setAlpha(0);
			} else
				layout.getForeground().setAlpha(220);

		//age
		TextView age = (TextView) findViewById(R.id.age);
		age.setText(getString(R.string.horse_age, horse.getAge()));
		//gender
		TextView gender = (TextView) findViewById(R.id.sex);
		gender.setText(horse.isFemale() ? "Female" : "Male");
		//status
		TextView status = (TextView) findViewById(R.id.pregnantStatus);
		status.setText(horse.getStatusString());


		if (!horse.isFemale() || horse.getStatus() == Horse.HORSE_STATUS.FOAL || horse.getStatus() == Horse.HORSE_STATUS.PREGNANT) {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_pregnancy);
			fab.setVisibility(View.GONE);
		}

		//---------SET UP PHOTO------------
		ImageView horsePhoto = (ImageView) findViewById(R.id.horse_photo);
		if (horse.getImagePath().isEmpty()) {
			int photo = horse.getStatus() == Horse.HORSE_STATUS.FOAL ? R.drawable.default_foal : R.drawable.default_horse;
			horsePhoto.setImageResource(photo);
		} else
			horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.getImagePath(), 300, 300));


		if (horse.getStatus() == Horse.HORSE_STATUS.DORMANT) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


			if (getSupportFragmentManager().findFragmentByTag("GENERAL_NOTES") == null) {
				HorseNoteFragment fragment = HorseNoteFragment.newInstance(horse);
				transaction.add(R.id.horse_detail_linear_layout, fragment, "GENERAL_NOTES");
			}

			if (getSupportFragmentManager().findFragmentByTag("BIRTH_NOTES") == null && horse.isFemale()) {
				HorseBirthNotesFragment birthNotesFragment = HorseBirthNotesFragment.newInstance(horse);
				transaction.add(R.id.horse_detail_linear_layout, birthNotesFragment, "BIRTH_NOTES");
			}
			transaction.commit();
		} else if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT) {
			TextView pregnancyStatus = (TextView) findViewById(R.id.horse_status);
			pregnancyStatus.setText(getString(R.string.pregnancy_left_time, horse.getCurrentBirth().getBirthDurationAsString()));

		}


		if (sire != null)
			addNewPregnancyFragment(layout);
/*        switch (horse.getStatus()) {

            case FOAL:
                setContentView(R.layout.activity_foal_detail);
                setUpImageView();
                setUpMilestones();
                break;

            case RETIRED:
                setContentView(R.layout.activity_horse_detail);
                break;

            case MAIDEN:
                //ADD EXTRA thing VIEW SAYING RISKS OF MAIDEN PREGNANCY
                setContentView(R.layout.activity_horse_detail);
                setUpImageView();
                setUpPregnant();

                LinearLayout layout =(LinearLayout) this.findViewById(R.id.horse_detail_linear_layout);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView tv = new TextView(this);
                tv.setId(R.id.maidenTextView);
                tv.setText("This is a maiden pregnancy, make sure you take extra special care");
                tv.setBackgroundColor(Color.BLUE);
                layout.addView(tv, 1, lparams);

                break;

            case DORMANT:
                setContentView(R.layout.activity_horse_detail);
                setUpImageView();
                break;

            case PREGNANT:
                setContentView(R.layout.activity_horse_detail);
                setUpImageView();
                setUpPregnant();
                break;
        }*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle(horse.getName());
		}

	}

	private void setupDormant() {
/*        Button haveBirth = (Button)this.findViewById(R.id.button_add_pregnancy);
		haveBirth.setText("Add Pregnancy");
        haveBirth.setBackgroundColor( ContextCompat.getColor(this, R.color.colorAccent));

        haveBirth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddNewPregnancyFragment(v);
            }
        });*/
	}

	private void setUpImageView() {
		TextView tv = (TextView) findViewById(R.id.maidenTextView);
		if (tv != null) {
			tv.setVisibility(GONE);
		}

		// TODO: If it's a foal display which notification it is up to
		TextView age = (TextView) findViewById(R.id.age);
		age.setText(horse.getAge());

		TextView gender = (TextView) findViewById(R.id.sex);
		gender.setText(horse.isFemale() ? "Female" : "Male");

		TextView status = (TextView) findViewById(R.id.pregnantStatus);
		status.setText(horse.getStatusString());

		//sets up the photo
		ImageView horsePhoto = (ImageView) findViewById(R.id.horse_photo);
		try {
			if (horse.getImagePath().isEmpty() && horse.getStatus() == Horse.HORSE_STATUS.FOAL)
				horsePhoto.setImageResource(R.drawable.default_foal);
			else
				horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.getImagePath(), 300, 300));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (horse.getStatus() != Horse.HORSE_STATUS.FOAL) {
			updateNotesView();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	private void setUpMilestones() {

		final RecyclerView milestoneRV = (RecyclerView) findViewById(R.id.milestone_recycler_view);
		MilestoneAdaptor adaptor = new MilestoneAdaptor(horse);
		milestoneRV.setAdapter(adaptor);
	}

	private void updateNotesView() {
/*        // used for notes for horse
		Map<String, String> map = new HashMap<>();
	    Collection<Birth> births = horse.getBirths();

	    for (Birth b : births)
		    map.put(b.getYearOfBirth(), b.getNotes());

        List<String> years = new ArrayList<>(map.keySet());

        final ExpandableListView expandableLayoutListView = (ExpandableListView) findViewById(R.id.exlistview);
        HorseNoteAdaptor adaptor = new HorseNoteAdaptor(this, years, map);
        expandableLayoutListView.setAdapter(adaptor);*/
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		NavUtils.navigateUpFromSameTask(this);
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

	public void addNewPregnancyFragment(View v) {


		FloatingActionsMenu fab = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
		fab.collapse();

		View popupView = getLayoutInflater().inflate(R.layout.fragment_add_pregnancy, null);
		if (sire != null)
			((EditText) popupView.findViewById(R.id.siresName)).setText(sire);
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


	public void AddPregnancy(View v) {
		System.out.println("Add pregnancy selected");

		// get values
/*        EditText fatherName = (EditText) findViewById(R.id.fathers_name_textView);
		Horse fatherHorse = new Horse(fatherName.getText());*/
		//TODO Move this method to the fragment. The fragment could then point back here.. but it needs to be moved to the fragment

		TextView conceptionDate = (TextView) findViewById(R.id.conceptionDate);
		//turn to date

		// add to database
		//TODO change 2nd parameter to be Father's name, date to be the date that was selected
		Birth newBirth = new Birth(horse, "TEMP", new DateTime(), new DateTime());
		getHelper().getBirthsDataDao().create(newBirth);

		ViewGroup parent = (ViewGroup) findViewById(R.id.horse_detail_screen);

		View fragment = findViewById(R.id.add_pregnancy_fragment);
		horse.setStatus(Horse.HORSE_STATUS.PREGNANT);
		horse.setCurrentBirth(newBirth);
		getHelper().getHorseDataDao().update(horse);

		setup();
		parent.removeView(fragment);
	}

	public void AddFoalFragment(View v) {
		final View popupView = getLayoutInflater().inflate(R.layout.fragment_add_foal, null);

		final PopupWindow popupWindow = new PopupWindow(popupView,
				ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		Button b = (Button) popupView.findViewById(R.id.add_foal_button);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddFoal(((EditText) popupWindow.getContentView().findViewById(R.id.news_foal_name_textView)).getText().toString());
				popupWindow.dismiss();
			}
		});
		popupWindow.setFocusable(true);
		popupWindow.setAnimationStyle(R.style.Animation);

		popupWindow.setBackgroundDrawable(new ColorDrawable());

		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	public void AddFoal(String s) {

		System.out.println("Add foal added");
		// to do show a dialog with date sex etc

		if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT || horse.getStatus() == Horse.HORSE_STATUS.MAIDEN) {

//            TextView foalName = (TextView) v.findViewById(R.id.news_foal_name_textView);
			//TODO: Get notes from horse current birth notes - also get image from default Brendan

			RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();
			RuntimeExceptionDao<Birth, Integer> birthDao = getHelper().getBirthsDataDao();

			// set birth time
			Birth birth = horse.getCurrentBirth();
			birth.setBirthTime(new DateTime());
			birthDao.update(birth);

//            Horse foal = new Horse(s, birth, "Notes", true);
			Horse foal = new Horse(s, birth, true, "Notes", Horse.HORSE_STATUS.DORMANT, null);
			horseDao.assignEmptyForeignCollection(foal, "milestones");
			foal.setStatus(Horse.HORSE_STATUS.FOAL);
			horseDao.create(foal);

			birth.setHorse(foal);
			birthDao.update(birth);

			horse.setStatus(Horse.HORSE_STATUS.DORMANT);
			horseDao.update(horse);
			setUpImageView();
			setupDormant();
			Log.d("Added horse gee", "gee");

			ViewGroup parent = (ViewGroup) findViewById(R.id.horse_detail_screen);
			View fragment = findViewById(R.id.add_foal_fragment);

			setup();
			parent.removeView(fragment);
		}
	}

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

		String sire = ((TextView) view.findViewById(R.id.siresName)).getText().toString();

		Birth birth = new Birth(horse, sire, conceptionDate, conceptionDate.plusDays(R.integer.days_to_birth));
		getHelper().getBirthsDataDao().create(birth);

		horse.setCurrentBirth(birth);
		getHelper().getHorseDataDao().update(horse);

		cancel(view);
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

/*		Birth b = horse.getCurrentBirth();
		b.setBirthTime(DateTime.now());

		Horse foal = new Horse("new_horse", horse.getCurrentBirth(), true, "notes", Horse.HORSE_STATUS.FOAL, null);
		getHelper().getHorseDataDao().assignEmptyForeignCollection(foal, "milestones");
		getHelper().getHorseDataDao().create(foal);

		foal.createMilestones(this);

		b.setHorse(foal);
		getHelper().getBirthsDataDao().update(b);

		horse.setCurrentBirth(null);
//		horse.getBirths().add(b);
		horse.setStatus(Horse.HORSE_STATUS.DORMANT);*/
		Intent horseIntent = new Intent(this, HorseDetailActivity.class);
		horseIntent.putExtra("HorseID", horseID);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(HorseDetailActivity.class);
		stackBuilder.addNextIntent(horseIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		DateTime time = DateTime.now().plusSeconds(10);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle("Message")
				.setContentText("I got a message")
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_HIGH)

				.setSmallIcon(R.mipmap.ic_launcher)
				.setLargeIcon(ImageHelper.bitmapSmaller(getResources(), R.drawable.default_foal, 50, 50))
				.setContentIntent(resultPendingIntent)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


		Intent notificationIntent = new Intent(this, NotificationPublisher.class);
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, Integer.parseInt(horseID+""+3));
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, builder.build());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		long futureInMillis = time.getMillis();
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);

	}
}