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

    private static UserInfo ourInstance;

    public static UserInfo getInstance() {
        if (ourInstance == null) {
            ourInstance = new UserInfo();
        }
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


}
