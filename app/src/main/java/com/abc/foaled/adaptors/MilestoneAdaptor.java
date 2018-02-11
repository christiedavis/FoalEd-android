package com.abc.foaled.adaptors;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.models.Horse;
import com.abc.foaled.models.Milestone;
import com.abc.foaled.notifications.CompleteNotification;
import com.abc.foaled.notifications.NotificationPublisher;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;

import static com.abc.foaled.models.Milestone.MILESTONE_ID;

/**
 * Created by christie on 20/05/17.
 *
 */

public class MilestoneAdaptor extends ArrayAdapter<Milestone> {

    private RuntimeExceptionDao<Milestone, Integer> milestonesDataDao;
    private Context context;

    public MilestoneAdaptor(Context context, ArrayList<Milestone> milestoneArrayList, RuntimeExceptionDao<Milestone, Integer> milestonesDataDao) {
        super(context, 0, milestoneArrayList);
        this.context = context;
        this.milestonesDataDao = milestonesDataDao;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Milestone milestone = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.milestone_list_view, null);

        TextView title = convertView.findViewById(R.id.milestoneTitle);
        title.setText(milestone != null ? milestone.getNotificationTitle() : "No milestone text");

        CheckBox milestoneCheckbox = convertView.findViewById(R.id.milestoneCheckbox);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (milestone.isCompleted() == false) { // IDK if this should not be able to be unchecked
                    milestone.toggleCompleted();
                    ((CheckBox) view).setChecked(milestone.isCompleted());
                    milestonesDataDao.update(milestone);

                    int notificationID = milestone.getNotificationID();

                    Intent intent = new Intent(context, NotificationPublisher.class);

                    Log.d("CANCEL_NOTIFICATION", "pending notification id = " +  notificationID);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, 0);
                    pendingIntent.cancel();
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(notificationID);

//                    Intent completeIntent = new Intent(context, CompleteNotification.class);
//                    completeIntent.putExtra(Horse.HORSE_ID, milestone.getHorseID());
//                    completeIntent.putExtra(MILESTONE_ID, milestone.getID());
//                    completeIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationID);
//                    PendingIntent.getBroadcast(context, (int)System.nanoTime(), completeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                }
            }
        };

        milestoneCheckbox.setOnClickListener(clickListener);
        milestoneCheckbox.setChecked(milestone.isCompleted());

        return convertView;
    }
}
