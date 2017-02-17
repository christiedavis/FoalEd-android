package com.abc.foaled.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.Models.Horse;
import com.abc.foaled.Activity.HorseDetailActivity;
import com.abc.foaled.R;

import java.io.File;
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
        TextView personName;
        TextView personAge;
        ImageView personPhoto;
        int horseID;
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
                    intent.putExtra("HorseID", position);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    c.startActivity(intent);


                }
            });
            personName = (TextView)itemView.findViewById(R.id.horse_name);
            personAge = (TextView)itemView.findViewById(R.id.horse_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.horse_photo);
            horseID =   getAdapterPosition();
        }
    }

    @Override
    public HorseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_card, viewGroup, false);
        HorseViewHolder horseCard = new HorseViewHolder(v);
        return horseCard;
    }

    @Override
    public int getItemCount() {
        return horses.size();
    }

    @Override
    public void onBindViewHolder(HorseViewHolder holder, int i) {
        holder.personName.setText(horses.get(i).name);
        holder.horseID = horses.get(i).getHorseID();
//        holder.personAge.setText(horses.get(i).age);
//        holder.personPhoto.setImageResource(horses.get(i).imagePath);
//        holder.personPhoto.setImageResource(R.drawable.christie);
        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap imageBitmap = BitmapFactory.decodeFile(horses.get(i).imagePath, options);*/
        //holder.personPhoto.setImageBitmap(horses.get(i).getImage(true));
        holder.personPhoto.setImageURI(Uri.fromFile(new File(horses.get(i).smallImagePath)));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}