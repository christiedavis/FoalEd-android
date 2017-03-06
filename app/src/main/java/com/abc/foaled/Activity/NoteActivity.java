package com.abc.foaled.Activity;

import android.os.Bundle;
import android.widget.EditText;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.R;

/**
 * Created by Brendan on 24/02/2017.
 *
 */

public class NoteActivity extends ORMBaseActivity<DatabaseHelper> {
    EditText noteTitle;
    EditText noteContent;

    String title;
    String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = getIntent().getStringExtra("title");
        note = getIntent().getStringExtra("note");
//        noteTitle = (EditText) findViewById(R.id.note_activity_title);
//        noteContent = (EditText) findViewById(R.id.note_activity_content);
    }

    @Override
    public void onResume() {
        super.onResume();

        noteTitle = (EditText) findViewById(R.id.note_activity_title);
        noteContent = (EditText) findViewById(R.id.note_activity_content);

        noteTitle.setText(title);
        noteContent.setText(note);
    }
}
