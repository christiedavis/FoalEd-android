package com.abc.foaled.Activity;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        setContentView(R.layout.activity_horse_detail);
        this.userInfo = UserInfo.getInstance();

        horseID = getIntent().getIntExtra("HorseID", 0);
        this.horse = this.userInfo.horses.get(horseID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(horse.name);
        }

        final int horseID = getIntent().getIntExtra("HorseID", 0);
        this.horse = this.userInfo.getHorseAtIndex(horseID);

        Button horseAge = (Button)this.findViewById(R.id.buttonAge);
        horseAge.setText(horse.getAge());

        TextView name = (TextView)this.findViewById(R.id.horse_name);
        name.setText(horse.name);

        TextView age = (TextView)this.findViewById(R.id.horse_age);
        age.setText(horse.getAge());

        TextView gender = (TextView)this.findViewById(R.id.buttonSex);
        gender.setText(horse.getSex());

        TextView status = (TextView)this.findViewById(R.id.buttonStatus);
        status.setText(horse.getStatusString());

        //sets up the photo
        ImageView personPhoto = (ImageView)this.findViewById(R.id.horse_photo);
        personPhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.smallImagePath, personPhoto.getMaxHeight(), personPhoto.getMaxWidth()));

         /// TODO: Set up notes with birth info - see feedback screen to play around with this functionality

//        Map<String, String> map = horse.getBirthNotes();
//        List<String> notesList = new LinkedList<>();
//        notesList.add("Current pregnancy");
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            notesList.add(entry.getKey());
//        }
//            String[] array = new String[notesList.size()];
//            notesList.toArray(array);
//
//
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.ex_layout_row_view, R.id.expandableLayoutHeaderText, array);
//        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.exlistview);
//
//        expandableLayoutListView.setAdapter(arrayAdapter);
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


    //TODO this is the delete Horse method - B
    // WHat is this used for? Does it belong in this class - C
    private boolean deleteHorse(int id) {
        RuntimeExceptionDao<Horse, Integer> horseDao = this.userInfo.getHelper().getHorseDataDao();
        Horse horse = horseDao.queryForId(id);
        //returns true if deleted 1 row (which should be the case if ID exists)
        //else returns false
        return horseDao.delete(horse) == 1;
    }

    public void AddPregnancy(View v) {
        System.out.println("Add pregnancy selected");

        // get values
        EditText fatherName = (EditText)this.findViewById(R.id.fathers_name_textView);
//        Horse fatherHorse = new Horse(fatherName.getText().toString());
        //TODO: add horse 


        EditText conceptionDate = (EditText)this.findViewById(R.id.date_of_conception);
        //turn to date -
        Calendar conceptionDateMOCK = Calendar.getInstance();
        conceptionDateMOCK.clear(Calendar.HOUR); conceptionDateMOCK.clear(Calendar.MINUTE); conceptionDateMOCK.clear(Calendar.SECOND);


        // add to database
//        Birth newBirth = new Birth(this.horse.getHorseID(), fatherHorse.getHorseID(), conceptionDateMOCK);
//        this.userInfo.getHelper().addNewBirth(newBirth);

        // go back to horse detail and update
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();

        ViewGroup parent = (ViewGroup) findViewById(R.id.horse_detail_screen);

        View fragment = findViewById(R.id.add_pregnancy_fragment);

        parent.removeView(fragment);
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
