package com.abc.foaled.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Fragment.FavouriteHorsesFragment;
import com.abc.foaled.Fragment.HorseNoteFragment;
import com.abc.foaled.Fragment.NotificationSettingsFragment;
import com.abc.foaled.Helpers.DateTimeHelper;
import com.abc.foaled.Helpers.ImageHelper;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Births;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;


public class HorseDetailActivity extends ORMBaseActivity<DatabaseHelper>
    implements FavouriteHorsesFragment.OnListFragmentInteractionListener, NotificationSettingsFragment.OnFragmentInteractionListener {

    UserInfo userInfo;
    Horse horse;
    int horseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horse_detail);
        this.userInfo = UserInfo.getInstance();

        horseID = getIntent().getIntExtra("HorseID", 0);
        this.horse = this.userInfo.getHorseAtIndex(horseID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(horse.name);
        }

        Button horseAge = (Button)this.findViewById(R.id.buttonAge);
        horseAge.setText(DateTimeHelper.printPeriod(horse.getAge()));

        TextView name = (TextView)this.findViewById(R.id.horse_name);
        name.setText(horse.name);

        TextView age = (TextView)this.findViewById(R.id.horse_age);
        age.setText(DateTimeHelper.printPeriod(horse.getAge()));

        TextView gender = (TextView)this.findViewById(R.id.buttonSex);
        gender.setText(horse.getSex());

        TextView status = (TextView)this.findViewById(R.id.buttonStatus);
        status.setText(horse.getStatusString());

        //sets up the photo
        ImageView personPhoto = (ImageView)this.findViewById(R.id.horse_photo);
        personPhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.smallImagePath, personPhoto.getMaxHeight(), personPhoto.getMaxWidth()));


        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        HorseNoteFragment fragment = HorseNoteFragment.newInstance();
        //fragment.setListToBeDisplayed(this.userInfo.horses);

        fragmentManager.replace(R.id.horseDetailNotes, fragment).commit();

        final TextView tvTitle = (TextView) findViewById(R.id.horse_only_note_title);
        TextView tvText = (TextView) findViewById(R.id.horse_only_note_content);

        StringBuilder stringBuilder = new StringBuilder(horse.notes);
        if (stringBuilder.length() >= 50) {
            stringBuilder.setLength(47);
            stringBuilder.append("...");
        }

        tvTitle.setText(horse.name+"'s General Notes");
        tvText.setText(stringBuilder.toString());

        CardView cv = (CardView) findViewById(R.id.horse_only_note);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("title", tvTitle.getText().toString());
                intent.putExtra("note", horse.notes);
                intent.putExtra("horseID", horseID);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onResume() {
        this.horse = this.userInfo.horses.get(horseID);
        TextView tvText = (TextView) findViewById(R.id.horse_only_note_content);

        StringBuilder stringBuilder = new StringBuilder(horse.notes);
        if (stringBuilder.length() >= 50) {
            stringBuilder.setLength(47);
            stringBuilder.append("...");
        }

        tvText.setText(stringBuilder.toString());
        super.onResume();
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

    //TODO this is the delete Horse method
    private boolean deleteHorse(int id) {
        RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();
        Horse horse = horseDao.queryForId(id);
        //returns true if deleted 1 row (which should be the case if ID exists)
        //else returns false
        return horseDao.delete(horse) == 1;
    }

    @Override //needed
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Horse item) {

    }
}