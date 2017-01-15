package com.abc.foaled;

/**
 * Created by Brendan on 15/01/2017.
 *
 */

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.abc.foaled.DatabaseDescription.ChildrenTable;
import com.abc.foaled.DatabaseDescription.HorseTable;

public class DatabaseManager {

    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null==instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseHelper helper;
    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    public List<HorseTable> getAllHorses() {
        List<HorseTable> Horses = null;
        try {
            Horses = getHelper().getHorseDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Horses;
    }
}