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
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.Fragment.DatePickerFragment;
import com.abc.foaled.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNewHorseActivity extends ORMBaseActivity<DatabaseHelper> {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT = 2;
    private String bigImagePath = "";
    private String smallImagePath = "";
    private int API_LEVEL = 1;
    private BitmapFactory.Options options = new BitmapFactory.Options();


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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView iV = (ImageView) findViewById(R.id.imageView3);
        options.inSampleSize = 4;
        iV.setImageBitmap(BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.christie, options));

        API_LEVEL = android.os.Build.VERSION.SDK_INT;

        bigImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/FoalEd/placeholder.jpg";
        smallImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/FoalEd/Small_Versions/placeholder.jpg";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
/*
        File image = new File(bigImagePath);
        image.delete();*/
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
    //TODO make new photos (camera or gallery) temp, until inserted where they then make the files
    //Because when you select a photo, currently a file is made. Select multiple photos before deciding,
    //      and then you have multiple files, and only the most recently selected gets deleted.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);

        //If the activity WAS taking a photo
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { //RESULT_OK = -1
            try {
                File newImage = createImageFile(true);
                FileOutputStream outputStream = new FileOutputStream(newImage);
                BitmapFactory.decodeFile(bigImagePath).compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                outputStream.close();
            } catch (IOException ioE) {
                ioE.printStackTrace();
            }
            //Pull photo through bitmapFactory and display
            ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(BitmapFactory.decodeFile(smallImagePath));
//            Toast.makeText(getApplicationContext(), bigImagePath, Toast.LENGTH_LONG).show();

            //If request code is from selecting photo from gallery
        } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {

            /*
             * List all the media's, then query that media using returned uri
             * Go to the first option (which should be our file), and list
             *  file's absolute path
             * Then make cursor null
             */
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                bigImagePath = cursor.getString(column_index);
            }
            finally {
                if (cursor != null)
                    cursor.close();
            }


            /**
             * Creates new image (which also alters string imagePath
             * Read chosen file
             */

            try {
                File newImage;

                //Make the big image
                FileInputStream inputStream = new FileInputStream(new File(bigImagePath));
                newImage = createImageFile(false);
                FileOutputStream outputStream = new FileOutputStream(newImage);
                byte[] byteArray = new byte[1024];
                while (inputStream.read(byteArray) != -1)
                    outputStream.write(byteArray);
                inputStream.close();

                //Makes the small image, based off the big image
                newImage = createImageFile(true);
                outputStream = new FileOutputStream(newImage);
                BitmapFactory.decodeFile(bigImagePath).compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(BitmapFactory.decodeFile(smallImagePath));
            Toast.makeText(getApplicationContext(), bigImagePath, Toast.LENGTH_LONG).show();
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

        //Gets the Data Access Object and creates a new Horse row
        RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();
        //RuntimeExceptionDao<Births, Integer> birthsDao = getHelper().getBirthsDataDao();
        Horse horse = new Horse();
        horse.name = nameView.getText().toString();

/*        ImageView iV = (ImageView) findViewById(R.id.imageView3);
        if (imagePath.equals("")) {
            File newImage;
            try {
                //TODO Get rid of assets and open through bitmap and read bytes and save
                InputStream inputStream = getAssets().open("christie.jpg");
                newImage = createImageFile();
                FileOutputStream outputStream = new FileOutputStream(newImage);
                byte[] byteArray = new byte[1024];
                while (inputStream.read(byteArray) != -1) {
                    outputStream.write(byteArray);
                }
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        horse.setImagePath(bigImagePath, smallImagePath);
        horseDao.create(horse);

        query(view);

        showSucessConfirmation();
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
     * TODO This is not ideal and need to change how you select what source (from the positive/negative methods)
     * @param view The control that calls this method
     */
    public void chooseImage(View view) {
        //Checks to see whether the user has permission to read&write
        if (API_LEVEL >= 23)
            verifyStoragePermissions(this);

        //Creates a dialog to choose where the photo cromes from
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

        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                photoFile = createImageFile(false);
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
     * @return
     * @throws IOException
     */
    /**
     *
     * @param smallImage true means to create a smaller image file
     * @return Returns a new empty file in the storage directory for the app
     */
    private File createImageFile(boolean smallImage) {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";
            String storageDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/FoalEd";

            if (smallImage)
                storageDirPath += "/Small_Versions";

            //File storageDir = new File(storageDirPath);
            //storageDir.mkdirs();
/*        File image =  File.createTempFile(
                imageFileName,  *//* prefix *//*
                ".jpg",         *//* suffix *//*
                storageDir      *//* directory *//*
        );*/
            File image;
            //If we're creating the smaller image,
            if (smallImage) {
                File f = new File(bigImagePath);
                image = new File(storageDirPath + "/" + f.getName());
                smallImagePath = image.getAbsolutePath();
            } else {
                image = new File(storageDirPath + "/" + imageFileName);
                bigImagePath = image.getAbsolutePath();
            }
            image.createNewFile();
            return image;
        } catch (IOException ioE) {
            ioE.printStackTrace();
            return null;
        }
    }


    /**
     * Success method to be called when a horse has been succesfully added.
     */
    private void showSucessConfirmation() {
        Toast confirmationToast = Toast.makeText(this, "Horse added sucessfully", Toast.LENGTH_LONG);
        confirmationToast.show();

        NavUtils.navigateUpFromSameTask(this);
        return;

    }
}