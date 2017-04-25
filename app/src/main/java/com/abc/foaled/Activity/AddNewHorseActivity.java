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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewHorseActivity extends ORMBaseActivity<DatabaseHelper> {

    //TODO just checking that this bad boy actually works relatively well
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT = 2;
    private String imagePath = "";
    private StringBuilder imageFileName = new StringBuilder();
    private int API_LEVEL = 1;

    private UserInfo userInfo;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //TODO reckon we can put all photo related methods in the ImageHelper class to be re-used everywhere else?
    //This would require a little bit of rework
    //TODO: make the keyboard not come up straight away

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
//        dob.setText(date);
        dob.setHint(date);

        API_LEVEL = android.os.Build.VERSION.SDK_INT;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * Deals with the result of coming back from another intent when started with 'startActivityForResult
     *
     * @param requestCode Custom request code send with 'startActivityForResult'. Used to determine what to do with result
     * @param resultCode The activity returns a result code, depending on what you did
     * @param data The intent we just came from. Will usually hold data
     */
    //TODO If user clicks select new photo while original is not Placeholder image, remove old image
    //But this will not work, because what if the user goes back from the camera or gallery activity?
    //TODO deal with Picasa web album URI's
    //TODO fix error with camera when using S4
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);

        //If we just took a photo
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { //RESULT_OK = -1
            try {
                FileInputStream fis = new FileInputStream(new File(imagePath));
                imagePath = ImageHelper.createImageFile(this, imageFileName);

                FileOutputStream fos = openFileOutput(imageFileName.toString(), Context.MODE_PRIVATE);
                byte[] byteArray = new byte[2048];
                while (fis.read(byteArray) != -1)
                    fos.write(byteArray);
                fos.close(); fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //If we just selected a photo from the gallery
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
                imagePath = cursor.getString(column_index);
            }
            finally {
                if (cursor != null)
                    cursor.close();
            }

            //TODO: Brendan - make the default image drawable/brown-horse

            /*
                Creates FIS from selected image path, then changes imagePath to be a new empty File
                We then copy the bytes from the selected image into the new internal image file
             */

            try {
                FileInputStream fis = new FileInputStream(new File(imagePath));
                imagePath = ImageHelper.createImageFile(this, imageFileName);
                File file = new File(imagePath);
                String name = file.getName();
                FileOutputStream fos = openFileOutput(name, Context.MODE_PRIVATE);
                byte[] byteArray = new byte[2048];
                while (fis.read(byteArray) != -1)
                    fos.write(byteArray);
                fos.close();
                fis.close();

            } catch (Exception e) {
                    e.printStackTrace();
            }
        }

        //Pull photo through bitmapFactory and display
        ImageView iV = ((ImageView) findViewById(R.id.add_horse_image));
        int height = iV.getHeight();
        int width = iV.getWidth();
        iV.setImageBitmap(ImageHelper.bitmapSmaller(imagePath, height, width));
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

        String name = "Christie"; //((EditText) findViewById(R.id.add_horse_name)).getText().toString();
        String marking = "Brown brown"; //((EditText) findViewById(R.id.add_markings_text)).getText().toString();
        String notes = "notes notes"; // ((EditText) findViewById(R.id.add_notes_text)).getText().toString();

        Boolean sexIsFemale = true; //((RadioButton) findViewById(R.id.isFemaleRadioButton)).isChecked();

        Birth birth = new Birth();

        Horse horse = new Horse(name, birth, marking, notes, sexIsFemale);

        horse.setImagePath(imagePath);

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
        LinearLayout layout = (LinearLayout) findViewById(R.id.pregnantRow);
        if (checkBox.isChecked()) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
            findViewById(R.id.conceptionRow).setVisibility(View.GONE);
            ((CheckBox) findViewById(R.id.checkboxPregnant)).setChecked(false);
        }
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
        TextView editText = (TextView) findViewById(R.id.newHorseDOB);

        DialogFragment dialog = new DatePickerFragment();
        ((DatePickerFragment) dialog).setViewResult(editText);
        dialog.setRetainInstance(true);
//        ((DatePickerDialog)dialog.getDialog()).getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        dialog.show(getFragmentManager(), "datePicker");


    }

}