package com.nidoos.doocti.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Internet {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
