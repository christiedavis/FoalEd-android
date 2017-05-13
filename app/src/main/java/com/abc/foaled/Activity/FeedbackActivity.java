package com.abc.foaled.Activity;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.abc.foaled.MainActivity;
import com.abc.foaled.R;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import net.hockeyapp.android.FeedbackManager;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FeedbackManager.register(this);
        final View feedbackView = (View) findViewById(R.id.feedback_view);

        Button feedbackButton = (Button) findViewById(R.id.feedback_button);
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackManager.showFeedbackActivity(feedbackView.getContext());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FeedbackManager.unregister();
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
