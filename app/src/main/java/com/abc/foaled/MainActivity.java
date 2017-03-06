package com.abc.foaled;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
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

import com.abc.foaled.Activity.AboutActivity;
import com.abc.foaled.Activity.AddNewHorseActivity;
import com.abc.foaled.Activity.faqActivity;
import com.abc.foaled.Activity.FeedbackActivity;
import com.abc.foaled.Activity.NotificationSettingsActivity;
import com.abc.foaled.Activity.SettingsActivity;
import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.DatabaseManager;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.Fragment.FavouriteHorsesFragment;
import com.abc.foaled.Fragment.NotificationSettingsFragment;
import com.abc.foaled.Notifications.NotificationScheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends ORMBaseActivity<DatabaseHelper>
        implements NavigationView.OnNavigationItemSelectedListener, FavouriteHorsesFragment.OnListFragmentInteractionListener, NotificationSettingsFragment.OnFragmentInteractionListener {

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO create default placeholder image file if it doesn't exist already (essentially creating on first run through)
        try {
            createPlaceholderImageFile(getAssets().open("christie.jpg"));

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_main);
            Log.d("Application Started", "YAY");

            // Set up Nav
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            this.userInfo = UserInfo.getInstance();

            // SET UP FRAGMENT
            this.userInfo.horses = getHelper().getHorseDataDao().queryForAll(); //get data
//            this.userInfo.refresh();
            FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
            FavouriteHorsesFragment fragment = FavouriteHorsesFragment.newInstance();
            fragment.setListToBeDisplayed(this.userInfo.horses);
            fragmentManager.replace(R.id.flContent, fragment).commit();


            //Settings drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            //TODO fix this deprecated call
            drawer.setDrawerListener(toggle);
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
            System.out.println(ex.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //createPlaceholderImageFile();
        //TODO this seems like the wrong way to update the recycler view?
        this.userInfo.horses = getHelper().getHorseDataDao().queryForAll(); //get data
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        FavouriteHorsesFragment fragment = FavouriteHorsesFragment.newInstance();
        fragment.setListToBeDisplayed(this.userInfo.horses);
        fragmentManager.replace(R.id.flContent, fragment).commit();
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass;
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_horses :  // new fragment
                fragmentClass = NotificationSettingsFragment.class;

            case R.id.nav_foals : // new fragment
                fragmentClass = NotificationSettingsFragment.class;

            case R.id.nav_mares :     // new fragment
                fragmentClass = NotificationSettingsFragment.class;

            case R.id.nav_notifications :
                intent = new Intent(this, NotificationSettingsActivity.class);
                this.startActivity(intent);
                return true;

            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
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

            default:
                fragmentClass = NotificationSettingsFragment.class;
        }


        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

      //  createNotification();
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
        f.mkdir();

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

    private void createNotification() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this);

        notificationBuilder
                .setSmallIcon(R.mipmap.ic_horse_launcher)
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