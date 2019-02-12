package com.evartem.remsimon.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import timber.log.Timber;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.evartem.remsimon.TheApp.isInternetConnectionAvailable;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = "";
        if (intent != null) action = intent.getAction();

        if (action != null && action.equals(CONNECTIVITY_ACTION)) {
            try {
                isInternetConnectionAvailable = isInternetConnectionAvailable(context);
            } catch (Exception exception) {
                Timber.wtf(exception);
            }
        }
        Timber.i("ConnectivityChangeReceiver, isOnline: %s", isInternetConnectionAvailable);
    }

    private boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null)
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
