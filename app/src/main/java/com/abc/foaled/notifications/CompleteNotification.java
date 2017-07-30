package com.abc.foaled.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.models.Horse;

import static com.abc.foaled.models.Milestone.MILESTONE_ID;

/**
 * Created by bcr6 on 7/4/17.
 *
 */

public class CompleteNotification extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		//Cancels the notification
		int notificationID = intent.getIntExtra(NotificationPublisher.NOTIFICATION_ID, 0);
		notificationManager.cancel(notificationID);

		//Cancels the pending intent set to fire. This dummy intent to have the same destination as one set to go off
		Intent toCancel = new Intent(context, NotificationPublisher.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, toCancel, PendingIntent.FLAG_CANCEL_CURRENT);
		pendingIntent.cancel();

		//Cancel the scheduled alarm. I don't really need to do this, but I'll do it for safety's sake
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);


		int horseID = intent.getIntExtra(Horse.HORSE_ID, 0);
		int milestoneID = intent.getIntExtra(MILESTONE_ID, 0);

		//If the horse's details haven't been passed through
		if (horseID == 0 || milestoneID == 0)
			throw new IllegalArgumentException("No horse or milestone ID passed into this class");

		//Throws the user into the foal's detail screen
		Intent startIntent = new Intent(context, HorseDetailActivity.class);
		startIntent.putExtra(Horse.HORSE_ID, horseID);
		startIntent.putExtra(MILESTONE_ID, milestoneID);
		context.startActivity(startIntent);

	}
}
