package com.abc.foaled.Activity;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.abc.foaled.R;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;

public class FeedbackActivity extends AppCompatActivity {


    //TODO: This is currently being used as a test screen to play with the notes.
    //The notes need to add ability to scroll up when you click on one below the keyboard
    // Also we need to save the note on button click

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO: this needs to be populated based on the horses previous births + General
        final String[] array = {"General Notes", "2015", "2014", "2013", "Awesome"};

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.ex_layout_row_view, R.id.expandableLayoutHeaderText, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.exlistview);

        expandableLayoutListView.setAdapter(arrayAdapter);

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
