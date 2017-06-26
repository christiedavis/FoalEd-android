package com.abc.foaled.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Brendan on 5/17/17.
 *
 * Used as a global class to check user's permissions
 */

public class PermissionsHelper {

    private PermissionsHelper(){}

	/**
	 *
	 * @param context The context that has requested the permissions
	 * @param permissions The permissions this context is requesting
	 * @return The list of permissions that this app does not have
	 */
    public static List<String> hasPermissions(Context context, String... permissions) {

	    List<String> toReturn = new ArrayList<>();
	    //Checks to see if the permissions we're requesting are in the manifest
	    try {
		    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
		    List<String> requestedPermissions = new ArrayList<>(Arrays.asList(packageInfo.requestedPermissions));

		    for (String s : permissions) {

			    //If the permission is listed in the manifest
			    if (requestedPermissions.contains(s)) {
				    //if the permission is not granted
				    if (ContextCompat.checkSelfPermission(context, s) == PackageManager.PERMISSION_DENIED)
				    	toReturn.add(s);
			    } else {
				    //else throw an error
				    Toast.makeText(context, "Error requesting permissions. Unable to access this feature", Toast.LENGTH_LONG).show();
				    throw new PackageManager.NameNotFoundException("The permission " + s + " does not exist in the manifest");
			    }
		    }

	    } catch (PackageManager.NameNotFoundException e) {
		    e.printStackTrace();
		    Log.e("Permissions", e.getMessage());
	    }
	    return toReturn;
    }


    public static void getPermissions(Activity activity, int requestCode, List<String> permissions) {
	    if (permissions.isEmpty()) {
		    Log.e("Permissions", "No permissions to request for");
		    return;
	    }
	    ActivityCompat.requestPermissions(activity, permissions.toArray(new String[]{}), requestCode);
    }

}
