package com.abc.foaled.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.abc.foaled.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class faqActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq);

		Toolbar myToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(myToolbar);
		if (getSupportActionBar() != null)
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ExpandableTextView expTv1 = findViewById(R.id.faq_q1);
		expTv1.setText(getString(R.string.FAQ_Q1_Title) + "\n" + getString(R.string.FAQ_Q1_Message));
		ExpandableTextView expTv2 = findViewById(R.id.faq_q2);
		expTv2.setText(getString(R.string.FAQ_Q2_Title) + "\n" + getString(R.string.FAQ_Q2_Message));
		ExpandableTextView expTv3 = findViewById(R.id.faq_q3);
		expTv3.setText(getString(R.string.FAQ_Q3_Title) + "\n" + getString(R.string.FAQ_Q3_Message));
		ExpandableTextView expTv4 = findViewById(R.id.faq_q4);
		expTv4.setText(getString(R.string.FAQ_Q4_Title) + "\n" + getString(R.string.FAQ_Q4_Message));
		ExpandableTextView expTv5 = findViewById(R.id.faq_q5);
		expTv5.setText(getString(R.string.FAQ_Q5_Title) + "\n" + getString(R.string.FAQ_Q5_Message));
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
