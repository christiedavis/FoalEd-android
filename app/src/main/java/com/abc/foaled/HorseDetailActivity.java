package com.abc.foaled;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HorseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horse_detail);
        //passed details
        Bundle bundle = getIntent().getExtras();
        TextView tv = (TextView) this.findViewById(R.id.nameText);
        tv.setText(bundle.getString("nameText"));
    }
}
