package com.abc.foaled.Activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.DrawFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Fragment.AddPregnancyFragment;
import com.abc.foaled.Helpers.DateTimeHelper;
import com.abc.foaled.Helpers.ImageHelper;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Birth;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

//FavouriteHorsesFragment.OnListFragmentInteractionListener, FavouriteHorsesFragment.OnListFragmentInteractionListener,
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

        switch (horse.getStatus()) {

            case HORSE_STATUS_FOAL:
                setContentView(R.layout.activity_foal_detail);
                setUpImageView();
            break;

            case HORSE_STATUS_RETIRED:
                setContentView(R.layout.activity_horse_detail);
                break;

            case HORSE_STATUS_MAIDEN:
                //ADD EXTRA NOTE VIEW SAYING RISKS OF MAIDEN PREGNANCY
                setContentView(R.layout.activity_horse_detail);
                break;

            case HORSE_STATUS_DORMANT:
                setContentView(R.layout.activity_horse_detail);
                setUpImageView();
                break;

            case HORSE_STATUS_PREGNANT:
                setContentView(R.layout.activity_horse_detail);
                setUpImageView();
                break;
        }
    }

    private void setUpImageView() {
        Button horseAge = (Button)this.findViewById(R.id.buttonAge);
        horseAge.setText("Age");
        horseAge.setText(DateTimeHelper.getAgeString(horse.getAge()));

        TextView name = (TextView)this.findViewById(R.id.horse_name);
        name.setText(horse.name);

        TextView age = (TextView)this.findViewById(R.id.horse_age);
        age.setText(DateTimeHelper.getAgeString(horse.getAge()));

        TextView gender = (TextView)this.findViewById(R.id.buttonSex);
        gender.setText(horse.getSex());

        TextView status = (TextView)this.findViewById(R.id.buttonStatus);
        status.setText(horse.getStatusString());

        //sets up the photo
        ImageView horsePhoto = (ImageView)this.findViewById(R.id.horse_photo);
        if (horse.smallImagePath != null) {
            horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.smallImagePath, horsePhoto.getMaxHeight(), horsePhoto.getMaxWidth()));
        }
        else { // no horse photo, use default
            if (horse.getStatus() == Horse.HORSE_STATUS.HORSE_STATUS_FOAL) {
                //TODO: set to show default
                Bitmap foalImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_foal);
                horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(Resources.getSystem(), R.drawable.default_foal, horsePhoto.getMaxHeight(), horsePhoto.getMaxWidth()));
            } else {
                Bitmap horseImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_horse);
                horsePhoto.setImageBitmap(horseImage);
            }
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

    private void updateNotesView() {

        Map<String, String> map = horse.getBirthNotes(this);
        List<String> notesList = new LinkedList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            notesList.add(entry.getKey());
        }
        String[] array = new String[notesList.size()];
        notesList.toArray(array);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.ex_layout_row_view, R.id.expandableLayoutHeaderText, array);
        final ArrayAdapter<String> notesAdapter = new ArrayAdapter<String>(this, R.layout.ex_layout_row_view, R.id.expandableLayoutTextContent, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.exlistview);

        expandableLayoutListView.setAdapter(arrayAdapter);
        expandableLayoutListView.setAdapter(notesAdapter);
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

    public void AddNewPregnancy(View v) {
        System.out.println("Add pregnancy clicked");

        //TODO: make it inflate properly

        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        AddPregnancyFragment fragment = AddPregnancyFragment.newInstance();

        fragmentManager.replace(R.id.horse_detail_screen, fragment).commit();
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
        horse.setStatus(Horse.HORSE_STATUS.HORSE_STATUS_PREGNANT);
        updateNotesView();

        parent.removeView(fragment);
    }

    public void AddFoal(View v) {
        System.out.println("Add foal added");
        // to do show a dialog with date sex etc

        if (horse.getStatus() == Horse.HORSE_STATUS.HORSE_STATUS_PREGNANT) {

            // set birth time
            horse.currentBirth.birth_time = new DateTime();
            Horse foal = new Horse("New Foal", horse.currentBirth, "Markings yolo", "Notes", true);
            foal.setStatus(Horse.HORSE_STATUS.HORSE_STATUS_FOAL);
            //set image to be default

            this.userInfo.getHelper().addNewHorse(horse.currentBirth, foal);

            horse.setStatus(Horse.HORSE_STATUS.HORSE_STATUS_DORMANT);
            Log.d("Added horse gee", "gee");
        }
    }

    @Override
    public void onAddPregnancyFragmentInteraction(Uri uri) {

    }

    public void ChooseDate(View v) {
        // display date picker
        // on selectiong - put in the thing
    }

    public void Cancel(View v) {
        System.out.println("Cancel birth");
        // go back to horse detail
    }
}


//   This is brendan's fragment note stuff. TO be deleted if we decide we're not using it.
//    @Override //needed
//    public void onFragmentInteraction(Uri uri) {
//
//    }
//  @Override
//public void onListFragmentInteraction(Horse item) {
//
//}
// @Override
//    public void onResume() {
//        this.horse = this.userInfo.horses.get(horseID);
//        TextView tvText = (TextView) findViewById(R.id.horse_only_note_content);
//
//        StringBuilder stringBuilder = new StringBuilder(horse.notes);
//        if (stringBuilder.length() >= 50) {
//            stringBuilder.setLength(47);
//            stringBuilder.append("...");
//        }
//
//        tvText.setText(stringBuilder.toString());
//        super.onResume();
//    }
//       FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
//                HorseNoteFragment fragment = HorseNoteFragment.newInstance();
//
//                fragmentManager.replace(R.id.horseDetailNotes, fragment).commit();
//
//                final TextView tvTitle = (TextView) findViewById(R.id.horse_only_note_title);
//                TextView tvText = (TextView) findViewById(R.id.horse_only_note_content);
//
//                StringBuilder stringBuilder = new StringBuilder(horse.notes);
//                if (stringBuilder.length() >= 50) {
//                    stringBuilder.setLength(47);
//                    stringBuilder.append("...");
//                }
//
//                tvTitle.setText(horse.name+"'s General Notes");
//                tvText.setText(stringBuilder.toString());
//
//                CardView cv = (CardView) findViewById(R.id.horse_only_note);
//                cv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//            public void onClick(View v) {
//                        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
//                        intent.putExtra("title", tvTitle.getText().toString());
//                        intent.putExtra("note", horse.notes);
//                        intent.putExtra("horseID", horseID);
//                        startActivity(intent);
//                    }
//                });
