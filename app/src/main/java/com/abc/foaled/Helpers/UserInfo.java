package com.abc.foaled.Helpers;

import android.content.Context;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Models.Birth;
import com.abc.foaled.Models.Horse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by christie on 17/02/17.
 *
 */
public class UserInfo {

    private static DatabaseHelper databaseHelper;
    private static UserInfo ourInstance;

    public static UserInfo getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new UserInfo();
        if (databaseHelper == null)
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        return ourInstance;
    }

    public List<Horse> horses;
    public List<Birth> births;

    public List<Horse> getHorses() {
        if (this.horses == null) {
            this.horses = new LinkedList<>();
        }
        this.horses = databaseHelper.refreshHorseList();
        return this.horses;
    }

    public List<Birth> getBirths() {
        if (this.births == null) {
            this.births = new LinkedList<>();
        }
        this.births = databaseHelper.refreshBirthList();
        return this.births;
    }

    public Horse getHorseByID(int id) {
        return databaseHelper.getHorseDataDao().queryForId(id);
    }

    public Horse getHorseAtIndex(int index) {
        return this.horses.get(index);
    }

    public Map<String, String> getBirthNotesForHorse(int horseID) {
        this.births = databaseHelper.getBirthsForHorse(horseID);

        Map<String, String> notesMap = new HashMap<>();
        for (Birth b : this.births) {
            notesMap.put(b.getYearOfBirth(), b.notes);
        }
        return notesMap;
    }

    public List<Birth> getBirthsByHorseID(int id) throws SQLException {
        return databaseHelper.getBirthsDataDao().queryBuilder().where().eq("mare", id).query();
    }

    public DatabaseHelper getHelper() {
        return databaseHelper;
    }

    public void release() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
