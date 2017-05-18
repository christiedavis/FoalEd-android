package com.abc.foaled.Models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.abc.foaled.Helpers.DateTimeHelper;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Brendan on 29/12/16.
 *
 */
@DatabaseTable(tableName = "horse")
public class Horse implements Serializable {

    public enum HORSE_STATUS {

        DORMANT(0),
        MAIDEN(1),
        PREGNANT(2),
        FOAL(3),
        RETIRED(4);

        private final int value;

        HORSE_STATUS(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }

        public String getString() {
            switch (this) {
                case DORMANT :
                    return "Dormant";

                case MAIDEN:
                    return "Maiden Pregnancy";

                case PREGNANT:
                    return "Pregnant";

                case FOAL:
                    return "Recently born";

                case RETIRED:
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
    @DatabaseField(unknownEnumName = "DORMANT")
    private HORSE_STATUS status;
    @DatabaseField
    private boolean favourite;
    //TODO remove one of these and just use the default one. Probably small? - sounds good (Brendan)
    @DatabaseField
    public String smallImagePath;
    @DatabaseField
    public String bigImagePath;

    @ForeignCollectionField(columnName = "milestones")
    ForeignCollection<Milestone> milestones;

	@ForeignCollectionField(columnName = "births")
	ForeignCollection<Birth> births;

    private Bitmap image;


    public boolean isFavourite() {
        return favourite;
    }

    public Horse() {
        this.name = null;
        this.dateOfBirth = null;
        this.currentBirth = null;
        this.notes = null;
        this.status = HORSE_STATUS.DORMANT;
        this.sex = false;
        this.favourite = false;
    }
    public Horse(String name) {
        // this constructor is used only for father horses - is retired always

        this.name = name;
        this.status = HORSE_STATUS.RETIRED;
        this.favourite = false;
    }
    public Horse(String name, Birth birth, String notes, boolean sex) {

        this.name = name;
        this.dateOfBirth = birth;
        this.currentBirth = null;
        this.notes = notes;
        this.status = HORSE_STATUS.DORMANT;
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

    public void setStatus(HORSE_STATUS newStatus, Context c) {
        //perform necassary checks

        // if first pregnancy - maiden else pregnant
        if (newStatus == HORSE_STATUS.PREGNANT && births.isEmpty()) {
            this.status = HORSE_STATUS.MAIDEN;
            return;
        } else if ((this.status == HORSE_STATUS.PREGNANT || this.getStatus() == HORSE_STATUS.MAIDEN) && newStatus == HORSE_STATUS.DORMANT) {
            // if from pregnant -> dormant  - remove current pregnancy
            this.currentBirth = null;
        }
        else if (newStatus == HORSE_STATUS.FOAL) {
            createMilestones();
        }
        this.status = newStatus;
    }

    public ForeignCollection<Birth> getBirths() {
	    return births;
    }

    public String getStatusString() {
        return this.status.getString();
    }

    public void setFavourite(boolean fav) {
        favourite = fav;
    }


    //TODO return method for age (done through birth dob field)


    public void createMilestones() {
//        milestones = new ArrayList<Milestone>();
        milestones.add(new Milestone(0, this));
        milestones.add(new Milestone(1, this));
        milestones.add(new Milestone(2, this));
        milestones.add(new Milestone(3, this));
        try {
            milestones.getDao().create(milestones);
        } catch (SQLException e) {
            Log.e("DATABASE", "Trouble creating initial milestones");
            e.printStackTrace();
        }
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

	public String getName() {
		return name;
	}

	public String getNotes() {
		return notes;
	}
}