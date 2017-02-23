package com.abc.foaled.Database;

/**
 * Created by Brendan on 15/01/2017.
 *
 */

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.abc.foaled.Models.Horse;

public class DatabaseManager {

    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (instance==null)
            instance = new DatabaseManager(ctx);
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

    public List<Horse> getAllHorses() {
        List<Horse> Horses = null;
        try {
            Horses = getHelper().getHorseDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Horses;
    }
}