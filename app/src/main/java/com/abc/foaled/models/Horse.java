package com.abc.foaled.models;

import android.content.Context;
import android.util.Log;

import com.abc.foaled.helpers.DateTimeHelper;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;

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
    @DatabaseField(canBeNull = false)
    private String name;                        //NAME
    @DatabaseField (foreign = true, canBeNull = false, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Birth dateOfBirth;
    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Birth currentBirth;
    @DatabaseField(canBeNull = false)
    private boolean sex;                       //SEX true - gal
    @DatabaseField(canBeNull = false)
    private String notes;
    @DatabaseField(unknownEnumName = "DORMANT", canBeNull = false)
    private HORSE_STATUS status;
    @DatabaseField(canBeNull = false)
    private boolean favourite = false;
    @DatabaseField(canBeNull = false)
    private String imagePath;

    @ForeignCollectionField(columnName = "milestones")
    private ForeignCollection<Milestone> milestones;

	@ForeignCollectionField(columnName = "births")
	private ForeignCollection<Birth> pastBirths;



    public Horse() {
    }

    public Horse(String name, Birth dob, boolean sex, String notes, HORSE_STATUS status, String imagePath) {
		this.name = name;
	    this.dateOfBirth = dob;
	    this.sex = sex;
	    this.notes = notes != null ? notes : "";
	    this.status = status != null ? status : HORSE_STATUS.DORMANT;
	    this.imagePath = imagePath != null ? imagePath : "";
    }


    public int getHorseID() {
        return horseID;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Birth getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Birth birth) {
		dateOfBirth = birth;
	}

	public Birth getCurrentBirth() {
		return currentBirth;
	}

	public void setCurrentBirth(Birth currentBirth) {
		this.currentBirth = currentBirth;
	}

	public boolean isFemale() {
	    return sex;
    }

    public void setFemale(boolean sex) {
	    this.sex = sex;
    }

	public String getNotes() {
		return notes;
	}

	public void setNotes(String s) {
		notes = s;
	}

	public HORSE_STATUS getStatus() {
		return this.status;
	}

	public void setStatus(HORSE_STATUS newStatus) {
		//perform necassary checks

		// if first pregnancy - maiden else pregnant
		if (newStatus == HORSE_STATUS.PREGNANT && pastBirths.isEmpty()) {
			status = HORSE_STATUS.MAIDEN;
			return;
		} else if ((status == HORSE_STATUS.PREGNANT || status == HORSE_STATUS.MAIDEN) && newStatus == HORSE_STATUS.DORMANT) {
			// if from pregnant -> dormant  - remove current pregnancy
			pastBirths.add(currentBirth);
			this.currentBirth = null;
		}
		else if (newStatus == HORSE_STATUS.FOAL) {
			createMilestones();
		}
		this.status = newStatus;
	}


	public boolean isFavourite() {
		return favourite;
	}

	public void toggleFavourite() {
		favourite = !favourite;
	}

	public int getAge(){
		return DateTimeHelper.getCurrentAge(dateOfBirth.getBirthTime());
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public ForeignCollection<Birth> getBirths() {
	    return pastBirths;
    }

    public String getStatusString() {
        return this.status.getString();
    }

    public void createMilestones() {
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
}