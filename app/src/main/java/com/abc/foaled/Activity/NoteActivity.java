package com.abc.foaled.Activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;

/**
 * Created by Brendan on 24/02/2017.
 *
 */

public class NoteActivity extends ORMBaseActivity<DatabaseHelper> {
    TextView noteTitle;
    EditText noteContent;
    UserInfo userInfo;

    String title;
    String note;
    int horseID;
    Horse horse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        userInfo = UserInfo.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_18dp);
        }

        horseID = getIntent().getIntExtra("horseID", 0);
        horse = userInfo.getHorses().get(horseID);
        title = getIntent().getStringExtra("title");
        note = getIntent().getStringExtra("note");

        noteTitle = (TextView) findViewById(R.id.note_activity_title);
        noteContent = (EditText) findViewById(R.id.note_activity_content);
        noteTitle.append(title);
        noteContent.append(note);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_note: {
                changeNote();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeNote() {
        horse.notes = noteContent.getText().toString();
        getHelper().getHorseDataDao().update(horse);
    }


}
