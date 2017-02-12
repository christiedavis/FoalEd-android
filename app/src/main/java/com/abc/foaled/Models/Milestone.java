package com.abc.foaled.Models;

/**
 * Created by Brendan on 7/02/2017.
 */

public class Milestone {

    public static enum MILESTONE{

        MILESTONE_POOP(0),
        MILESTONE_EAT(1),
        MILESTONE_STAND(2),
        MILESTONE_DRINK(3);

        private final int value;

        MILESTONE(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
    }
    //Variables
    MILESTONE milestone;
    double startTime;
    double snoozeTime;
    String message;
    String detail;
    String notificationMessage;

    public Milestone(MILESTONE milestone) {
        //constructor stuff here
        this.milestone = milestone;
        switch (this.milestone) {
            case MILESTONE_STAND:
                startTime = 2;
                snoozeTime = 0.5;
                message = "Your horse should have stood by now";
                detail = "It's important your horse stands so that his legs work";
                notificationMessage = "Has your horse stood?";
                break;

            case MILESTONE_POOP:
                startTime = 4;
                snoozeTime = 1;
                message = "Your horse should have pooped by now";
                detail = "It's important your horse poos so that it can empty itself. You might need to give him a laxative.";
                notificationMessage = "Has your foal pooed?";
                break;




        }

    }
}
