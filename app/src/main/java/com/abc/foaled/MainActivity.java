package com.abc.foaled;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.abc.foaled.Activity.AddNewHorseActivity;
import com.abc.foaled.Activity.NotificationSettingsActivity;
import com.abc.foaled.Activity.ProfileActivity;
import com.abc.foaled.Adaptors.RVAdaptor;
import com.abc.foaled.DatabaseTables.Horse;

import java.util.ArrayList;
import java.util.List;

import static com.abc.foaled.R.id.rv;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    private ListView drawerList;
    private String[] drawerStringArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.d("Application Started", "YAY");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Settings drawer
        drawerStringArray = getResources().getStringArray(R.array.drawer_items_arrays);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        drawerList = (ListView) findViewById(R.id.navdrawer_list);
//        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, drawerStringArray));
//        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        //floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewHorseActivity.class);
                startActivity(intent);
            }
        });

        // TODO: SET UP VIEW

        RecyclerView rvHorses = (RecyclerView) findViewById(R.id.rv);

        // Initialize Horse list
        List<Horse> horses = new ArrayList<>();
//      horses.add(new Horse("Emma Wilson", "23 years old", R.drawable.christie));
//      horses.add(new Horse("Lavery Maiss", "25 years old", R.drawable.emma));
//      horses.add(new Horse("Lillie Watts", "35 years old", R.drawable.alitia));
        for (int i = 0; i < 3; i++) {
            Horse horse = new Horse();
            horse.name = "horse" + i;
            horse.photo = R.drawable.christie;
            horses.add(horse);
        }
        // Create adapter passing in the sample user data
        RVAdaptor adapter = new RVAdaptor(horses);
        // Attach the adapter to the recyclerview to populate items
        rvHorses.setAdapter(adapter);
        // Set layout manager to position the items
        rvHorses.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        RVAdaptor adapter = new RVAdaptor(horses);
//        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}