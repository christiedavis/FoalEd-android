package com.abc.foaled.activities;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.abc.foaled.adaptors.HorseNoteAdaptor;
import com.abc.foaled.adaptors.MilestoneAdaptor;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.database.ORMBaseActivity;
import com.abc.foaled.fragments.AddFoalFragment;
import com.abc.foaled.fragments.AddPregnancyFragment;
import com.abc.foaled.fragments.DatePickerFragment;
import com.abc.foaled.helpers.DateTimeHelper;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;
import com.abc.foaled.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class HorseDetailActivity extends ORMBaseActivity<DatabaseHelper>
    implements AddPregnancyFragment.OnFragmentInteractionListener, AddFoalFragment.OnFragmentInteractionListener
{
    Horse horse;
    int horseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        horseID = getIntent().getIntExtra("HorseID", 0);
	    horse = getHelper().getHorseDataDao().queryForId(horseID);
        setup();
    }

	/**
	 * Inflates the custom menu items in to the menu on the toolbar
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

    private void setup() {
        Log.d("Horse Detail Activity", "- horse status" + horse.getStatusString());

		setContentView(R.layout.activity_horse_detail);


		TextView age = (TextView) findViewById(R.id.age);
		age.setText(horse.getAge());

		TextView gender = (TextView) findViewById(R.id.sex);
		gender.setText(horse.isFemale() ? "Female" : "Male");

		TextView status = (TextView) findViewById(R.id.pregnantStatus);
		status.setText(horse.getStatusString());


		//sets up the photo
		ImageView horsePhoto = (ImageView) findViewById(R.id.horse_photo);

		if (horse.getImagePath().isEmpty()) {
			int photo = horse.getStatus() == Horse.HORSE_STATUS.FOAL ? R.drawable.default_foal : R.drawable.default_horse;
			horsePhoto.setImageResource(photo);
		} else
			horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.getImagePath(), 300, 300));


		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);



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

    private void setUpPregnant() {
/*        Button haveBirth = (Button) findViewById(R.id.button_add_pregnancy);
        haveBirth.setText("Give Birth");
        if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT) {
            haveBirth.setBackgroundColor(Color.RED);
        } else {
            haveBirth.setBackgroundColor(Color.BLUE);
        }

        haveBirth.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  AddFoalFragment(v);
              }
        });*/
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

    public void AddNewPregnancyFragment(View v) {
        Log.d("", "Add pregnancy button was clicked");

	    View popupView = getLayoutInflater().inflate(R.layout.fragment_add_pregnancy, null);

	    PopupWindow popupWindow = new PopupWindow(popupView,
			    ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

	    popupWindow.setFocusable(true);
	    popupWindow.setAnimationStyle(R.style.Animation);

	    popupWindow.setBackgroundDrawable(new ColorDrawable());

	    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void AddPregnancy(View v) {
        System.out.println("Add pregnancy selected");

        // get values
/*        EditText fatherName = (EditText) findViewById(R.id.fathers_name_textView);
        Horse fatherHorse = new Horse(fatherName.getText());*/
        //TODO Move this method to the fragment. The fragment could then point back here.. but it needs to be moved to the fragment

        TextView conceptionDate = (TextView) findViewById(R.id.date_of_conception);
        //turn to date

        // add to database
	    //TODO change 2nd parameter to be Father's name, date to be the date that was selected
        Birth newBirth = new Birth(horse, "TEMP", new Date(), new DateTime());
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
			    AddFoal(((EditText)popupWindow.getContentView().findViewById(R.id.news_foal_name_textView)).getText().toString());
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

    @Override
    public void onAddPregnancyFragmentInteraction(Uri uri) {
    }

    @Override
    public void onAddFoalFragmentInteraction (Uri uri) {
    }

    public void ChooseDate(View v) {
	    TextView editText = (TextView) v.getRootView().findViewById(R.id.date_of_conception);
	    DialogFragment dialog = new DatePickerFragment();
	    ((DatePickerFragment) dialog).setViewResult(editText);
	    dialog.setRetainInstance(true);
//        ((DatePickerDialog)dialog.getDialog()).getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
	    dialog.show(getFragmentManager(), "datePicker");
    }

    public void Cancel(View v) {
        System.out.println("Cancel birth");
        // TODO: leave fragmemt go back to horse detail
    }
}