package com.abc.foaled;

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
    int startTime;
    int snoozeTime;
    String message;
    String detail;
    String notificationMessage;

    public Milestone(MILESTONE milestone) {
        //constructor stuff here
        this.milestone = milestone;
        switch (this.milestone) {
            case MILESTONE_POOP:
                startTime = 4;


        }

    }
}
