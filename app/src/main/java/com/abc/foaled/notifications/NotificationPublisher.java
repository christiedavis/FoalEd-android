package com.abc.foaled.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationPublisher extends BroadcastReceiver {

	public static final String INTENT_ID = "intent-id";
	public static final String NOTIFICATION_ID = "notification-id";
	public static final String NOTIFICATION = "notification";
	public static final String SNOOZE_TIME = "snooze-time";
	public static final String NOTIFICATION_TITLE = "notification-title";
	public static final String NOTIFICATION_MESSAGE = "notification-message";
	public static final String NOTIFICATION_RESULT_INTENT = "result-intent";

	public void onReceive(Context context, Intent intent) {

		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

/*		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Tag");

		wakeLock.acquire();
		wakeLock.release();*/

		Notification notification = intent.getParcelableExtra(NOTIFICATION);
		int id = intent.getIntExtra(NOTIFICATION_ID, 0);
		long snoozeTime = intent.getLongExtra(SNOOZE_TIME, 0);
		int intentID = intent.getIntExtra(INTENT_ID, 0);
		notificationManager.notify(id, notification);



		//Pending intent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intentID, intent, 0);

		//sets up the next alarm in snoozeTime milliseconds
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+snoozeTime, pendingIntent);

	}
}