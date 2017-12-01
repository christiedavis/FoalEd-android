package com.abc.foaled.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.app.DialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.database.DatabaseHelper;
import com.abc.foaled.database.ORMBaseActivity;
import com.abc.foaled.fragments.DatePickerFragment;
import com.abc.foaled.fragments.NumberPickerFragment;
import com.abc.foaled.fragments.TimePickerFragment;
import com.abc.foaled.helpers.DateTimeHelper;
import com.abc.foaled.helpers.ImageHelper;
import com.abc.foaled.models.Birth;
import com.abc.foaled.models.Horse;
import com.abc.foaled.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;

import java.awt.font.NumericShaper;
import java.io.File;
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
import static com.abc.foaled.MainActivity.API_LEVEL;
import static com.abc.foaled.helpers.DateTimeHelper.DATE_FORMATTER;
import static com.abc.foaled.helpers.PermissionsHelper.hasPermissions;
import static com.abc.foaled.helpers.PermissionsHelper.getPermissions;

public class AddNewHorseActivity extends ORMBaseActivity<DatabaseHelper> implements
		DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
	private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 4;
    private String imagePath = null;
	private String tempPath = "";

	DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
	DateTimeFormatter dateAndTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy - HH:mm");
    DateTimeFormatter gmtFormatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'GMT'Z yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_horse);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
	    if (getSupportActionBar() != null)
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//gets current date
		String date = DateTime.now().toString(DATE_FORMATTER);

//        TextView dob = findViewById(R.id.newHorseDOB);
        TextView concep = findViewById(R.id.newHorseConceptionDate);
//        dob.setText(date);
        concep.setText(date);
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
				    InputStream is = getContentResolver().openInputStream(data.getData());

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
	    finish();
    }

    /**
     *  Adds a horse to the database with the relevant information entered in this activity
     * @param view The UI control that called this insert method
     */
    public void insert(View view) {

	    //Get name. Throw error if no name supplied
        EditText nameEditText = findViewById(R.id.horseName);
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError("Please don't leave name blank");
            return;
        }
        //NAME---------------------------------
	    String name = nameEditText.getText().toString();

		//DOB------------------------------------------
//	    String dobString = ((TextView) findViewById(R.id.newHorseDOB)).getText().toString();//TODO FIX THIS
	    DateTime dob = DATE_FORMATTER.parseDateTime(DateTime.now().minusYears(3).toString());

		Calendar today = Calendar.getInstance();
		Integer yob = today.get(Calendar.YEAR) - Integer.valueOf(3);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, yob );
		cal.set(Calendar.MONTH, today.get(Calendar.MONTH));
		cal.set(Calendar.DATE, today.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date ageDate = cal.getTime();

        DateTime ageDateTime = DateTime.parse(ageDate.toString(), gmtFormatter);

		//BREED--------------------------------
		EditText breedET = (EditText) findViewById(R.id.horseBreed);
		String breed = breedET.getText().toString();

		// DAm -------------
		EditText damET = (EditText) findViewById(R.id.newHorseDam);
		String dam = damET.getText().toString();

		// Colour -------------
		EditText colourEt = (EditText) findViewById(R.id.newHorseColour);
		String colour = colourEt.getText().toString();

		// SIRES NAME -------------------
		String sire = ((EditText) findViewById(R.id.siresName)).getText().toString();

		Date date = new Date();
	    //create empty birth instance for horse
	    Birth birth = new Birth(null, sire, null, ageDateTime);

	    //PREGNANT STATUS -------------------------------------
	    Boolean pregnant = ((CheckBox) findViewById(R.id.checkboxPregnant)).isChecked();

	    //status of horse
		Horse.HORSE_STATUS status = Horse.HORSE_STATUS.DORMANT;

		//assumes a horse can't get pregnant at less than a year old
		if (DateTimeHelper.getAgeInYears(dob) < 1)
			status = Horse.HORSE_STATUS.FOAL;
		else if (pregnant)
			status = Horse.HORSE_STATUS.PREGNANT;


		//TODO make a 'pick status' dialog that lets the user choose the status of the horse
	    Horse horse = new Horse(name, birth, true, null, status, imagePath);
	   horse.setExtraDetails(breed, dam, sire, colour);

	    //Needs to be done in this order
	    RuntimeExceptionDao<Horse, Integer> horseDao = getHelper().getHorseDataDao();
	    horseDao.assignEmptyForeignCollection(horse, "milestones");
	    horseDao.assignEmptyForeignCollection(horse, "births");
        horse.createMilestones(this);
	    horseDao.create(horse);

		//Make a new birth object if the horse is pregnant
		if (horse.getStatus() == Horse.HORSE_STATUS.PREGNANT) {
			//get conception date
			String concep = ((TextView) findViewById(R.id.newHorseConceptionDate)).getText().toString();
			DateTime conceptionDate = DATE_FORMATTER.parseDateTime(concep);

			//creates birth
			Birth b = new Birth(horse, sire, conceptionDate, conceptionDate.plusDays(getResources().getInteger(R.integer.days_to_birth)));
			getHelper().getBirthsDataDao().create(b);

			//attach current pregnancy birth object to the horse we are also adding
			horse.setCurrentBirth(b);
			horseDao.update(horse);
		}

		//attach the new horse to the empty birth instance we made
		birth.setHorse(horse);
        getHelper().getBirthsDataDao().create(birth);

        finish();
    }

    public void togglePregnant(final View view) {
        CheckBox checkBox = (CheckBox) view;
	    final ScrollView scrollView = findViewById(R.id.content_add_new_horse);
        LinearLayout layout = findViewById(R.id.conceptionRow);

		DateTime dob = DATE_FORMATTER.parseDateTime(DateTime.now().minusYears(3).toString()); //TODO FIX THIS
        if (checkBox.isChecked()) {
			if (Days.daysBetween(dob, DateTime.now()).getDays() > getResources().getInteger(R.integer.foal_to_horse_days)) {
				layout.setVisibility(View.VISIBLE);
				findViewById(R.id.siresNameRow).setVisibility(View.VISIBLE);
				scrollView.post(new Runnable() {
					@Override
					public void run() {
						scrollView.fullScroll(View.FOCUS_DOWN);
					}
				});
			} else {
				Toast.makeText(this, "Your horse is too young to get pregnant!", Toast.LENGTH_LONG).show();
				checkBox.setChecked(false);
			}
        } else {
            layout.setVisibility(View.GONE);
	        findViewById(R.id.siresNameRow).setVisibility(View.GONE);
	        ((EditText) findViewById(R.id.siresName)).setText("");
        }
    }

    public void selectDate(View view) {

		String date = ((TextView) view).getText().toString();

		DateTime conception = view.getId() == R.id.newHorseConceptionDate ? DateTime.now().minusYears(1) : null;

		DialogFragment dialog = DatePickerFragment.newInstance(date, this, conception);
		//used to distinguish between selection dob and conception dates
		String tag = "dobPicker"; //TODO FIX THIS
        dialog.show(getFragmentManager(), tag);
    }

    @Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		int textViewID;
/*		if (getFragmentManager().findFragmentByTag("dobPicker") != null) {//TODO FIX THIS
			textViewID = R.id.newHorseDOB;
		} else*/
			textViewID = R.id.newHorseConceptionDate;

		TextView dateField = findViewById(textViewID);
		String parsedDob = day + "/" + (month + 1) + "/" + year;
		dateField.setText(parsedDob);
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

        //Creates an empty temporary file in public directory Pictures/Friends, and then calls camera to take a photo
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	        //TODO make this use the ImageHelper class to create the file
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