package com.abc.foaled.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.abc.foaled.R;
import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.notifications.CompleteNotification;
import com.abc.foaled.notifications.NotificationPublisher;
import com.abc.foaled.notifications.SnoozeNotification;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;
import org.joda.time.Days;


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
    private long snoozeTime;
    @DatabaseField
    private String message;
    @DatabaseField
    private String notificationMessage;
    @DatabaseField
    private String notificationTitle;
    @DatabaseField
    private Boolean completed = false;

    @DatabaseField(foreign = true)
    private Horse h;

	//Required empty constructor
    public Milestone() {

    }

    Milestone(int value, Horse horse, Context context) {
        this.h = horse;
        //constructor stuff here
        this.milestone = MILESTONE.values() [value];
        milestoneID = milestone.getValue();

	    DateTime birthTime = horse.getDateOfBirth().getBirthTime();

        switch (this.milestone) {
            case POOP:

				startTime = birthTime.plusSeconds(10);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 15; // 15 seconds
                message = "Your horse should have pooped by now";
                notificationMessage = "It's important your horse poos so that it can empty itself. You might need to give them a laxative.";
                notificationTitle = "Has your foal pooed?";
                break;

            case EAT:
                startTime = birthTime.plusSeconds(90);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have eaten by now";
                notificationMessage = "It's important your horse eats";
                notificationTitle = "Has your foal eaten?";
                break;

            case STAND:
                startTime = birthTime.plusSeconds(30);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have stood by now";
                notificationMessage = "It's important your horse stands so that his legs work";
                notificationTitle = "Has your horse stood?";
                break;

            case DRINK:
                startTime = birthTime.plusSeconds(60);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have drunk by now";
                notificationMessage = "It's important your horse drinks.";
                notificationTitle = "Has your foal drunk yet?";
                break;
        }
        if (Days.daysBetween(h.getDateOfBirth().getBirthTime(), DateTime.now()).getDays() < 50) {
			scheduleNotification(context, startTime, snoozeTime, milestone.getValue());
		}
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public String getNotificationTitle() {
		return notificationTitle;
	}

	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}

	public Boolean isCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	private void scheduleNotification(Context context, DateTime time, long snoozeTime, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification

		//Notification and intent ID.. these should really be different /-:
		//	combination of horseID and milestoneID (5 & 3 = 53)
		//	results in no conflicting IDs
		int actualID = Integer.parseInt(h.getHorseID()+""+notificationId);


		//-----ON CLICK INTENT, takes you to the horse view-------
		Intent horseIntent = new Intent(context, HorseDetailActivity.class);
		horseIntent.putExtra(Horse.HORSE_ID, h.getHorseID());

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(HorseDetailActivity.class);
		stackBuilder.addNextIntent(horseIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		//----------------------------------------------------------


		//Done action on the notification
		Intent completeIntent = new Intent(context, CompleteNotification.class);
		PendingIntent completePendingIntent = PendingIntent.getBroadcast(context, actualID, completeIntent, 0);
		NotificationCompat.Action doneAction = new NotificationCompat.Action(R.drawable.ic_done_black_18dp, "Done", completePendingIntent);


		//Snooze action on the notification
		Intent snoozeIntent = new Intent(context, SnoozeNotification.class);
		snoozeIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, actualID);
		snoozeIntent.putExtra(NotificationPublisher.NOTIFICATION_MESSAGE, notificationTitle);
		snoozeIntent.putExtra(NotificationPublisher.NOTIFICATION_TITLE, notificationMessage);
		snoozeIntent.putExtra(NotificationPublisher.NOTIFICATION_RESULT_INTENT, resultPendingIntent);
		snoozeIntent.putExtra(NotificationPublisher.SNOOZE_TIME, snoozeTime);
		PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 1111, snoozeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationCompat.Action snoozeAction = new NotificationCompat.Action(R.drawable.ic_add_white_18dp, "snooze (5 mins)", snoozePendingIntent);


		//Builds the notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				.setContentTitle(notificationTitle)
				.setContentText(notificationMessage)
				.setAutoCancel(true)
				.addAction(doneAction)
				.addAction(snoozeAction)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setSmallIcon(R.drawable.ic_menu_pregnancy)
				.setLargeIcon(ImageHelper.bitmapSmaller(context.getResources(), R.drawable.ic_menu_pregnancy, 50, 50))
				.setContentIntent(resultPendingIntent)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));



		Intent notificationIntent = new Intent(context, NotificationPublisher.class);
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, actualID);
		notificationIntent.putExtra(NotificationPublisher.SNOOZE_TIME, snoozeTime);
		notificationIntent.putExtra(NotificationPublisher.INTENT_ID, actualID);
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, builder.build());

		//Pending intent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, actualID, notificationIntent, 0);


		long futureInMillis = time.getMillis();

		//sets an alarm at the exact time given.
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
	}
}
