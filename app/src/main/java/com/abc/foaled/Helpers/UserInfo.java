package com.abc.foaled.Helpers;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Models.Birth;
import com.abc.foaled.Models.Horse;

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

//} extends ORMBaseActivity<DatabaseHelper> {

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
    public List<Birth> births;

    public List<Horse> getHorses() {
        if (this.horses == null) {
            this.horses = new LinkedList<>();
        }
        this.horses = getHelper().refreshHorseList();
        return this.horses;
    }

    public List<Birth> getBirths() {
        if (this.births == null) {
            this.births = new LinkedList<>();
        }
//        this.births = getHelper().refreshBirthList();
        return this.births;
    }

    public Horse getHorseAtIndex(int index) {
        return this.horses.get(index);
    }
    public Map<String, String> getBirthNotesForHorse(int horseID) {
//        this.births = getHelper().getBirthsForHorse(horseID);

        Map<String, String> notesMap = new HashMap<String, String>();
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


}
