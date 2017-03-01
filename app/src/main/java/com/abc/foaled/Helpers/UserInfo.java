package com.abc.foaled.Helpers;

import com.abc.foaled.Models.Horse;

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

    //TODO make this private
    public List<Horse> horses;

     public List<Horse> getHorses() {
            return this.horses;
    }

    private UserInfo() {

    }


}
