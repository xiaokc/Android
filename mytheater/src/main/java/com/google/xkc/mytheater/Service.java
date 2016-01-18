package com.google.xkc.mytheater;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by xkc on 1/18/16.
 */
public class Service {

    public static boolean hasNetwork(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || ! info.isConnected()){
            return false;
        }
        if (info.isRoaming()){
            return true;
        }
        return true;
    }
}
