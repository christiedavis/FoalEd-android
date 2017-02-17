package com.abc.foaled.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.abc.foaled.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by christie on 17/02/17.
 */

public class ImageHelper {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createPlaceholderImageFile(InputStream inputStream) {
        //The directory the images are saved in
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
        }
    }
}
