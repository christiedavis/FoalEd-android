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

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.System.in;

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

    public List<Horse> getFavouriteHorses() {
        //TODO: return only favourited horses
        if (this.horses == null) {
            this.horses = new LinkedList<>();
        }
        this.horses = databaseHelper.refreshHorseList();

        ArrayList favouriteHorses = new ArrayList<Horse>();
        for (int i =0 ; i < this.horses.size(); i ++)
            if (this.horses.get(i).isFavourite()) {
                favouriteHorses.add(this.horses.get(i));
        }
        return favouriteHorses;
    }

    public void favourite(int index) {
        horses.get(index).setFavourite(!horses.get(index).isFavourite());
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

    public void updateHorse(Horse horse) {
        databaseHelper.updateHorse(horse);
    }

    public Horse getHorseAtIndex(int index) {
        return this.horses.get(index);
    }

    public Map<String, List<String>> getBirthNotesForHorse(int horseID) {
        this.births = databaseHelper.getBirthsForHorse(horseID);

        Map<String, List<String>> notesMap = new HashMap<>();
        for (Birth b : this.births) {
            //TODO Chrisite, should this not just be a String and not a list of strings? I.e 'list' always has only one String object
            List<String> notes = new ArrayList<>();
            notes.add(b.notes);
            notesMap.put(b.getYearOfBirth(), notes);
        }
        return notesMap;
    }

    public void updateBirth(int horseId, String birthId, String note){
        databaseHelper.updateBirth(horseId, birthId, note);
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
