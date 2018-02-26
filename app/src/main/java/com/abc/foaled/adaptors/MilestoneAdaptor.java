package com.abc.foaled.adaptors;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

        //set title
        TextView title = convertView.findViewById(R.id.milestoneTitle);
        title.setText(milestone != null ? milestone.getNotificationTitle() : "No milestone text");


        //set on click on checkbox
        CheckBox milestoneCheckbox = convertView.findViewById(R.id.milestoneCheckbox);
        milestoneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    milestone.setCompleted(true);
                    compoundButton.setChecked(milestone.isCompleted());
                    compoundButton.setEnabled(false);
                    milestonesDataDao.update(milestone);

                    //Disables the upcoming notifications and removes current one if it is there
                    int notificationID = milestone.getNotificationID();
                    Intent intent = new Intent(context, NotificationPublisher.class);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, 0);
                    pendingIntent.cancel();
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(notificationID);
            }
        });

        milestoneCheckbox.setChecked(milestone.isCompleted());

        //If milestone has already been complete
        if (milestone.isCompleted()) {
            milestoneCheckbox.setEnabled(!milestone.isCompleted());
            title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }


        return convertView;
    }
}
