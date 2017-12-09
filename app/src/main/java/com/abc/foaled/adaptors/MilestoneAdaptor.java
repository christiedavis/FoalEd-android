package com.abc.foaled.adaptors;

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
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;

/**
 * Created by christie on 20/05/17.
 *
 */

public class MilestoneAdaptor extends ArrayAdapter<Milestone> {

    private RuntimeExceptionDao<Milestone, Integer> milestonesDataDao;

    public MilestoneAdaptor(Context context, ArrayList<Milestone> milestoneArrayList, RuntimeExceptionDao<Milestone, Integer> milestonesDataDao) {
        super(context, 0, milestoneArrayList);
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
                }
            }
        };

        milestoneCheckbox.setOnClickListener(clickListener);
        milestoneCheckbox.setChecked(milestone.isCompleted());

        return convertView;
    }
}
