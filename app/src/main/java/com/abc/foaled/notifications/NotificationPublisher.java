package com.abc.foaled.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationPublisher extends BroadcastReceiver {

	public static final String NOTIFICATION_ID = "notification-id";
	public static final String NOTIFICATION = "notification";
	public static final String REPEAT_TIME = "repeat-time";
	public static final String NOTIFICATION_TITLE = "notification-title";
	public static final String NOTIFICATION_MESSAGE = "notification-message";
	public static final String NOTIFICATION_RESULT_INTENT = "result-intent";

	public void onReceive(Context context, Intent intent) {

		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		//this wakes the phone.. do I want to use?
/*		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Tag");

		wakeLock.acquire();
		wakeLock.release();*/

		//Pulls out the intent and ID and displays it
		Notification notification = intent.getParcelableExtra(NOTIFICATION);    //Gets the notification out
		int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);            //Gets the ID out '12'

		if (notificationId == 0)
			throw new IllegalArgumentException("Un-allowed Notification ID of 0");


		notificationManager.notify(notificationId, notification);

		long repeatTime = intent.getLongExtra(REPEAT_TIME, 0);                  //Gets the snooze time of the milestone

		//If there was a repeat
		if (repeatTime != 0) {
			//Pending intent that will repeat what has just happened. So we use the same ID
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);

			Log.d("INTENT", "Published intent; ID = " + notificationId);


			//sets this to repeat in repeat-time milliseconds
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeatTime, pendingIntent);
		}

	}
}