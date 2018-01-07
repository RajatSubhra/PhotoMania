package io.rajat.sample.photo_mania.util;

import android.content.Context;
import android.net.ConnectivityManager;


public class NetworkUtility {

    /* Checking Network connection */
    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;

        } else {
            return false;
        }

    }
}