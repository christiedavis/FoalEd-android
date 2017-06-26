package com.abc.foaled.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.database.ORMBaseActivity;
import com.abc.foaled.fragments.DatePickerFragment;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;
import com.abc.foaled.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.abc.foaled.helpers.PermissionsHelper.hasPermissions;
import static com.abc.foaled.helpers.PermissionsHelper.getPermissions;

public class AddNewHorseActivity extends ORMBaseActivity<DatabaseHelper> {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT = 2;
	private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 4;
    private String imagePath = "";
	private String tempPath = "";
    static final int API_LEVEL = android.os.Build.VERSION.SDK_INT;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_horse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
	    if (getSupportActionBar() != null)
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

/*
        ImageView iV = (ImageView) findViewById(R.id.add_horse_image);
	    iV.setImageBitmap(ImageHelper.bitmapSmaller(imagePath, 200, 200));*/

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
     *   //Todo only create the image on insert
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


	    if (resultCode == RESULT_OK) {

		    if (requestCode == REQUEST_IMAGE_CAPTURE) {
			    imagePath = tempPath;
		    } else if (requestCode == REQUEST_IMAGE_SELECT) {
			    try {
				    InputStream is;
				    is = getContentResolver().openInputStream(data.getData());

				    if (is != null) {
					    imagePath = ImageHelper.createImageFile(this);
					    if (imagePath != null) {
						    OutputStream fos = new FileOutputStream(imagePath);

						    byte[] buffer = new byte[65536];
						    int len;

						    while ((len = is.read(buffer)) != -1)
							    fos.write(buffer, 0, len);

						    fos.close();
						    is.close();
					    }
				    } else
					    throw new FileNotFoundException("Unable to find this file");

			    }  catch (IOException e) {
				    Toast.makeText(this, "unable to read in this photo", Toast.LENGTH_LONG).show();
				    Log.e("image_select", e.getMessage());
				    e.printStackTrace();

			    }
		    }

		    ImageView iV = ((ImageView) findViewById(R.id.add_horse_image));
		    int height = iV.getHeight() == 0 ? 300 : iV.getHeight();
		    int width = iV.getWidth() == 0 ? 300 : iV.getWidth();
		    iV.setImageBitmap(ImageHelper.bitmapSmaller(imagePath, width, height));
	    } else {
		    if (requestCode == REQUEST_IMAGE_CAPTURE) {
			    File f = new File(tempPath);
			    if (!f.delete())
				    Log.e("Image", "Unable to delete empty image that we were going to use for the photo");
		    }
	    }


/*

	    //If we were taking a photo and we cancel, delete the temporary file we made. If unable to delete this file, throw an exception
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode != RESULT_OK) {
		    File f = new File(tempPath);
		    try {
			    if (f.delete())
				    throw new IOException("Unable to remove existing file");
		    } catch (IOException e) {
				e.printStackTrace();
		    }
	    }


	    if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_SELECT) && resultCode == RESULT_OK) {
		    InputStream is;

		    try {
			    //If we took the photo, open inputStream from file that the photo was loaded in to
			    if (requestCode == REQUEST_IMAGE_CAPTURE) {
				    is = new FileInputStream(new File(tempPath));
			    }
			    else //else get it from the URI
				    is = getContentResolver().openInputStream(data.getData());

				//Create new internal file
			    imagePath = ImageHelper.createImageFile(this);
			    OutputStream fos = new FileOutputStream(imagePath);

			    //Copy the external file to the internal file
			    byte[] buffer = new byte[65536];
			    int len;
				if (is != null) {
					while ((len = is.read(buffer)) != -1)
						fos.write(buffer, 0, len);

					fos.close();
					is.close();
				} else
					throw new IOException();
		    } catch (FileNotFoundException e) {
			    Log.e("PHOTO", "Unable to open input or output stream. Please view stack trace");
			    e.printStackTrace();
		    } catch (IOException e) {
			    Log.e("PHOTO", "Unable to write to output stream. Please look at stack trace");
			    e.printStackTrace();
		    }
	    }

	    //If we selected a new photo, set the new photo in the image view
	    if (resultCode == RESULT_OK && !imagePath.isEmpty()) {
		    ImageView iV = ((ImageView) findViewById(R.id.add_horse_image));
		    int height = iV.getHeight() == 0 ? 300 : iV.getHeight();
		    int width = iV.getWidth() == 0 ? 300 : iV.getWidth();
		    iV.setImageBitmap(ImageHelper.bitmapSmaller(imagePath, width, height));
	    }*/
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

