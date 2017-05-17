package com.abc.foaled.Helpers;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.abc.foaled.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by christie & Brendan on 17/02/17.
 *
 */

public class ImageHelper {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createPlaceholderImageFile(InputStream inputStream) {
/*        //The directory the images are saved in
        String storageDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/FoalEd";
        //The image I'm looking for
        File image = new File(storageDirPath + "/placeholder.jpg");
        if (!image.exists()) {
            //Make both of the directories (fails if it already exists, which it probably shouldn't if it's getting here for the first time)
            File storageDir = new File(storageDirPath);
            storageDir.mkdir();
            File smallStorageDir = new File(storageDirPath + "/Small_Versions");
            smallStorageDir.mkdir();

            try {

                image.createNewFile();
                File smallVersion = new File(smallStorageDir + "/placeholder.jpg");
                smallVersion.createNewFile();
                //TODO use the drawable instead of asset for this
                //Opens default christie image, and reads it into the new file /FoalEd/placeholder.jpg
                //InputStream inputStream = MainActivity.getAssets().open("christie.jpg");
                FileOutputStream outputStream = new FileOutputStream(image);
                byte[] byteArray = new byte[1024];
                while (inputStream.read(byteArray) != -1)
                    outputStream.write(byteArray);
                inputStream.close();
                outputStream.close();

                //Saves a smaller version into the new output stream
                outputStream = new FileOutputStream(smallVersion);
                BitmapFactory.decodeFile(image.getAbsolutePath()).compress(Bitmap.CompressFormat.JPEG, 50, outputStream);;
                outputStream.close();

            } catch (IOException ioE) {
                ioE.printStackTrace();
            }
        }*/

    }

    public static Bitmap bitmapSmaller(String filePath, int reqWidth, int reqHeight) {
        return decodeSampledBitmapFromString(filePath, reqWidth, reqHeight);
    }

    public static Bitmap bitmapSmaller(Resources resources, int resID, int reqWidth, int reqHeight) {
        return decodeSampledBitmapFromResource(resources, resID, reqWidth, reqHeight);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromString(String photo,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photo, options);
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources resources,
                                                          int resID, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resID, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resID, options);
    }

    public static String createImageFile(Activity a) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

        String imagePath = a.getFilesDir().getAbsolutePath() + "/JPEG_" + timeStamp + ".jpg";
        try {
            if (!new File(imagePath).createNewFile())
            	throw new IOException("Unable to create new image file");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }
}
