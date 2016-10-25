package com.scube.Gondor.Helpers.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Created by vashoka on 9/3/15.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String regId = intent.getExtras().getString("registration_id");

        if(regId != null && !regId.equals("")) {
            /* Now we can do what ever we want with the regId:
            * 1. send it to our server
            * 2. store it once successfuly registered on the server side */
            Log.d("GCM Broadcast Receiver", "Received GCM ID : " + regId);
        }
    }
}
