package com.abc.foaled;

import android.app.Notification;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.abc.foaled.Activity.AddNewHorseActivity;
import com.abc.foaled.Adaptors.RVAdaptor;
import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.DatabaseTables.Horse;
import com.abc.foaled.Notifications.NotificationScheduler;

import java.util.List;

public class MainActivity extends ORMBaseActivity<DatabaseHelper>
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView rvHorses;
    List<Horse> horses;
    RVAdaptor adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.d("Application Started", "YAY");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Settings drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //TODO fix this
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewHorseActivity.class);
                startActivity(intent);
            }
        });

        // SET UP VIEW


        rvHorses = (RecyclerView) findViewById(R.id.rv);
        // Initialize Horse list with all horses in database
        horses = getHelper().getHorseDataDao().queryForAll();
        // Create adapter passing in the sample user data
        adapter = new RVAdaptor(horses);
        // Attach the adapter to the recyclerview to populate items
        rvHorses.setAdapter(adapter);
        // Set layout manager to position the items
        rvHorses.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onResume() {
        super.onResume();
        horses = getHelper().getHorseDataDao().queryForAll();
        //TODO this seems like the wrong way to update the recycler view?
        adapter = new RVAdaptor(horses);
        rvHorses.setAdapter(adapter);
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

        createNotification();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNotification() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this);


        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(Notification.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle("FoalEd")
                .setContentText("<Insert notification here>");

        NotificationScheduler notificationScheduler = new NotificationScheduler(this);
        notificationScheduler.schedule(notificationBuilder.build(), 10000, new Intent(this, MainActivity.class));

/*        Intent resultIntent = new Intent(this, HomePageActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        1,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        mBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle("FoalEd")
                .setContentText("<Insert notification here>");


        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_ALL;


        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        2,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        long futureInMillis = SystemClock.elapsedRealtime() + 5000;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);*/

    }
}