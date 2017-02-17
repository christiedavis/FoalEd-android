package com.abc.foaled.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Models.Births;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

public class HorseDetailActivity extends ORMBaseActivity<DatabaseHelper> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horse_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();
        Horse current = horseDao.queryForId(intent.getIntExtra("nameText", 1));
        ((ImageView) findViewById(R.id.person_photo)).setImageURI(Uri.fromFile(new File(current.smallImagePath)));

        DateTime birthDate = current.birth.birth_time;
        DateTime currentDate = new DateTime();
        Period difference = new Period(birthDate, currentDate);
        PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
                .appendYears()
                .appendSuffix(" year", " years")
                .appendSeparator(" - ")
                .appendMonths()
                .appendSuffix(" month", " months")
                .appendSeparator(" - ")
                .appendWeeks()
                .appendSuffix(" week", " weeks")
                .appendSeparator(" - ")
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparator(" - ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .toFormatter();

        ((Button) findViewById(R.id.buttonAge)).setText(daysHoursMinutes.print(difference));

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

    private String differenceInDates(Date startDate, Date endDate) {

        //Period difference
//        return formatter.format("$1");
        return "";
    }
}
