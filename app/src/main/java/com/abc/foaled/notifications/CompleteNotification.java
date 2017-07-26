package com.abc.foaled.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.abc.foaled.models.Horse;

/**
 * Created by bcr6 on 7/4/17.
 *
 */

public class CompleteNotification extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		Log.d("NOTIFICATION", "completed notification");

		int notificationID = intent.getIntExtra(NotificationPublisher.NOTIFICATION_ID, 0);
		notificationManager.cancel(notificationID);

		Log.d("NOTIFICATION_ID", notificationID+"");

		//Pending intent
/*		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);*/


		int horseID = intent.getIntExtra(Horse.HORSE_ID, 0);
		int milestoneID = intent.getIntExtra("MILESTONE", 0);

		//If the horse's details haven't been passed through
/*		if (horseID == 0 || milestoneID == 0)
			throw new IllegalArgumentException("No horse ID passed into this class");*/



//		Horse horse =
	}
}
