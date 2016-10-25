package com.scube.Gondor.Helpers.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by vashoka on 9/28/14.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        String regId = intent.getExtras().getString("registration_id");

        if(regId != null && !regId.equals("")) {
            /* Now we can do what ever we want with the regId:
            * 1. send it to our server
            * 2. store it once successfuly registered on the server side */
            Log.d("GCM Broadcast Receiver", "Received GCM ID : "+regId);
        }
    }
}