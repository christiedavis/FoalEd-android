package com.abc.foaled.Models;

import android.app.Activity;
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
    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    public Birth currentBirth;
    @DatabaseField
    private boolean sex;                       //SEX true - gal
    @DatabaseField
    private String markings;
    @DatabaseField
    public String notes;
    @DatabaseField (unknownEnumName = "HORSE_STATUS_DORMANT")
    private HORSE_STATUS status;
//    @DatabaseField
//    private boolean favourite;
    @DatabaseField
    public String smallImagePath;

    @DatabaseField
    public String bigImagePath;

    private Bitmap image;
    private List<Birth> births;

    public Horse() {
        this.name = null;
        this.currentBirth = null;
        this.markings = null;
        this.notes = null;
        this.status = null;
        this.sex = false;
/*        this.smallImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + "/FoalEd/Small_Versions/placeholder.jpg";;
        this.bigImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + "/FoalEd/placeholder.jpg";
        this.image = BitmapFactory.decodeFile(bigImagePath);*/
    }
    public Horse(String name) {
        // this constructer is used only for father horses

        this.name = name;
    }
    public Horse(String name, Birth birth, String markings, String notes, boolean sex) {
        this.name = name;
        this.currentBirth = birth;
        this.markings = markings;
        this.notes = notes;
        this.status = HORSE_STATUS.HORSE_STATUS_DORMANT;
        this.sex = sex;
    }

    public int getAge(){
        return DateTimeHelper.getCurrentAge(this.currentBirth.birth_time);
    }
    public int getHorseID() {
        return this.horseID;
    }
    public String getSex() {
        if (this.sex == true) {
            return "Female";
        }
        else
            return "Male";
    }
    public String getStatusString() {
        return this.status.getString();
    }

    public Map<String, String> getBirthNotes() {
        return UserInfo.getInstance().getBirthNotesForHorse(this.horseID);

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
/*        bigImagePath = bigImage;
        smallImagePath = smallImage;
        image = BitmapFactory.decodeFile(smallImagePath);*/
    }

    public void setImagePath(String filePath) {
        bigImagePath = filePath;
        smallImagePath = bigImagePath;
        image = BitmapFactory.decodeFile(smallImagePath);
    }
}