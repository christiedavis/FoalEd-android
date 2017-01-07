package com.abc.foaled;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private String[] drawerStringArray;
    private AppBarLayout appBarLayout;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.d("Application Started", "YAY");

        //this is for brendans settings drawer
        drawerStringArray = getResources().getStringArray(R.array.drawer_items_arrays);

        drawerList = (ListView) findViewById(R.id.navdrawer_list);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, drawerStringArray));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.CYAN);
        setSupportActionBar(toolbar);

//        drawerLayout = (DrawerLayout) findViewById(R.id.settings_drawer_layout);
//        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
//        appBarLayout.setBackgroundColor(Color.MAGENTA);
//        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
//        {
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//            super.onDrawerClosed(view);
//            getActionBar().setTitle("TEST1");
//        }
//
//            /** Called when a drawer has settled in a completely open state. */
//        public void onDrawerOpened(View drawerView) {
//            super.onDrawerOpened(drawerView);
//            getActionBar().setTitle("TEST2");
//        }
//        };
//
//        // Set the drawer toggle as the DrawerListener
//        drawerLayout.setDrawerListener(mDrawerToggle);
//
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);



            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager=(ViewPager)

            findViewById(R.id.container);

            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);


            //this is to go to the add horse screen from the floating action button
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    Intent intent = new Intent(MainActivity.this, AddHorseActivity.class);
                    startActivity(intent);
                }
            });

        };



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) // makes cards for second view
            {
                List<Horse> horses;

                horses = new ArrayList<>();
                horses.add(new Horse("Emma Wilson", "23 years old", R.drawable.christie));
                horses.add(new Horse("Lavery Maiss", "25 years old", R.drawable.emma));
                horses.add(new Horse("Lillie Watts", "35 years old", R.drawable.alitia));

                View rootView = inflater.inflate(R.layout.recyler_view, container, false);

                RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                RVAdaptor adapter = new RVAdaptor(horses);
                recyclerView.setAdapter(adapter);

                return rootView;
            }

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * EVERYTHING BELOW HERE IS FOR THE TAB PAGES
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override // Show 3 total pages.
        public int getCount() { return 3; }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "FOALS";
                case 1:
                    return "MARES";
                case 2:
                    return "PREGNANT";
            }
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            TextView t = (TextView) findViewById(R.id.toolbar_title);
            t.setText(getPageTitle(position));
        }
    }

    //this is for the settings drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("drawer click listener", "has been click at position" + position);
            Intent intent;

            //TODO: the rest of these options need screens to be linked too
            switch (position) {
                case 1:
                    //my profile
                    intent = new Intent(MainActivity.this, Profile.class);
                    break;
                case 2:
                    //notifications
                    intent = new Intent(MainActivity.this, NotificationSettings.class);
                    break;
//                case 3:
//                    //my horses - not required?
//                    break;
//                case 4:
//                    //settings
//                    break;
//                case 5:
//                    //feedback
//                    break;
                default:
                    intent = new Intent(MainActivity.this, MainActivity.class);
                    break;


            }
            drawerLayout.closeDrawer(drawerList);

            startActivity(intent);

        }
    }

}
