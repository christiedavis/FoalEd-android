package com.abc.foaled.models;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.abc.foaled.R;
import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.notifications.NotificationPublisher;
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

    Milestone(int value, Horse horse, Context context) {
        this.h = horse;
        //constructor stuff here
        this.milestone = MILESTONE.values() [value];
        milestoneID = milestone.getValue();

	    DateTime birthTime = horse.getDateOfBirth().getBirthTime();

        switch (this.milestone) {
            case POOP:

				startTime = birthTime.plusMinutes(2);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5; // 5 minutes
                message = "Your horse should have pooped by now";
                detail = "It's important your horse poos so that it can empty itself. You might need to give them a laxative.";
                notificationMessage = "Has your foal pooed?";
                break;

            case EAT:
                startTime = birthTime.plusSeconds(90);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have eaten by now";
                detail = "It's important your horse eats";
                notificationMessage = "Has your foal eaten?";
                break;

            case STAND:
                startTime = birthTime.plusSeconds(30);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have stood by now";
                detail = "It's important your horse stands so that his legs work";
                notificationMessage = "Has your horse stood?";
                break;

            case DRINK:
                startTime = birthTime.plusMinutes(1);
	            emergencyTime = startTime.plusHours(1);
                snoozeTime = 1000 * 60 * 5;
                message = "Your horse should have drunk by now";
                detail = "It's important your horse drinks.";
                notificationMessage = "Has your foal drunk yet?";
                break;
        }
        if (Days.daysBetween(h.getDateOfBirth().getBirthTime(), DateTime.now()).getDays() < 50) {
			scheduleNotification(context, startTime, milestone.getValue());
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

	private void scheduleNotification(Context context, DateTime time, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification
		Intent horseIntent = new Intent(context, HorseDetailActivity.class);
		horseIntent.putExtra("horseID", h.getHorseID());

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(HorseDetailActivity.class);
		stackBuilder.addNextIntent(horseIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				.setContentTitle(notificationMessage)
				.setContentText(detail)
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setLargeIcon(ImageHelper.bitmapSmaller(context.getResources(), R.drawable.default_foal, 50, 50))
				.setContentIntent(resultPendingIntent)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


		Intent notificationIntent = new Intent(context, NotificationPublisher.class);
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, Integer.parseInt(h.getHorseID()+""+notificationId));
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, builder.build());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		long futureInMillis = time.getMillis();
		long now = System.currentTimeMillis();
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
	}

}
