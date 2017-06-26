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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_horse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
	    if (getSupportActionBar() != null)
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime());
        TextView dob = (TextView) findViewById(R.id.newHorseDOB);
        TextView concep = (TextView) findViewById(R.id.newHorseConceptionDate);
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
    }

	/**
	 * Finishes this activity because the user clicked the cancel button
	 *
	 * @param view cancel button
	 */
	public void cancel(View view) {
	    finish();
    }

    /**
     *  Adds a horse to the database with the relevant information entered in this activity
     * @param view The UI control that called this insert method
     */
    public void insert(View view) {

	    //Get name. Throw error if no name supplied
        EditText nameEditText = (EditText) findViewById(R.id.add_new_horse_name);
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError("Please don't leave name blank");
            return;
        }
	    String name = nameEditText.getText().toString();


	    //parse date
	    String dobString = ((TextView) findViewById(R.id.newHorseDOB)).getText().toString();
	    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
	    DateTime dob = formatter.parseDateTime(dobString);

	    //create arbitrary birth instance
	    Birth birth = new Birth(null, null, null, dob);


	    Boolean female = false;
	    Boolean pregnant = false;

	    CheckBox femaleCB = (CheckBox) findViewById(R.id.checkboxSex);
	    CheckBox pregnantCB = (CheckBox) findViewById(R.id.checkboxPregnant);

	    if (femaleCB.isChecked()) {
		    female = true;

		    if (pregnantCB.isChecked())
		    	pregnant = true;
	    }

		//TODO make a 'pick status' dialog that lets the user choose the status of the horse
	    Horse horse = new Horse(name, birth, female, null, pregnant ? Horse.HORSE_STATUS.PREGNANT : Horse.HORSE_STATUS.DORMANT, imagePath);

	    //Needs to be done in this order
	    RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();
	    horseDao.assignEmptyForeignCollection(horse, "milestones");
	    horseDao.assignEmptyForeignCollection(horse, "births");
        horse.createMilestones();
	    horseDao.create(horse);

		birth.setHorse(horse);
        getHelper().getBirthsDataDao().create(birth);

        finish();
    }

    public void toggleSex(View view) {
        CheckBox checkBox = (CheckBox) view;
        final ScrollView scrollView = (ScrollView) findViewById(R.id.content_add_new_horse);
        LinearLayout layout = (LinearLayout) findViewById(R.id.pregnantRow);
        if (checkBox.isChecked()) {
            layout.setVisibility(View.VISIBLE);
	        scrollView.post(new Runnable() {
		        @Override
		        public void run() {
			        scrollView.fullScroll(View.FOCUS_DOWN);
		        }
	        });
        } else {
            layout.setVisibility(View.GONE);
            findViewById(R.id.conceptionRow).setVisibility(View.GONE);
            ((CheckBox) findViewById(R.id.checkboxPregnant)).setChecked(false);
        }
    }

    public void togglePregnant(View view) {
        CheckBox checkBox = (CheckBox) view;
	    final ScrollView scrollView = (ScrollView) findViewById(R.id.content_add_new_horse);
        LinearLayout layout = (LinearLayout) findViewById(R.id.conceptionRow);
        if (checkBox.isChecked()) {
            layout.setVisibility(View.VISIBLE);
	        scrollView.post(new Runnable() {
		        @Override
		        public void run() {
			        scrollView.fullScroll(View.FOCUS_DOWN);
		        }
	        });
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