package com.abc.foaled.Models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;


import com.abc.foaled.Helpers.DateTimeHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.Period;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.R.attr.id;

/**
 * Created by Brendan on 29/12/16.
 *
 */
@DatabaseTable(tableName = "horse")
public class Horse implements Serializable {

    private enum HORSE_STATUS {

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
    }

    @DatabaseField(generatedId = true)
    private int horseID;                             //ID
    @DatabaseField
    public String name;                        //NAME
    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    public Births birth;
    @DatabaseField
    private boolean sex;                       //SEX
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

    public Horse() {
        this.name = null;
        this.birth = null;
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

    // TODO: is this when this horse was born?
    // TODO yes
    public Period getAge(){
        return DateTimeHelper.getCurrentAge(this.birth.birth_time);
    }
    /**
     *
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

    /**
     * @return A string representation of the row in the database
     * TODO build this to be proper again
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

    public int getHorseID() {
        return this.horseID;
    }

}