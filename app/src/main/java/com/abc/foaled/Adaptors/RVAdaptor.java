package com.abc.foaled.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.foaled.DatabaseTables.Horse;
import com.abc.foaled.Activity.HorseDetailActivity;
import com.abc.foaled.R;

import java.util.List;

/**
 * Created by christie on 29/12/16.
 * Used to generate cards :)
 */

public class RVAdaptor extends RecyclerView.Adapter<RVAdaptor.HorseViewHolder>{

    List<Horse> horses;

    public static class HorseViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;
        private final Context c = itemView.getContext();

        HorseViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            cv.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Intent intent;
                    intent = new Intent(c, HorseDetailActivity.class);
                    intent.putExtra("nameText", position);
                    //intent.putExtra("desc",details[position]);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    c.startActivity(intent);

                }
            });
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personAge = (TextView)itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }


    public RVAdaptor(List<Horse> horses) { this.horses = horses; }

    @Override
    public HorseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_card, viewGroup, false);
        HorseViewHolder pvh = new HorseViewHolder(v);
        return pvh;
    }


    @Override
    public int getItemCount() {
        return horses.size();
    }

    @Override
    public void onBindViewHolder(HorseViewHolder holder, int i) {
        holder.personName.setText(horses.get(i).name);
//        holder.personAge.setText(horses.get(i).age);
        holder.personPhoto.setImageResource(horses.get(i).photo);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}