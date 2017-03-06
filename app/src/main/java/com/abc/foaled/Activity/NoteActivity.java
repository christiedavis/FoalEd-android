package com.abc.foaled.Activity;

import android.os.Bundle;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Horse;

/**
 * Created by Brendan on 24/02/2017.
 *
 */

public class NoteActivity extends ORMBaseActivity<DatabaseHelper> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int horseID = getIntent().getIntExtra("HorseID", 0);
        if (horseID == 0)
            throw new IllegalArgumentException("No Horse ID was passed to this activity");
        Horse horse = UserInfo.getInstance().getHorseAtIndex(horseID);

    }
}
