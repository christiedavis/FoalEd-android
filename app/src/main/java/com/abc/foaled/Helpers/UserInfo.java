package com.abc.foaled.Helpers;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Models.Birth;
import com.abc.foaled.Models.Horse;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by christie on 17/02/17.
 *
 */
public class UserInfo {

    private static UserInfo ourInstance;
    private static DatabaseHelper databaseHelper;

    public static UserInfo getInstance(DatabaseHelper helper) {
        if (ourInstance == null)
            ourInstance = new UserInfo();
        if (databaseHelper == null)
            databaseHelper = helper;
        return ourInstance;
    }

    public static UserInfo getInstance() {
        if (ourInstance == null)
            ourInstance = new UserInfo();
        return ourInstance;
    }

    public List<Horse> horses;

    public List<Horse> getHorses() {
            return this.horses;
    }

    public Horse getHorseAtIndex(int index) {
        return this.horses.get(index);
    }

    private UserInfo() { // intiatializer

    }

    public List<Birth> getBirths() {
        return databaseHelper.getBirthsDataDao().queryForAll();
    }

    public List<Birth> getBirthsByHorseID(int id) throws SQLException {
        return databaseHelper.getBirthsDataDao().queryBuilder().where().eq("mare", id).query();
    }

    public DatabaseHelper getHelper() {
        return databaseHelper;
    }


}
