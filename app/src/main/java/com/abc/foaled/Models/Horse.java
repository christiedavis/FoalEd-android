package com.abc.foaled.Models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;


import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Helpers.DateTimeHelper;
import com.abc.foaled.Helpers.UserInfo;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.Period;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Brendan on 29/12/16.
 *
 */
@DatabaseTable(tableName = "horse")
public class Horse implements Serializable {

    public enum HORSE_STATUS {

        HORSE_STATUS_DORMANT(0),
        HORSE_STATUS_MAIDEN(1),
        HORSE_STATUS_PREGNANT(2),
        HORSE_STATUS_FOAL(3),
        HORSE_STATUS_RETIRED(4);

        private final int value;

        HORSE_STATUS(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }

        public String getString() {
            switch (this) {
                case HORSE_STATUS_DORMANT :
                    return "Dormant";

                case HORSE_STATUS_MAIDEN:
                    return "Maiden Pregnancy";

                case HORSE_STATUS_PREGNANT:
                    return "Pregnant";

                case HORSE_STATUS_FOAL:
                    return "With foal";

                case HORSE_STATUS_RETIRED:
                    return "Retired";

                default:
                    return "Dormant";
            }
        }
    }

    @DatabaseField(generatedId = true)
    private int horseID;                             //ID
    @DatabaseField
    public String name;                        //NAME
    @DatabaseField (foreign = true, canBeNull = false, foreignAutoRefresh = true)
    public Birth dateOfBirth;
    @DatabaseField (foreign = true, canBeNull = true, foreignAutoRefresh = true)
    public Birth currentBirth;
    @DatabaseField
    private boolean sex;                       //SEX true - gal
    @DatabaseField
    public String notes;
    @DatabaseField(unknownEnumName = "HORSE_STATUS_DORMANT")
    private HORSE_STATUS status;
    @DatabaseField
    private boolean favourite;
    //TODO remove one of these and just use the default one. Probably small?
    @DatabaseField
    public String smallImagePath;
    @DatabaseField
    public String bigImagePath;

    private Bitmap image;
    private List<Birth> births;
    private boolean isMaiden = true;

    public boolean isMaiden() {
        return isMaiden;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public Horse() {
        this.name = null;
        this.dateOfBirth = null;
        this.currentBirth = null;
        this.notes = null;
        this.status = HORSE_STATUS.HORSE_STATUS_DORMANT;
        this.sex = false;
        this.favourite = false;
    }
    public Horse(String name) {
        // this constructer is used only for father horses - is retired always

        this.name = name;
        this.status = HORSE_STATUS.HORSE_STATUS_RETIRED;
        this.favourite = false;
    }
    public Horse(String name, Birth birth, String notes, boolean sex) {
        this.name = name;
        this.dateOfBirth = birth;
        this.currentBirth = null;
        this.notes = notes;
        this.status = HORSE_STATUS.HORSE_STATUS_DORMANT;
        this.sex = sex;
        this.favourite = false;
    }

    public int getAge(){
        return DateTimeHelper.getCurrentAge(this.dateOfBirth.birth_time);
    }

    public int getHorseID() {
        return this.horseID;
    }

    public String getSex() {
        return this.sex ? "Female" : "Male";
    }

    public HORSE_STATUS getStatus() {
        return this.status;
    }

    public void setStatus(HORSE_STATUS status) {
        //perform nessacary checks

        // if first pregnanancy - maiden else pregnant

        if (status == HORSE_STATUS.HORSE_STATUS_PREGNANT && isMaiden) {
            isMaiden = false;
            this.status = HORSE_STATUS.HORSE_STATUS_MAIDEN;
            return;
        } else if ((this.status == HORSE_STATUS.HORSE_STATUS_PREGNANT || this.getStatus() == HORSE_STATUS.HORSE_STATUS_MAIDEN) && status == HORSE_STATUS.HORSE_STATUS_DORMANT) {
            // if from pregnant -> dormant  - remove current pregnancy
            this.currentBirth = null;
        }
        this.status = status;
    }

    public String getStatusString() {
        return this.status.getString();
    }

    public void setFavourite(boolean fav) {
        favourite = fav;
    }

    public Map<String, List<String>> getBirthNotes(Context c) {
        UserInfo userInfo = UserInfo.getInstance(c);
        return userInfo.getBirthNotesForHorse(this.horseID);

    }

    public void updateBirth(Context c, String birthId, String note) {
        UserInfo.getInstance(c).updateBirth(this.horseID, birthId, note);
    }

    /**
     * @return A string representation of the row in the database
     * TODO build this to be proper again - brendan - is this still being used?
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(horseID);
        sb.append(", ").append("name=").append(name);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        sb.append(", ").append("sex=").append(sex);
        sb.append(", ").append("photo=").append(smallImagePath+"");
        return sb.toString();
    }

    //TODO return method for age (done through birth dob field)


    public void addMilestones() {
        // add new milestone
        Milestone milestone1 = new Milestone(Milestone.MILESTONE.MILESTONE_POOP);
    }

//// Mark - Image helper methods

    /**
     * @param getSmall boolean indicating whether to select the small version, or the big one. True means small
     * @return Returns the relevant bitmap image
     */
    public Bitmap getImage(boolean getSmall) {
        return getSmall ? image : BitmapFactory.decodeFile(bigImagePath);
    }

    /**
     * This sets the image paths. The big image path is already created and the image is already there.
     * But then I need to create the small image file, and compress big image to smaller size
     * @param a The current absolute path of the now existing photo (fullsize).
     */
    //TODO this could just be bigImagePath, and then create the small image file here instead?
    public void setImagePath(Activity a) {
        this.smallImagePath = a.getFilesDir().getAbsolutePath() + "/placeholder.jpg";
        this.bigImagePath = a.getFilesDir().getAbsolutePath() + "/placeholder.jpg";
        this.image = BitmapFactory.decodeFile(bigImagePath);
    }

    public void setImagePath(String filePath) {
        bigImagePath = filePath;
        smallImagePath = bigImagePath;
        image = BitmapFactory.decodeFile(smallImagePath);
    }
}