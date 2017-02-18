package com.abc.foaled.Activity;

import android.net.Uri;
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
import com.abc.foaled.Helpers.DateTimeHelper;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.File;

public class HorseDetailActivity extends ORMBaseActivity<DatabaseHelper> {

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
        Button horseName = (Button)this.findViewById(R.id.buttonAge);
        horseName.setText(horse.name);

        TextView age = (TextView)this.findViewById(R.id.horse_age);
        age.setText(DateTimeHelper.printPeriod(horse.getAge()));


        ImageView personPhoto = (ImageView)this.findViewById(R.id.horse_photo);
        personPhoto.setImageURI(Uri.fromFile(new File(horse.smallImagePath)));

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
}