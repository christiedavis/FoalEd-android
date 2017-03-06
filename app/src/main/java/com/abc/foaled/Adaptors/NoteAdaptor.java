package com.abc.foaled.Adaptors;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abc.foaled.Activity.NoteActivity;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;

import java.util.List;

/**
 * Created by Brendan on 23/02/2017.
 *
 */

public class NoteAdaptor extends RecyclerView.Adapter<NoteAdaptor.NoteViewHolder> {

    private String horseNote;
    private String[] birthNotes;

    public NoteAdaptor(String horseNote, String[] birthNotes) { this.horseNote = horseNote; this.birthNotes = birthNotes; }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;


        NoteViewHolder(View itemView) {
            super(itemView);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                   int viewType) {
        // create a new view
/*        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;*/
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_card, viewGroup, false);
        return new NoteViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return mDataset.length;
        return 1;
    }
}
