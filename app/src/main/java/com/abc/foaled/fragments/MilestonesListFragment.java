package com.abc.foaled.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.abc.foaled.R;
import com.abc.foaled.adaptors.MilestoneAdaptor;
import com.abc.foaled.adaptors.RVAdaptor;
import com.abc.foaled.models.Horse;
import com.abc.foaled.models.Milestone;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class MilestonesListFragment extends Fragment {

    public static final String FRAGMENT_TAG = "milestonesDetailsFragment";

    private ArrayList<Milestone> milestoneList;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MilestonesListFragment() {
    }

    @SuppressWarnings("unused")
    public static MilestonesListFragment newInstance(ArrayList<Milestone> milestones) {
        MilestonesListFragment fragment = new MilestonesListFragment();
        fragment.setArguments(new Bundle());
        fragment.milestoneList = milestones;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         if (milestoneList == null) {      //error
             Log.e(null, "onCreateView: ERROR no milestones for this horse ", null);
         }

         View view = inflater.inflate(R.layout.fragment_foal_milestones, container, false);

        // Set the adapter
        if (view instanceof LinearLayout) {
            Context context = view.getContext();
            ListView listView = view.findViewById(R.id.milestoneList);
            MilestoneAdaptor adapter = new MilestoneAdaptor(container.getContext(), milestoneList);
            listView.setAdapter(adapter);
            justifyListViewHeightBasedOnChildren(listView);
            listView.setScrollContainer(false);
        }
        return view;
    }

    public void setListToBeDisplayed(ArrayList<Milestone> milestones) {
        milestoneList = milestones;
    }

    public static void justifyListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null)
            return;

        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

}