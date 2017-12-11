package com.abc.foaled.activities;

import android.graphics.Bitmap;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SET UP GENERAL
        Bitmap image = ImageHelper.bitmapSmaller(getResources(), R.drawable.pic_ash, 250, 250);
        setContentView(R.layout.activity_about);
        ImageView iV = findViewById(R.id.about_circle_image_view);
        iV.setImageBitmap(image);

        //setup us
        Bitmap christieBitmap = ImageHelper.bitmapSmaller(getResources(), R.drawable.pic_christie, 250, 250);
        ImageView christieImageView = findViewById(R.id.devCircleImageView1);
        christieImageView.setImageBitmap(christieBitmap);

        Bitmap brendanBitmap = ImageHelper.bitmapSmaller(getResources(), R.drawable.pic_brendan, 250, 250);
        ImageView brendanImageView = findViewById(R.id.devCircleImageView2);
        brendanImageView.setImageBitmap(brendanBitmap);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
	    if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
