package com.abc.foaled.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.foaled.Adaptors.HorseNoteAdaptor;
import com.abc.foaled.Fragment.AddPregnancyFragment;
import com.abc.foaled.Helpers.DateTimeHelper;
import com.abc.foaled.Helpers.ImageHelper;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Birth;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class HorseDetailActivity extends AppCompatActivity
    implements AddPregnancyFragment.OnFragmentInteractionListener
{
    UserInfo userInfo;
    Horse horse;
    int horseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.userInfo = UserInfo.getInstance(this);

        horseID = getIntent().getIntExtra("HorseID", 0);
        this.horse = this.userInfo.getHorseByID(horseID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(horse.name);
        }
        setup();
    }

    private void setup() {
        Log.d("Horse Detail Activity", "- horse status" + horse.getStatusString());

        switch (horse.getStatus()) {

            case HORSE_STATUS_FOAL:
                setContentView(R.layout.activity_foal_detail);
                setUpImageView();
                break;

            case HORSE_STATUS_RETIRED:
                setContentView(R.layout.activity_horse_detail);
                break;

            case HORSE_STATUS_MAIDEN:
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

            case HORSE_STATUS_DORMANT:
                setContentView(R.layout.activity_horse_detail);
                setUpImageView();
                break;

            case HORSE_STATUS_PREGNANT:
                setContentView(R.layout.activity_horse_detail);
                setUpImageView();
                setUpPregnant();
                break;
        }
    }

    private void setUpPregnant() {
        Button haveBirth = (Button)this.findViewById(R.id.button_add_pregnancy);
        haveBirth.setText("Give Birth");
        if (horse.getStatus() == Horse.HORSE_STATUS.HORSE_STATUS_PREGNANT) {
            haveBirth.setBackgroundColor(Color.RED);
        } else {
            haveBirth.setBackgroundColor(Color.BLUE);
        }

        haveBirth.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  AddFoal(v);
              }
        });
    }

    private void setupDormant() {
        Button haveBirth = (Button)this.findViewById(R.id.button_add_pregnancy);
        haveBirth.setText("Add Pregnancy");
        haveBirth.setBackgroundColor( ContextCompat.getColor(this, R.color.colorAccent));

        haveBirth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddNewPregnancyFragment(v);
            }
        });
    }

    private void setUpImageView() {

        TextView tv = (TextView)findViewById(R.id.maidenTextView);
        if (tv != null) {
            tv.setVisibility(GONE);
        }

        Button horseAge = (Button)this.findViewById(R.id.buttonAge);
        horseAge.setText("Age");
        horseAge.setText(DateTimeHelper.getAgeString(horse.getAge()));

        TextView name = (TextView)this.findViewById(R.id.horse_name);
        name.setText(horse.name);

        // TODO: If it's a foal display which notification it is up to
        TextView age = (TextView)this.findViewById(R.id.horse_status);
        age.setText(horse.getStatusString());

        TextView gender = (TextView)this.findViewById(R.id.buttonSex);
        gender.setText(horse.getSex());

        TextView status = (TextView)this.findViewById(R.id.buttonStatus);
        status.setText(horse.getStatusString());

        if (horse.isFavourite())
            ((ImageView) findViewById(R.id.favourite)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star));
        else
            ((ImageView) findViewById(R.id.favourite)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_hollowed));


        //sets up the photo
        ImageView horsePhoto = (ImageView) findViewById(R.id.horse_photo);
        try {
            if (horse.smallImagePath != null && horse.smallImagePath.contains("placeholder"))
                if (horse.getStatus() == Horse.HORSE_STATUS.HORSE_STATUS_FOAL)
                    horsePhoto.setImageDrawable(Drawable.createFromStream(getAssets().open("default_foal.jpg"), null));
                else
                    horsePhoto.setImageDrawable(Drawable.createFromStream(getAssets().open("default_horse.jpg"), null));
            else
                horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.smallImagePath, 300, 300));
        } catch (Exception e) {
            e.printStackTrace();
        }
       if (horse.getStatus() != Horse.HORSE_STATUS.HORSE_STATUS_FOAL) {
           updateNotesView();
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.userInfo.release();
    }

    public void editNotesCallback(String note, String birthId) {
        horse.updateBirth(this, birthId, note);
    }

    private void updateNotesView() {

        Map<String, List<String>> map = horse.getBirthNotes(this);
        List<String> years = new ArrayList<>(map.keySet());

        final ExpandableListView expandableLayoutListView = (ExpandableListView) findViewById(R.id.exlistview);

        HorseNoteAdaptor adaptor = new HorseNoteAdaptor(this, years, map);

        expandableLayoutListView.setAdapter(adaptor);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.userInfo.updateHorse(horse);
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
        System.out.println("Add pregnancy clicked");

        //TODO: make it inflate properly - Brendan
        //ALSO hook it up

        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        AddPregnancyFragment fragment = AddPregnancyFragment.newInstance();

        fragmentManager.replace(R.id.horse_detail_screen, fragment).commit();
//        if (horse.isMaiden()) {  - this doesnt work because i havent inflated my fragment
//            TextView addPregnancyLabel = (TextView) fragment.findViewById(R.id.add_pregnancy_fragment_text);
//            addPregnancyLabel.append("/nYour horse is a maiden pregnancy etc");
//        }
    }

    public void AddPregnancy(View v) {
        System.out.println("Add pregnancy selected");

        // get values
        EditText fatherName = (EditText)this.findViewById(R.id.fathers_name_textView);
        Horse fatherHorse = new Horse(fatherName.getText().toString());
        //TODO: add horse 

        EditText conceptionDate = (EditText)this.findViewById(R.id.date_of_conception);
        //turn to date

        // add to database
        Birth newBirth = new Birth(this.horse.getHorseID(), fatherHorse.getHorseID(), new Date());
        this.userInfo.getHelper().addNewBirth(newBirth);

        // go back to horse detail and update
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();

        ViewGroup parent = (ViewGroup) findViewById(R.id.horse_detail_screen);

        View fragment = findViewById(R.id.add_pregnancy_fragment);
        horse.setStatus(Horse.HORSE_STATUS.HORSE_STATUS_PREGNANT, this);
        horse.currentBirth = newBirth;

        setup();
        parent.removeView(fragment);
    }

    public void AddFoal(View v) {
        System.out.println("Add foal added");
        // to do show a dialog with date sex etc

        if (horse.getStatus() == Horse.HORSE_STATUS.HORSE_STATUS_PREGNANT || horse.getStatus() == Horse.HORSE_STATUS.HORSE_STATUS_MAIDEN) {

            // set birth time
            horse.currentBirth.birth_time = new DateTime();
            Horse foal = new Horse("New Foal", horse.currentBirth, "Notes", true);
            foal.setStatus(Horse.HORSE_STATUS.HORSE_STATUS_FOAL, this);
            //set image to be default

            this.userInfo.getHelper().addNewHorse(horse.currentBirth, foal);

            horse.setStatus(Horse.HORSE_STATUS.HORSE_STATUS_DORMANT, this);
            setUpImageView();
            setupDormant();
            Log.d("Added horse gee", "gee");

            //TODO: Brendan (?) we need a dialog to pop up here asking for the foals name and birth date
            // there should also be a message saying there will be a chance to edit later
        }
    }

    @Override
    public void onAddPregnancyFragmentInteraction(Uri uri) {
    }

    public void ChooseDate(View v) {
        // TODO: Brendan - display date picker -  on selecting - put in the thing
    }

    public void Cancel(View v) {
        System.out.println("Cancel birth");
        // go back to horse detail
    }
}