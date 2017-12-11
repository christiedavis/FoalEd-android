package com.abc.foaled.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.abc.foaled.R;

import net.hockeyapp.android.FeedbackManager;

public class FeedbackActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		Toolbar myToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(myToolbar);
		if (getSupportActionBar() != null)
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		FeedbackManager.register(this);
		final View feedbackView = findViewById(R.id.feedback_view);

		Button feedbackButton = findViewById(R.id.feedback_button);
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
