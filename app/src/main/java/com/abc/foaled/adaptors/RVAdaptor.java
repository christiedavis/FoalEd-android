package com.abc.foaled.adaptors;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.models.Horse;
import com.abc.foaled.activities.HorseDetailActivity;
import com.abc.foaled.R;

import java.util.List;

/**
 * Created by christie on 29/12/16.
 * Used to generate cards :)
 */

public class RVAdaptor extends RecyclerView.Adapter<RVAdaptor.HorseViewHolder>{

    private List<Horse> horses;

    public RVAdaptor(List<Horse> horses) { this.horses = horses; }

    static class HorseViewHolder extends RecyclerView.ViewHolder {

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
        if ( horses != null ) {
            return horses.size();
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(HorseViewHolder holder, int i) {
		Horse horse = horses.get(i);
        holder.horseName.setText(horse.getName());

        holder.horseStatus.setText(horse.getStatusString());
        holder.horseID = horse.getHorseID();

		if (horse.getImagePath().isEmpty()) {
			int image = horse.getStatus() == Horse.HORSE_STATUS.FOAL ? R.drawable.default_foal : R.drawable.default_horse;
			holder.horsePhoto.setImageResource(image);

		} else
			holder.horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.getImagePath(), 200, 200));

		int star = horse.isFavourite() ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_unfilled;
        holder.favouriteIcon.setImageResource(star);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}