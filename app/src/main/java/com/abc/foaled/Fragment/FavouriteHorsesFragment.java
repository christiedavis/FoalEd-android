package com.abc.foaled.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abc.foaled.Adaptors.RVAdaptor;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link FavouriteHorsesFragment.OnListFragmentInteractionListener}
 * interface.
 */
public class FavouriteHorsesFragment extends Fragment {

    private List<Horse> mHorses;
    private OnListFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavouriteHorsesFragment() {
    }

    @SuppressWarnings("unused")
    public static FavouriteHorsesFragment newInstance() {
        FavouriteHorsesFragment fragment = new FavouriteHorsesFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        int height = container.getHeight();
//        int width = container.getHeight();

         if (mHorses == null) {      //error
             Log.e(null, "onCreateView: ERROR no horses ", null);
         }

         View view = inflater.inflate(R.layout.fragment_favourite_horses, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new RVAdaptor(mHorses));
//            Log.d("LayoutManager Height - ", recyclerView.getLayoutManager().getHeight() + "");
//            Log.d("RecyclerView Height - ", recyclerView.getHeight() + "");
        }
//        Log.d("View Height - ", view.getHeight() + "");
        return view;
    }

    public void setListToBeDisplayed(List<Horse> horseList) {
        mHorses = horseList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Horse horse);

    }
}