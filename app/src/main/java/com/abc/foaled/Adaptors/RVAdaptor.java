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

import com.abc.foaled.Helpers.ImageHelper;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.Activity.HorseDetailActivity;
import com.abc.foaled.R;

import java.util.List;

/**
 * Created by christie on 29/12/16.
 * Used to generate cards :)
 */

public class RVAdaptor extends RecyclerView.Adapter<RVAdaptor.HorseViewHolder>{

    List<Horse> horses;

    public RVAdaptor(List<Horse> horses) { this.horses = horses; }

    public static class HorseViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView horseName;
        TextView horseStatus;
        ImageView horsePhoto;
        ImageView favouriteIcon;

        int horseID;
        private final Context c = itemView.getContext();

        HorseViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            cv.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent;

                    intent = new Intent(c, HorseDetailActivity.class);
                    intent.putExtra("HorseID", horseID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    c.startActivity(intent);
                }
            });
            horseName = (TextView)itemView.findViewById(R.id.horse_name);
            horseStatus = (TextView)itemView.findViewById(R.id.horse_status);
            horsePhoto = (ImageView)itemView.findViewById(R.id.horse_photo);
            favouriteIcon = (ImageView)itemView.findViewById(R.id.favourite);
        }
    }

    @Override
    public HorseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_card, viewGroup, false);

        return new HorseViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return horses.size();
    }

    @Override
    public void onBindViewHolder(HorseViewHolder holder, int i) {
        holder.horseName.setText(horses.get(i).name);

        holder.horseStatus.setText(horses.get(i).getStatusString());
        holder.horseID = horses.get(i).getHorseID();
        holder.horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horses.get(i).bigImagePath, 200, 200));
        if (horses.get(i).isFavourite()) {
            holder.favouriteIcon.setImageResource(R.drawable.ic_favourite_filled);
        } else {
            holder.favouriteIcon.setImageResource(R.drawable.ic_favourite_unfilled);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}