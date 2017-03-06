package com.abc.foaled.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
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
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Births;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.File;

public class HorseDetailActivity extends ORMBaseActivity<DatabaseHelper>
    implements FavouriteHorsesFragment.OnListFragmentInteractionListener, NotificationSettingsFragment.OnFragmentInteractionListener {

    UserInfo userInfo;
    Horse horse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horse_detail);
        this.userInfo = UserInfo.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int horseID = getIntent().getIntExtra("HorseID", 0);
        this.horse = this.userInfo.horses.get(horseID);

        // TODO: set up the rest of the fields.
        //Sets up horse name
        Button horseName = (Button)this.findViewById(R.id.buttonAge);
        horseName.setText(horse.name);
        //sets up horse age
        TextView age = (TextView)this.findViewById(R.id.horse_age);
        age.setText(DateTimeHelper.printPeriod(horse.getAge()));
        //sets up the photo
        ImageView personPhoto = (ImageView)this.findViewById(R.id.horse_photo);
        personPhoto.setImageURI(Uri.fromFile(new File(horse.smallImagePath)));


        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        HorseNoteFragment fragment = HorseNoteFragment.newInstance();

        fragmentManager.replace(R.id.horseDetailNotes, fragment).commit();

/*        StringBuilder note = new StringBuilder(horse.notes);
        String notes = note.substring(0, 47) + " ...";
        TextView tvTitle = (TextView) findViewById(R.id.horse_note_card_view_title);
        TextView tvText = (TextView) findViewById(R.id.horse_note_card_view_note);
        tvTitle.setText(horse.name + "'s notes");*/
//        tvText.setText

        final TextView tvTitle = (TextView) findViewById(R.id.horse_only_note_title);
        final TextView tvText = (TextView) findViewById(R.id.horse_only_note_content);
        tvTitle.setText(horse.name);
        tvText.setText(horse.notes);

        CardView cv = (CardView) findViewById(R.id.horse_only_note);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("title", tvTitle.getText().toString());
                intent.putExtra("note", tvText.getText().toString());
            }
        });


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