package com.abc.foaled.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by Brendan on 7/02/2017.
 *
 */

public class NotificationScheduler {

    private Context context;

/*    public NotificationScheduler(Context context, String title, String message, PendingIntent targetIntent) {
        this.context = context;

        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setContentIntent(targetIntent)
                .setContentText(message)
                .setContentTitle(title)
                .setPriority(Notification.PRIORITY_MAX)
                .setShowWhen(true);
    }*/

    public NotificationScheduler(Context context) {
        this.context = context;
    }

    public void schedule(Notification notification, long time, Intent targetIntent) {
        notification.defaults |= Notification.DEFAULT_ALL;

        //Sets the notification to open targetIntent on click
        notification.contentIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    targetIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );


        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        2,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        long futureInMillis = SystemClock.elapsedRealtime() + time;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }


}
