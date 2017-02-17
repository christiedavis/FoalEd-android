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

import com.abc.foaled.Helpers.DateTimeHelper;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;

import java.io.File;

public class HorseDetailActivity extends AppCompatActivity {

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
}