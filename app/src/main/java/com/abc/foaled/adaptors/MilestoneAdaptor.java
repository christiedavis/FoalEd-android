package com.abc.foaled.adaptors;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.models.Horse;
import com.abc.foaled.models.Milestone;

import java.util.ArrayList;

/**
 * Created by christie on 20/05/17.
 *
 */

public class MilestoneAdaptor extends ArrayAdapter<Milestone> {


    public MilestoneAdaptor(Context context, ArrayList<Milestone> milestoneArrayList) {
        super(context, 0, milestoneArrayList);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Milestone milestone = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.milestone_list_view, null);

        TextView title = convertView.findViewById(R.id.milestoneTitle);
        title.setText(milestone.getNotificationTitle());


        return convertView;
    }

}

