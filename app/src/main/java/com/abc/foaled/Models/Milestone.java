package com.abc.foaled.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Brendan on 7/02/2017.
 */

@DatabaseTable(tableName = "milestone")
public class Milestone {

    public enum MILESTONE{

        POOP(0),
        EAT(1),
        STAND(2),
        DRINK(3);

        private final int value;

        MILESTONE(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
    }
    //Variables
    @DatabaseField(id = true)
    private int milestoneID;
    @DatabaseField(unknownEnumName = "POOP")
    MILESTONE milestone;
    @DatabaseField()
    double startTime;
    @DatabaseField()
    double snoozeTime;
    @DatabaseField()
    String message;
    @DatabaseField()
    String detail;
    @DatabaseField()
    String notificationMessage;
    @DatabaseField()
    Boolean completed = false;

    public Milestone(int value) {
        //constructor stuff here
        this.milestone = MILESTONE.values() [value];
        milestoneID = milestone.getValue();

        switch (this.milestone) {
            case POOP:
                startTime = 4;
                snoozeTime = 1;
                message = "Your horse should have pooped by now";
                detail = "It's important your horse poos so that it can empty itself. You might need to give him a laxative.";
                notificationMessage = "Has your foal pooed?";
                break;

            case EAT:
                startTime = 4;
                snoozeTime = 1;
                message = "Your horse should have pooped by now";
                detail = "It's important your horse poos so that it can empty itself. You might need to give him a laxative.";
                notificationMessage = "Has your foal pooed?";
                break;

            case STAND:
                startTime = 2;
                snoozeTime = 0.5;
                message = "Your horse should have stood by now";
                detail = "It's important your horse stands so that his legs work";
                notificationMessage = "Has your horse stood?";
                break;

            case DRINK:
                startTime = 4;
                snoozeTime = 1;
                message = "Your horse should have pooped by now";
                detail = "It's important your horse poos so that it can empty itself. You might need to give him a laxative.";
                notificationMessage = "Has your foal pooed?";
                break;
        }

    }

    public void setCompleted() {
        this.completed = true;
    }
}
