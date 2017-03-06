package com.abc.foaled.Helpers;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Models.Births;
import com.abc.foaled.Models.Horse;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.joda.time.Period;

import java.util.List;

/**
 * Created by christie on 17/02/17.
 *
 */
public class UserInfo {

    private static UserInfo ourInstance = new UserInfo();

    public static UserInfo getInstance() {
        return ourInstance;
    }

    public List<Horse> horses;

    public List<Horse> getHorses() {
            return this.horses;
    }

    public Horse getHorseAtIndex(int index) {
        return this.horses.get(index);
    }

//    private void refresh() {
//        this.horses = getHelper().getHorseDataDao().queryForAll();
//    }
//    public void addNewHorse(Births birth, Horse horse){
//        birthDao.create(birth);
//        horseDao.create(horse);
//
//        this.refresh();
//    }

    private UserInfo() { // intiatializer

//        this.birthDao = getHelper().getBirthsDataDao();
//        this.horseDao = getHelper().getHorseDataDao();

    }


}
