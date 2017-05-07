package com.abc.foaled.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import android.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Fragment.DatePickerFragment;
import com.abc.foaled.Helpers.ImageHelper;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Birth;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewHorseActivity extends ORMBaseActivity<DatabaseHelper> {

    //TODO just checking that this bad boy actually works relatively well
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT = 2;
    private String imagePath = "";
    private StringBuilder imageFileName = new StringBuilder();
    static final int API_LEVEL = android.os.Build.VERSION.SDK_INT;

    private UserInfo userInfo;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //TODO reckon we can put all photo related methods in the ImageHelper class to be re-used everywhere else?
    //This would require a little bit of rework

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_horse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePath = getFilesDir().getAbsolutePath() + "/placeholder.jpg";
        this.userInfo = UserInfo.getInstance(this);

        ImageView iV = (ImageView) findViewById(R.id.add_horse_image);
        iV.setImageBitmap(ImageHelper.bitmapSmaller(imagePath, 200, 200));

        String date = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.UK).format(Calendar.getInstance().getTime());
        TextView dob = (TextView) findViewById(R.id.newHorseDOB);
        TextView concep = (TextView) findViewById(R.id.newHorseConceptionDate);
//        dob.setText(date);
        dob.setHint(date);
        concep.setHint(date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * This is called when an activity is called with an intent to return with a result.
     * @param requestCode The (hopefully) unique code that got sent with the intent
     * @param resultCode Successful or not code
     * @param data The data returned from the intent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If we just took a photo
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { //RESULT_OK = -1
            try {

                FileChannel fis = new FileInputStream(new File(imagePath)).getChannel();
                imagePath = ImageHelper.createImageFile(this); //changes the imagePath variable to new empty file
                int lastIndex = imagePath.lastIndexOf('/') + 1;
                FileChannel fos = openFileOutput(imagePath.substring(lastIndex, imagePath.length()), MODE_PRIVATE).getChannel();
                fos.transferFrom(fis, 0, fis.size());
                fos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //------------------If we just selected a photo from the gallery--------------
        else if(requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            /* //TODO need to keep a track of what images we take but don't save..
             * List all the media's, then query that media using returned uri
             * Go to the first option (which should be our file), and list
             *  file's absolute path. We then copy file from public directory in to private memory
             * Then make cursor null
             */
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);

                int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                imagePath = cursor.getString(column_index);


            /* Creates FIS from selected image path, then changes imagePath to be a new empty File
                We then copy the bytes from the selected image into the new internal image file*/

                FileChannel fis = new FileInputStream(new File(imagePath)).getChannel();
                imagePath = ImageHelper.createImageFile(this);
                int i = imagePath.lastIndexOf('/') + 1;
                FileChannel fos = openFileOutput(imagePath.substring(i, imagePath.length()), Context.MODE_PRIVATE).getChannel();
                fos.transferFrom(fis, 0, fis.size());
                fos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
//        If we selected an image
        if (!imagePath.isEmpty()) {
//        Sets the image as the new select image
            ImageView iV = ((ImageView) findViewById(R.id.add_horse_image));
            iV.setImageBitmap(ImageHelper.bitmapSmaller(imagePath, iV.getHeight(), iV.getWidth()));
        }
    }


    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     * @param activity The activity to check whether it has permissions or not
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

    /**
     *  Adds a horse to the database with the relevant information entered in this activity
     * @param view The UI control that called this insert method
     */
    public void insert(View view) {

//        String name = "Christie"; //((EditText) findViewById(R.id.add_horse_name)).getText().toString();
        EditText nameEditText = (EditText) findViewById(R.id.add_new_horse_name);
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError("Please don't leave name blank");
            return;
        }

        Boolean sexIsFemale = ((CheckBox) findViewById(R.id.checkboxSex)).isChecked();

        String name = nameEditText.getText().toString();
        String notes = ""; // ((EditText) findViewById(R.id.add_notes_text)).getText().toString();

        Birth birth = new Birth();
        Horse horse = new Horse(name, birth, notes, sexIsFemale);
        horse.setImagePath(imagePath);

        getHelper().getHorseDataDao().assignEmptyForeignCollection(horse, "milestones");
        horse.createMilestones();

        getHelper().addNewHorse(birth, horse);

        showSuccessConfirmation();
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


        //Creates a dialog to choose where the photo comes from
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
     * Creates the camera intent to take the photo. All photos that are accepted by this camera
     * intent are saved as temporary files in a public directory (public because camera doesn't
     * have permission to save to internal directory).
     * TODO need to delete all left over photos on insert method
     */
    public void takeImage() {

        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //foal-ed public directory
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + "/FoalEd");

                File image = null;
                try {
                    //creates a temp file that gets deleted when app closes (hopefully)
                    image = File.createTempFile(
                            "temp",
                            ".jpg",
                            f
                    );
                    image.deleteOnExit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imagePath = image.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Success method to be called when a horse has been successfully added.
     */
    private void showSuccessConfirmation() {
        Toast.makeText(this, "Horse added successfully", Toast.LENGTH_SHORT).show();
        NavUtils.navigateUpFromSameTask(this);
    }

    public void toggleSex(View view) {
        CheckBox checkBox = (CheckBox) view;
        ScrollView scrollView = (ScrollView) findViewById(R.id.content_add_new_horse);
        LinearLayout layout = (LinearLayout) findViewById(R.id.pregnantRow);
        if (checkBox.isChecked()) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
            findViewById(R.id.conceptionRow).setVisibility(View.GONE);
            ((CheckBox) findViewById(R.id.checkboxPregnant)).setChecked(false);
        }
        scrollView.scrollTo(0, scrollView.getBottom());
    }

    public void togglePregnant(View view) {
        CheckBox checkBox = (CheckBox) view;
        LinearLayout layout = (LinearLayout) findViewById(R.id.conceptionRow);
        if (checkBox.isChecked()) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    public void selectDate(View view) {
//        TextView editText = (TextView) findViewById(R.id.newHorseDOB);

        TextView editText = (TextView) view;
        DialogFragment dialog = new DatePickerFragment();
        ((DatePickerFragment) dialog).setViewResult(editText);
        dialog.setRetainInstance(true);
//        ((DatePickerDialog)dialog.getDialog()).getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        dialog.show(getFragmentManager(), "datePicker");


    }

    public void selectPhoto(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final String[] items = getResources().getStringArray(imagePath.contains("placeholder") ? R.array.no_photo : R.array.new_photo);
        builder.setTitle("Change photo")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (items[which]) {
                            case "Take Photo":
                            case "Take new photo":
                                takePhoto();
                                break;
                            case "Select new photo":
                            case "Choose photo":
                                choosePhoto();
                                break;
                            case "Remove photo":
                                removePhoto();
                                break;

                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Mandatory onClick override. Don't need to do anything here
                    }
                }).create().show();
    }


    /**
     * Let's you choose a photo from the gallery to use as the contact photo
     */
    private void choosePhoto() {
        if (API_LEVEL >= 23)
            if (!checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                getPermissions(PERMISSIONS_STORAGE);
                return;
            }

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );

        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    /**
     * Uses the phone's camera to take a photo
     */
    private void takePhoto() {
        //If we need to check permissions
        if (API_LEVEL >= 23) {
            if (!checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                getPermissions(PERMISSIONS_STORAGE);
                return;
            }
        }
//        Creates an empty temporary file in public directory Pictures/Friends, and then calls camera to take a photo
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //foal-ed public directory
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + "/Friends");
                //TODO change this temporary file thing to go in the app's cache instead

                try {
                    //creates a temp file that gets deleted when app closes (hopefully)
                    File image = File.createTempFile(
                            "temp",
                            ".jpg",
                            f
                    );
                    image.deleteOnExit();
                    imagePath = image.getAbsolutePath(); //creates a temporary file, saving path in imagePath
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Removes the user's photo and sets it back to the default one
     */
    private void removePhoto() {
        imagePath = getFilesDir().getAbsolutePath() + "/placeholder.jpg";
        ImageView iV = (ImageView) findViewById(R.id.add_horse_image);
        iV.setImageBitmap(ImageHelper.bitmapSmaller(imagePath, iV.getHeight(), iV.getWidth()));
    }

    /**
     * Returns whether the user has access to this particular permission
     * @param activity The activity requesting this permission
     * @param permission The particular permission to look in to
     * @return True if the activity has the permission, False if not
     */
    private boolean checkPermissions(Activity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permissions from the user.
     * @param permissions The list of permissions to request from the user
     */
    private void getPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    /**
     * @return The current image path to the image in the image view. Will be and empty String if no image
     */
    public String getImagePath() {
        return imagePath;
    }


}