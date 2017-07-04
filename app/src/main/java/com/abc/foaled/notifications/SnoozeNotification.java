package com.abc.foaled.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.abc.foaled.R;
import com.abc.foaled.helpers.ImageHelper;

/**
 * Created by Brendan on 7/4/17.
 *
 */

public class SnoozeNotification extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {


		Log.d("NOTIFICATION", "Snoozed notification");

		int actualID = intent.getIntExtra(NotificationPublisher.NOTIFICATION_ID, 0);
		Log.d("NOTIFICATION", "Snoozed notification with "+actualID);

		String notificationTitle = intent.getStringExtra(NotificationPublisher.NOTIFICATION_TITLE);
		String notificationMessage = intent.getStringExtra(NotificationPublisher.NOTIFICATION_MESSAGE);
		long snoozeTime = intent.getLongExtra(NotificationPublisher.SNOOZE_TIME, 0);
		PendingIntent resultIntent = intent.getParcelableExtra(NotificationPublisher.NOTIFICATION_RESULT_INTENT);


		//Done action
		Intent completeIntent = new Intent(context, CompleteNotification.class);
		PendingIntent completePendingIntent = PendingIntent.getBroadcast(context, actualID, completeIntent, 0);
		NotificationCompat.Action doneAction = new NotificationCompat.Action(R.drawable.ic_done_black_18dp, "Complete", completePendingIntent);

		//Snooze intent
		PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, actualID, intent, 0);
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
				.setContentIntent(resultIntent)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


		Intent notificationIntent = new Intent(context, NotificationPublisher.class);

		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, actualID);
		notificationIntent.putExtra(NotificationPublisher.SNOOZE_TIME, snoozeTime);
		notificationIntent.putExtra(NotificationPublisher.INTENT_ID, actualID);
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, builder.build());


		//Pending intent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, actualID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(1000*60*5), pendingIntent);
	}
}
