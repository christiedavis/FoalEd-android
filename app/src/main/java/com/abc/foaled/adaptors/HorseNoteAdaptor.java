package com.abc.foaled.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.abc.foaled.R;

import java.util.List;
import java.util.Map;

/**
 * Created by steven.brown on 18/04/2017.
 * http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */

public class HorseNoteAdaptor extends BaseExpandableListAdapter {

    private Context context;
    private List<String> yearHeaders;
    private Map<String, String> yearNotes;

    public HorseNoteAdaptor(Context context, List<String> yearHeaders, Map<String, String> yearNotes) {
        this.context = context;
        this.yearHeaders = yearHeaders;
        this.yearNotes = yearNotes;
    }

    @Override
    public int getGroupCount() {
        return yearHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return yearHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
//        return yearNotes.get(yearHeaders.get(groupPosition)).get(childPosition);
	    return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {

        long groupId = -1;
        try {
            groupId = Long.parseLong(yearHeaders.get(groupPosition));
        } catch (Exception ex) {
            Log.e("HorseNoteAdaptor", "Cannot Parse Group", ex);
        }
        return groupId;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String headerTitle = yearHeaders.get(groupPosition);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_group_header, null);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.year_header);
        textView.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_item, null);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
