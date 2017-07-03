package com.abc.foaled.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class NotificationPublisher extends BroadcastReceiver {

	public static String NOTIFICATION_ID = "notification-id";
	public static String NOTIFICATION = "notification";

	public void onReceive(Context context, Intent intent) {

		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

/*
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		Log.e("screen on.................................", ""+isScreenOn);
		if(isScreenOn==false)
		{
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.WA |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
			wl.acquire(10000);
			PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

			wl_cpu.acquire(10000);
		}*/

		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Tag");

		wakeLock.acquire();
		wakeLock.release();

		Notification notification = intent.getParcelableExtra(NOTIFICATION);
		int id = intent.getIntExtra(NOTIFICATION_ID, 0);
		notificationManager.notify(id, notification);

	}
}