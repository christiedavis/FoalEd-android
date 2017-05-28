package com.abc.foaled.adaptors;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.foaled.R;
import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.models.Horse;

/**
 * Created by christie on 20/05/17.
 */

public class MilestoneAdaptor extends RecyclerView.Adapter<MilestoneAdaptor.MilestoneViewHolder> {

    private Horse horse;

    public MilestoneAdaptor(Horse horse) {
        this.horse = horse;
    }

    public static class MilestoneViewHolder extends RecyclerView.ViewHolder {

        CardView mv;
        TextView milestoneTitle;
        ImageView completedIcon;

        private final Context c = itemView.getContext();

        MilestoneViewHolder(View itemView) {
            super(itemView);
            mv = (CardView) itemView.findViewById(R.id.milestoneView);
            mv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent;

                    intent = new Intent(c, HorseDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    c.startActivity(intent);
                }
            });
            milestoneTitle = (TextView) itemView.findViewById(R.id.milestone_title);
            completedIcon = (ImageView) itemView.findViewById(R.id.milestone_completed);
        }
    }

    @Override
    public MilestoneViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_milestone, viewGroup, false);

        return new MilestoneViewHolder(v);
    }

    @Override
    public int getItemCount() {
        if ( horse.getMilestones() == null ) {
            horse.createMilestones();
        }

            Log.d("Size = " +  Integer.toString(horse.getMilestones().size()), "");
            return horse.getMilestones().size();
    }

    @Override
    public void onBindViewHolder(MilestoneViewHolder holder, int i) {
        holder.milestoneTitle.setText(horse.getMilestones().get(i).getTitle());
        holder.completedIcon.setImageResource(R.drawable.ic_favourite_unfilled);

        if (horse.getMilestones().get(i).isCompleted())
            holder.completedIcon.setImageResource(R.drawable.ic_favourite_filled);
        else
            holder.completedIcon.setImageResource(R.drawable.ic_favourite_unfilled);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

