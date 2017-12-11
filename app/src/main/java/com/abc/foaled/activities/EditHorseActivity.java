package com.abc.foaled.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.R;
import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.database.ORMBaseActivity;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.abc.foaled.MainActivity.API_LEVEL;
import static com.abc.foaled.helpers.PermissionsHelper.getPermissions;
import static com.abc.foaled.helpers.PermissionsHelper.hasPermissions;

public class EditHorseActivity extends ORMBaseActivity<DatabaseHelper> {

	private Horse horse = null;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
	private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 4;
    private String imagePath = null;
	private String tempPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_horse);

		findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveHorseEdit();
			}
		});
		populateHorseDetails(getIntent().getIntExtra(Horse.HORSE_ID, 0));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
	    if (getSupportActionBar() != null)
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				cancel(null);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void populateHorseDetails(int horseID) {
		if (horseID == 0)
			throw new IllegalArgumentException("Didn't pass through a horse ID to this activity");

//		Gets horse
		horse = getHelper().getHorseDataDao().queryForId(horseID);
		imagePath = horse.getImagePath();

//		Shows image, or default if no image
		ImageView horsePhoto = findViewById(R.id.add_horse_image);
		if (horse.getImagePath().isEmpty()) {
			int photo = horse.getStatus() == Horse.HORSE_STATUS.FOAL ? R.drawable.default_foal : R.drawable.default_horse;
			horsePhoto.setImageResource(photo);
		} else
			horsePhoto.setImageBitmap(ImageHelper.bitmapSmaller(horse.getImagePath(), 300, 300));

//		Populates other data
		((EditText) findViewById(R.id.notificationCheckbox3)).setText(horse.getName());
		((EditText) findViewById(R.id.horseBreed)).setText(horse.getBreed());
		((EditText) findViewById(R.id.newHorseDam)).setText(horse.getDam());
		((EditText) findViewById(R.id.newHorseAge)).setText(getString(R.string.edit_horse_age_placeholder,DateTime.now().getYear() - horse.getDateOfBirth().getBirthTime().getYear()));
		((EditText) findViewById(R.id.newHorseColour)).setText(horse.getColour());
	}

	/**
     * This is called when an activity is called with an intent to return with a result.
     * @param requestCode The (hopefully) unique code that got sent with the intent
     * @param resultCode Successful or not code
     * @param data The data returned from the intent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


	    if (resultCode == RESULT_OK) {

		    if (requestCode == REQUEST_IMAGE_CAPTURE) {
			    imagePath = tempPath;
		    } else if (requestCode == REQUEST_IMAGE_SELECT) {
			    try {
				    //Open up the image selected
				    InputStream is = data.getData() != null ? getContentResolver().openInputStream(data.getData()) : null;

				    if (is != null) {
					    //Creates a new empty file and assigns it to imagePath
					    imagePath = ImageHelper.createImageFile(this);

					    //Copies the selected image into our own directory
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
				    Toast.makeText(this, "Unable to read in this photo", Toast.LENGTH_LONG).show();
				    Log.e("IMAGE SELECTION", e.getMessage());
				    e.printStackTrace();

			    }
		    }

		    //set the image, selected or captured, in the image view
		    ImageView iV = findViewById(R.id.add_horse_image);
		    int height = iV.getHeight() == 0 ? 300 : iV.getHeight();
		    int width = iV.getWidth() == 0 ? 300 : iV.getWidth();
		    iV.setImageBitmap(ImageHelper.bitmapSmaller(imagePath, width, height));

	    } else {
		    //if we went to take a photo and then cancelled, try to delete the empty file we had created
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
		setResult(RESULT_CANCELED);
	    finish();
    }

	/**
	 * Save the current horse with the data on the screen. Doesn't necessarily actually have to be updated at all.
	 */
	private void saveHorseEdit() {


//		NAME ----------------
		EditText nameEditText = findViewById(R.id.notificationCheckbox3);
		String name = nameEditText.getText().toString();
		if (name.isEmpty()) {
			nameEditText.setError("Required");
			return;
		}

//		DOB -----------------
		int age = Integer.parseInt(((TextView) findViewById(R.id.newHorseAge)).getText().toString());
		DateTime birthTime = DateTime.now().minusYears(age);

//		BREED ---------------
		String breed = ((EditText) findViewById(R.id.horseBreed)).getText().toString();

//		DAM -----------------
		String dam = ((EditText) findViewById(R.id.newHorseDam)).getText().toString();

//		COLOUR --------------
		String colour = ((EditText) findViewById(R.id.newHorseColour)).getText().toString();

		horse.update(name, breed, dam, colour, imagePath);

		Birth horseBirth = horse.getDateOfBirth();
		horseBirth.setBirthTime(birthTime);

		getHelper().getBirthsDataDao().update(horseBirth);
		getHelper().getHorseDataDao().update(horse);
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Requesting to add a photo
	 * @param view The view clicked that triggers this method
	 */
    public void selectPhoto(View view) {
	    //Checks to see whether we have the permissions required
	    if (API_LEVEL >= 23) {
		    List<String> neededPermissions = hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		    //If there are permissions to request, then request all of the ones that aren't accepted
		    if (!neededPermissions.isEmpty()) {
			    getPermissions(this, WRITE_EXTERNAL_STORAGE_REQUEST_CODE, neededPermissions);
			    return;
		    }
	    }
	    //if all is above board, continue
	    photoDialog();
    }

	/**
	 * Callback for the prompting the user of permissions
	 * @param requestCode The request code that was passed when requesting the permissions
	 * @param permissions The list of permissions requests
	 * @param grantResults The list of permissions, 0 being granted, -1 being denied
	 */
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

	/**
	 * Prompts the user to select how we are getting the new photo
	 */
	private void photoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String[] items = getResources().getStringArray(imagePath == null || imagePath.isEmpty() ? R.array.no_photo : R.array.new_photo);
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

					}
				}).create().show();
	}

    /**
     * Let's you choose a photo from the gallery to use as the image
     */
    private void choosePhoto() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    /**
     * Uses the phone's camera to take a photo
     */
    private void takePhoto() {

        //Creates an empty temporary file in public directory Pictures/Friends, and then calls camera to take a photo
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	        //Creates the file and passes the URI to the camera app
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

	            //Gives the image URI to the camera app so the camera app can write to a file in FoalEd's private directory
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
        imagePath = null;
        ImageView iV = findViewById(R.id.add_horse_image);
        iV.setImageBitmap(ImageHelper.bitmapSmaller(getResources(), R.drawable.default_horse, iV.getWidth(), iV.getHeight()));
    }

}