	    //TODO change date to be proper one
        Birth birth = new Birth(null, null, null, new DateTime());

	    Horse horse = new Horse(name, birth, true, notes, Horse.HORSE_STATUS.DORMANT, imagePath);
	    RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();
	    horseDao.assignEmptyForeignCollection(horse, "milestones");
	    horseDao.assignEmptyForeignCollection(horse, "births");
        horse.createMilestones();
	    horseDao.create(horse);

//	    birth.setEstConception(new Date());
        getHelper().getBirthsDataDao().create(birth);

        showSuccessConfirmation();
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
        TextView editText = (TextView) view;
        DialogFragment dialog = new DatePickerFragment();
        ((DatePickerFragment) dialog).setViewResult(editText);
        dialog.setRetainInstance(true);
        dialog.show(getFragmentManager(), "datePicker");
    }

    public void selectPhoto(View view) {

	    if (API_LEVEL >= 23) {
		    List<String> neededPermissions = hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		    //If there are permissions to request, then request all of the ones that aren't accepted
		    if (!neededPermissions.isEmpty()) {
			    getPermissions(this, WRITE_EXTERNAL_STORAGE_REQUEST_CODE, neededPermissions);
		    } else {
			    photoDialog();
		    }
	    }
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


		boolean canContinue = true;
		if (permissions.length > 0 && grantResults.length > 0) { //If the permissions request was not interrupted

			if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {

				for (int i = 0; i < grantResults.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						Toast.makeText(this, "This feature requires the requested permissions. It will not run without them.", Toast.LENGTH_LONG).show();
						Log.e("Permissions", "Denied permission; " +permissions[i] + ". Feature is disabled as a result");
						canContinue = false;
					}
				}
				if (canContinue)
					photoDialog();
			}
		}
	}

	private void photoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String[] items = getResources().getStringArray(imagePath.isEmpty() ? R.array.no_photo : R.array.new_photo);
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
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

//        Creates an empty temporary file in public directory Pictures/Friends, and then calls camera to take a photo
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	            String fileName = "JPEG_" + timeStamp;

	            File internalDirectory = new File(getFilesDir(), "images");
	            if (!internalDirectory.mkdirs())
		            Log.i("Image", "Image directory either already exists... or it was unable to be created (uh-oh)");

	            File image = null;

	            try {
		            image = File.createTempFile(fileName, ".jpg", internalDirectory);
	            } catch (IOException e) {
		            e.printStackTrace();
	            }

	            if (image != null) {
		            Uri photoURI = FileProvider.getUriForFile(this, "com.abc.foaled.fileprovider", image);
		            tempPath = image.getAbsolutePath(); //creates a temporary file, saving path in imagePath
		            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
		            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	            }


/*                //foal-ed public directory
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + "/FoalEd");
                //TODO change this temporary file thing to go in the app's cache instead

                try {
                    //creates a temp file that gets deleted when app closes (hopefully)
                    File image = File.createTempFile(
                            "temp",
                            ".jpg",
                            f
                    );
                    image.deleteOnExit();
                    tempPath = image.getAbsolutePath(); //creates a temporary file, saving path in imagePath
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
        }
    }

    /**
     * Removes the user's photo and sets it back to the default one
     */
    private void removePhoto() {
        imagePath = "";
        ImageView iV = (ImageView) findViewById(R.id.add_horse_image);
        iV.setImageBitmap(ImageHelper.bitmapSmaller(getResources(), R.drawable.default_horse, iV.getWidth(), iV.getHeight()));
    }

}