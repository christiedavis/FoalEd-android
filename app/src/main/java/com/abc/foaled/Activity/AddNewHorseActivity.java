package com.abc.foaled.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.DatabaseTables.Horse;
import com.abc.foaled.Fragment.DatePickerFragment;
import com.abc.foaled.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class AddNewHorseActivity extends ORMBaseActivity<DatabaseHelper> {

    public final static String EXTRA_MESSAGE = "";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT = 2;
    private String imagePath = "";
    private int API_LEVEL = 1;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_horse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        API_LEVEL = android.os.Build.VERSION.SDK_INT;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * On return to this activity, if it was from camera then change imageView to
     * the saved image
     *
     * THIS HAS AN ERROR
     * Not actually an error, but I noticed a bug in which the imagePath is not updated
     * and so the imageview is not updated at all... It was curious.
     * @param requestCode -
     * @param resultCode -
     * @param data -
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap imageBitmap;

        //If the activity WAS taking a photo
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { //RESULT_OK = -1
            //Pull photo through bitmapFactory and display
            imageBitmap = BitmapFactory.decodeFile(imagePath, options);
            ImageView test = (ImageView) findViewById(R.id.imageView3);
            test.setImageBitmap(imageBitmap);
            Toast.makeText(getApplicationContext(), imagePath, Toast.LENGTH_LONG).show();

        } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {

            /*
             * List all the media's, then query that media using returned uri
             * Go to the first option (which should be our file), and list
             *  file's absolute path
             * Then make cursor null
             *
             * TODO Get a URI in to a bitmap factory, OR compress a bitmap easily
             */
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                imagePath = cursor.getString(column_index);
            }
            finally {
                if (cursor != null)
                    cursor.close();
            }

            imageBitmap = BitmapFactory.decodeFile(imagePath, options);
            ImageView test = (ImageView) findViewById(R.id.imageView3);
            test.setImageBitmap(imageBitmap);
            Toast.makeText(getApplicationContext(), imagePath, Toast.LENGTH_LONG).show();
        }


    }


    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     * @param activity The activity to check whether it has permissions or now
     */
    private static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public void update(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setViewResult((TextView) view);
        newFragment.show(getFragmentManager(), "Date Picker");
    }


    public void insert(View view) {

        EditText nameView = (EditText) findViewById(R.id.editText2);
        TextView ageView = (TextView) findViewById(R.id.editText3);
        EditText colourView = (EditText) findViewById(R.id.editText4);

        RadioGroup test = (RadioGroup) findViewById(R.id.radioGroup);
        RadioButton radioSex = (RadioButton) findViewById(test.getCheckedRadioButtonId());

/*        String name = nameView.getText().toString();
        String age = ageView.getText().toString();
        String colour = colourView.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = dateFormat.parse(age);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //True if selected female, false if not
        boolean sex = radioSex.getText().equals("Female");*/

/*        if (name.equals("") || age.equals("")) {
            if (name.equals(""))
                nameView.setError("Field cannot be left blank");
            if (age.equals(""))
                ageView.setError("Field cannot be left blank");
            return;
        }*/

        //Gets the Data Access Object and creates a new Horse row
        RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();
        Horse horse = new Horse();
        horseDao.create(horse);

        query(view);
    }

    /**
     * Pulls all the horses from the database into database objects and
     * displays them in the text view.
     * @param view The control that calls this method
     */
    public void query(View view) {
        RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();

        TextView tv = (TextView) findViewById(R.id.textView4);
        String display = "";

        List<Horse> Horses = horseDao.queryForAll();

        for (Horse h : Horses)
            display += h.toString() + "\n";

        tv.setText(display);

    }

    /**
     * Displays an alert dialog box that lets your select what source to get
     * the image from; Camera or Gallery
     * TODO This is not ideal and need to change how you select what source
     * @param view The control that calls this method
     */
    public void chooseImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose photo source");

        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);
            }
        });
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takeImage();
            }
        });
        builder.show();
    }

    /**
     * Access the camera to take an image through an activityForResult
     */
    public void takeImage() {
        //Checks to see whether the user has permission to read&write
        if (API_LEVEL >= 23)
            verifyStoragePermissions(this);


        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    /**
     * Creates the empty file in the right directory with a unique filename
     * @return Returns a new empty file in the storage directory for the app
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/FoalEd";
        File storageDir = new File(storageDirPath);
        storageDir.mkdirs();
        File image =  File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imagePath = image.getAbsolutePath();
        return image;
    }
}
