package com.abc.foaled.Activity;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
        this.horse = this.userInfo.getHorseAtIndex(horseID);

        Button horseName = (Button)this.findViewById(R.id.buttonAge);
        horseName.setText(DateTimeHelper.printPeriod(horse.getAge()));

        TextView age = (TextView)this.findViewById(R.id.horse_age);
        age.setText(DateTimeHelper.printPeriod(horse.getAge()));

        TextView gender = (TextView)this.findViewById(R.id.buttonSex);
        gender.setText(horse.getSex());

        TextView status = (TextView)this.findViewById(R.id.buttonStatus);
        status.setText(horse.getStatusString());

        ImageView personPhoto = (ImageView)this.findViewById(R.id.horse_photo);
        personPhoto.setImageURI(Uri.fromFile(new File(horse.smallImagePath)));

//        QueryBuilder<Births, Integer> queryBuilder = getHelper().getBirthsDataDao().queryBuilder();
//        queryBuilder.where().eq(Births.mare, "qwerty");
//        String[] birthNotes = getHelper().getBirthsDataDao().query

        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        HorseNoteFragment fragment = HorseNoteFragment.newInstance();
        //fragment.setListToBeDisplayed(this.userInfo.horses);

        fragmentManager.replace(R.id.horseDetailNotes, fragment).commit();

/*        StringBuilder note = new StringBuilder(horse.notes);
        String notes = note.substring(0, 47) + " ...";
        TextView tvTitle = (TextView) findViewById(R.id.horse_note_card_view_title);
        TextView tvText = (TextView) findViewById(R.id.horse_note_card_view_note);
        tvTitle.setText(horse.name + "'s notes");*/
//        tvText.setText


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