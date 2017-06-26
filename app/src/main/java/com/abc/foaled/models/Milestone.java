package com.abc.foaled.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

/**
 * Created by Brendan on 7/02/2017.
 *
 */

@DatabaseTable(tableName = "milestone")
public class Milestone {

    public enum MILESTONE{

        POOP(0), //2 hours from birth
        EAT(1), //4 hours from birth
        STAND(2), //1 hour from birth
        DRINK(3); //30 minutes from birth

        private final int value;

        MILESTONE(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }
    //Variables
    @DatabaseField(generatedId = true)
    private int milestoneID;
    @DatabaseField(unknownEnumName = "POOP")
    private MILESTONE milestone;
    @DatabaseField
    private DateTime startTime;
	@DatabaseField
	private DateTime emergencyTime; //if this milestone reaches this emergency time before being completed.. seek help
    @DatabaseField
    private double snoozeTime;
    @DatabaseField
    private String message;
    @DatabaseField
    private String detail;
    @DatabaseField
    private String notificationMessage;
    @DatabaseField
    private Boolean completed = false;

    @DatabaseField(foreign = true)
    private Horse h;

	//Required empty constructor
    public Milestone() {

    }

    Milestone(int value, Horse horse) {
        this.h = horse;
        //constructor stuff here
        this.milestone = MILESTONE.values() [value];
        milestoneID = milestone.getValue();

	    DateTime birthTime = horse.getDateOfBirth().getBirthTime();

        switch (this.milestone) {
            case POOP:

				startTime = birthTime.plusHours(2);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5; // 5 minutes
                message = "Your horse should have pooped by now";
                detail = "It's important your horse poos so that it can empty itself. You might need to give them a laxative.";
                notificationMessage = "Has your foal pooed?";
                break;

            case EAT:
                startTime = birthTime.plusHours(4);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have eaten by now";
                detail = "It's important your horse eats";
                notificationMessage = "Has your foal eaten?";
                break;

            case STAND:
                startTime = birthTime.plusHours(1);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have stood by now";
                detail = "It's important your horse stands so that his legs work";
                notificationMessage = "Has your horse stood?";
                break;

            case DRINK:
                startTime = birthTime.plusMinutes(30);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have drunk by now";
                detail = "It's important your horse drinks.";
                notificationMessage = "Has your foal drunk yet?";
                break;
        }
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public Boolean isCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
}
