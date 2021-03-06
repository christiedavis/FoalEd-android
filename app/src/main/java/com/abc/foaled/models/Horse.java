package com.abc.foaled.models;

import android.content.Context;

import com.abc.foaled.helpers.DateTimeHelper;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Brendan on 29/12/16.
 *
 */
@DatabaseTable(tableName = "horse")
public class Horse implements Serializable {

	public static final String HORSE_ID = "HorseID";

    public String getDam() {
        return dam;
    }

    public String getColour() {
        return colour;
    }

    public void update(String name, String breed, String dam, String colour, String imagePath) {
        this.name = name;
        this.breed = breed;
        this.dam = dam;
        this.colour = colour;
        this.imagePath = imagePath != null ? imagePath : "";
    }

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

                case PREGNANT:
                    return "Pregnant";

                case FOAL:
                    return "Foal";

                default:
                    return "Dormant";
            }
        }

    }
    @DatabaseField(generatedId = true)
    private int horseID;                             //ID

    @DatabaseField(canBeNull = false)
    private String name;                        //NAME

    @DatabaseField(canBeNull = true)
    private String breed;

    @DatabaseField(canBeNull = true)
    private String dam;

    @DatabaseField(canBeNull = true)
    private String sire;

    @DatabaseField(canBeNull = true)
    private String colour;

    @DatabaseField (foreign = true, canBeNull = false, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Birth dateOfBirth;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Birth currentBirth;

    @DatabaseField(canBeNull = false)
    private boolean sex;                       //SEX true - girl

    @DatabaseField(canBeNull = false)
    private String notes;

    @DatabaseField(unknownEnumName = "DORMANT", canBeNull = false)
    private HORSE_STATUS status;

    @DatabaseField(canBeNull = false)
    private boolean favourite = false;

    @DatabaseField(canBeNull = false)
    private String imagePath;

    @ForeignCollectionField(eager = true, columnName = "milestones")
    private Collection<Milestone> milestones;

    private List<Milestone> milestoneList;
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

    public String getBreed() {
        return breed;
    }

	public Birth getCurrentBirth() {
		return currentBirth;
	}

	public void setCurrentBirth(Birth currentBirth) {
		this.currentBirth = currentBirth;
		this.status = HORSE_STATUS.PREGNANT;
	}

	public boolean isFemale() {
	    return sex;
    }

    public void setFemale(boolean sex) {
	    this.sex = sex;
    }

    public boolean isPregnant() {
		return currentBirth != null;
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
		this.status = newStatus;
	}

	public boolean isFavourite() {
		return favourite;
	}

	public void toggleFavourite() {
		favourite = !favourite;
	}

	public String getAge(){
		return DateTimeHelper.getCurrentAgeString(dateOfBirth.getBirthTime());
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
        return status.getString();
    }

    public void setExtraDetails(String breed, String dam, String sire, String colour){

        this.breed = breed;
        this.dam = dam;
        this.sire = sire;
        this.colour = colour;
    }

    public List<Milestone> getMilestones() {
        if (milestones.size() == 0)
            return null;

        if (milestoneList == null) {
            milestoneList = new ArrayList<>();
            for (Milestone milestone : milestones) {
                milestoneList.add(milestone);
            }
        }
        return milestoneList;
    }

    public Milestone completeMilestone(int milestoneID) {
	    for (Milestone m : milestones) {
		    if (m.getID() == milestoneID) {
			    m.setCompleted(true);
			    return m;
		    }
	    }
	    return null;
    }

	/**
	 * Should only get called if the horse is a Foal
	 */
	public void createMilestones(Context context) {
		milestones.add(new Milestone(0, this, context));
		milestones.add(new Milestone(1, this, context));
		milestones.add(new Milestone(2, this, context));
		milestones.add(new Milestone(3, this, context));
    }

}