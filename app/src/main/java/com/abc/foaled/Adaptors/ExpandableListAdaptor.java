/*
package com.abc.foaled.Adaptors;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import com.abc.foaled.Models.Birth;

import java.util.ArrayList;

*/
/**
 * Created by christie on 9/04/17.
 *//*


public class ExpandableListAdaptor implements ExpandableListAdapter{

        private Context context;
        private ArrayList<Birth> births;

        public ExpandableListAdaptor(Context context, ArrayList<Birth> births) {
            this.context = context;
            this.births = births;
        }
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return births.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
//            ArrayList<births> productList = births.get(groupPosition).
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return births.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            ArrayList<ChildItemsInfo> productList = teamName.get(groupPosition).getSongName();
            return productList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupItemsInfo headerInfo = (GroupItemsInfo) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.group_items, null);
            }

            TextView heading = (TextView) convertView.findViewById(R.id.heading);
            heading.setText(headerInfo.getName().trim());
            return convertView;

        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildItemsInfo detailInfo = (ChildItemsInfo) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.child_items, null);
            }
            TextView childItem = (TextView) convertView.findViewById(R.id.childItem);
            childItem.setText(detailInfo.getName().trim());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    }
}*/
