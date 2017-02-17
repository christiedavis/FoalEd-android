package com.abc.foaled.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Brendan on 29/12/16.
 *
 */
@DatabaseTable(tableName = "horse")
public class Horse {

    public enum HORSE_STATUS{

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
    private int id;                             //ID
    @DatabaseField
    public String name;                        //NAME
    @DatabaseField(canBeNull = false, foreign = true)
    public Births birth = new Births(this);
    @DatabaseField
    private boolean sex;                       //SEX
    @DatabaseField
    private String markings;
    @DatabaseField
    private String notes;
//    @DatabaseField (unknownEnumName = "HORSE_STATUS_DORMANT")
//    private HORSE_STATUS status;
//    @DatabaseField
//    private boolean favourite;
    @DatabaseField
    public String smallImagePath;

    @DatabaseField
    private String bigImagePath;

    private Bitmap image;


    //TODO need to update database to include photo location OR actual photo
    //public int photo;

    public Horse() {
        this.name = null;
        this.birth = new Births(this);
        this.markings = null;
        this.notes = null;
        this.sex = false;
        this.smallImagePath = null;
        this.bigImagePath = null;
        this.image = null;
    }

    /**
     *
     * @param getSmall boolean indicating whether to select the small version, or the big one. True means small
     * @return Returns the relevant bitmap image
     */
    public Bitmap getImage(boolean getSmall) {
        //TODO should some of these not be initialized with the horse object?
        if (bigImagePath == null) {
            bigImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + "/FoalEd/placeholder.jpg";
            smallImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + "/FoalEd/Small_Versions/placeholder.jpg";
            image = BitmapFactory.decodeFile(smallImagePath);
        }


        //returns image if getSmall is true, else returns full-size bitmap of the big image
        return getSmall ? image : BitmapFactory.decodeFile(bigImagePath);
    }

    /**
     * This sets the image paths. The big image path is already created and the image is already there.
     * But then I need to create the small image file, and compress big image to smaller size
     * @param bigImage The current absolute path of the now existing photo (fullsize).
     * @param smallImage The current absolute path of the now existing photo (compressed).
     */
    public void setImagePath(String bigImage, String smallImage) {
        bigImagePath = bigImage;
        smallImagePath = smallImage;
        image = BitmapFactory.decodeFile(smallImagePath);
    }

    /**
     * @return A string representation of the row in the database
     * TODO build this to be proper again
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
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

}