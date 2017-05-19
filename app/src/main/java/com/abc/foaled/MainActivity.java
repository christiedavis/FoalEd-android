package com.abc.foaled;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
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

import com.abc.foaled.activities.AboutActivity;
import com.abc.foaled.activities.AddNewHorseActivity;
import com.abc.foaled.activities.faqActivity;
import com.abc.foaled.activities.FeedbackActivity;
import com.abc.foaled.activities.NotificationSettingsActivity;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.database.DatabaseManager;
import com.abc.foaled.database.ORMBaseActivity;
import com.abc.foaled.models.Horse;
import com.abc.foaled.fragments.HorsesListFragment;
import com.abc.foaled.fragments.NotificationSettingsFragment;
import com.abc.foaled.notifications.NotificationScheduler;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class MainActivity extends ORMBaseActivity<DatabaseHelper>
        implements NavigationView.OnNavigationItemSelectedListener, HorsesListFragment.OnListFragmentInteractionListener,
        NotificationSettingsFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO create default placeholder image file if it doesn't exist already (essentially creating on first run through)
        try {
            createPlaceholderImageFile(getAssets().open("default_horse.jpg"));

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_main);
            Log.d("Application Started", "YAY");

            // Set up Nav
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // SET UP FRAGMENT
            FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
            HorsesListFragment fragment = HorsesListFragment.newInstance();
            fragment.setListToBeDisplayed(getHelper().getHorseDataDao().queryForAll());
            fragmentManager.replace(R.id.flContent, fragment).commit();

            //Settings drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            DatabaseManager.init(this);

            //floating action button
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, AddNewHorseActivity.class);
                    startActivity(intent);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        checkForUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO this seems like the wrong way to update the recycler view?
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        HorsesListFragment fragment = HorsesListFragment.newInstance();
        fragment.setListToBeDisplayed(getHelper().getHorseDataDao().queryForAll());
        fragmentManager.replace(R.id.flContent, fragment).commit();
        checkForCrashes();
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass = NotificationSettingsFragment.class;
        int id = item.getItemId();
        Intent intent;
        Boolean favourite = false;

        switch (id) {
            case R.id.nav_all_horses :  // new fragment
                fragmentClass = HorsesListFragment.class;
                break;

            case R.id.nav_fav_horses :  // new fragment
                fragmentClass = HorsesListFragment.class;
                favourite = true;
                break;

            case R.id.nav_notifications :
                intent = new Intent(this, NotificationSettingsActivity.class);
                this.startActivity(intent);
                return true;

            case  R.id.nav_faq :
                intent = new Intent(this, faqActivity.class);
                this.startActivity(intent);
                return true;

            case R.id.nav_about :
                intent = new Intent(this, AboutActivity.class);
                this.startActivity(intent);
                return true;

            case R.id.nav_feedback:
                intent = new Intent(this, FeedbackActivity.class);
                this.startActivity(intent);
                return true;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();


        if (fragmentClass == HorsesListFragment.class) {

	        List<Horse> horses;
	        horses = favourite ? getFavouriteHorses() : getHelper().getHorseDataDao().queryForAll();

	        HorsesListFragment fragment1 = (HorsesListFragment) fragment;
	        if (fragment1 != null)
	            fragment1.setListToBeDisplayed(horses);
        }

        fragmentManager.replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

	public List<Horse> getFavouriteHorses() {
		try {
			QueryBuilder<Horse, Integer> queryBuilder = getHelper().getHorseDataDao().queryBuilder();
			return queryBuilder.where().eq("favourite", true).query();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Database Query", "Error querying for horses");

			List<Horse> allHorses = getHelper().getHorseDataDao().queryForAll();
			List<Horse> favouriteHorses = new ArrayList<>();

			for (Horse h : allHorses)
				if (h.isFavourite())
					favouriteHorses.add(h);

			return favouriteHorses;
		}
	}

    @Override //needed
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Horse item) {

    }

    public void createPlaceholderImageFile(InputStream inputStream) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + "/FoalEd");
        if (!f.mkdir()) {
	        Log.i("FILE", "Unable to create FoalEd directory. It either exists, or we were unable to write");
        }

        File placeholderImage = new File(getFilesDir().getAbsolutePath() + "/placeholder.jpg");
        if (!placeholderImage.exists()) {
            try {
                FileOutputStream fos = openFileOutput("placeholder.jpg", Context.MODE_PRIVATE);
                byte[] array = new byte[2048];
                while (inputStream.read(array) != -1)
                    fos.write(array);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

/*    private void createNotification() {
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

        Intent resultIntent = new Intent(this, HomePageActivity.class);
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
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

    } */

    public void favouriteAction(View view) {
    // To be implemented later when we want to favourite on the main screen
        Log.d("", "");
    }

    // for hockey app
    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
}