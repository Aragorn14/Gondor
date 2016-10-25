package com.scube.Gondor.Helpers.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.scube.Gondor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vashoka on 9/28/14.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                /* GCM message is of type "Bundle"
                Sample Bundle format received from GCM Server
                Bundle[{value={sparkk:first}, android.support.content.wakelockid=3, collapse_key=do_not_collapse, from=121149059617}]
                */
                Log.i(getString(R.string.GCM_intent), "GCM message @ " + SystemClock.elapsedRealtime());
                Log.i(getString(R.string.GCM_intent), "Bundle: " + extras.toString());

                String messageValue = extras.getString("value");
                Log.i(getString(R.string.GCM_intent), "Data: " + messageValue);

                if(messageValue != null) {
                    // Handle the sparkk node gcm message appropriately
                    handleGCMSparkkMessage(extras.getString("value"));
                }

                /*
                 * The chat notification will have the dialog message and dialog ID
                 * Send this as a notification through GCM notifier helper
                 * Sample Bundle: "Bundle[{user_id=3291498, from=121149059617, badge=1, message=metalsriks@gmail.com: hey,
                 * dialog_id=557324f26390d89d6b038309, android.support.content.wakelockid=2, collapse_key=event2848386}]"
                 */
                String message = extras.getString("message");
                String dialogId = extras.getString("dialog_id");
                if(message != null && dialogId != null) {
                    Log.i(getString(R.string.GCM_intent), "Sending Notification: " + message + " With Dialog ID: " + dialogId);
                    GcmNotifier gcmNotifier = new GcmNotifier(this);
                    gcmNotifier.sendNotification(message, dialogId);
                }

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void handleGCMSparkkMessage(String gcmMessageValue) {
        JSONObject gcmMessageJsonObj;

        try {
            gcmMessageJsonObj = new JSONObject(gcmMessageValue);

            // Returns json object tied to the name "sparkk", if value is not an object then returns null
            JSONObject sparkkJsonObj = gcmMessageJsonObj.optJSONObject("sparkk");
            if(sparkkJsonObj == null) {
                return;
            }
            Log.d(getString(R.string.GCM_intent), "Sparkk Specific GCM Data Value = "+ sparkkJsonObj.toString());

            // Grab the function name received in GCM message to route the handling
            String functionNameRequested = sparkkJsonObj.getString("funcName");
            Log.d(getString(R.string.GCM_intent), "Requested function to be invoked = "+functionNameRequested);

            // Grab any params that should be passed to the function. Returns null if params is not an array
            JSONArray functionParams = sparkkJsonObj.optJSONArray("params");

            if(functionNameRequested.equals("first")) {
                Log.d(getString(R.string.GCM_intent), "Sparkk GCM Received : FirstFunc");

                // Invoking custom function to handle the gcm request with parameters
                FirstFunc(functionParams);
            } else if(functionNameRequested.equals("second")) {
                Log.d(getString(R.string.GCM_intent), "Sparkk GCM : SecondFunc");

                // Invoking custom function without any parameters
                SecondFunc(functionParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(getString(R.string.GCM_intent), "Could not parse malformed json string "+ gcmMessageValue);
        }
    }

    private void FirstFunc(JSONArray params) {
        Log.d(getString(R.string.GCM_intent), "Bingo !!! GCM Function Invoked : FirstFunc");

        // Access the received parameters
        if(params == null) {
            Log.d(getString(R.string.GCM_intent), "GCM params array is empty");
        } else {
            try {
                if (params.length() > 0) {
                    for (Integer i = 0; i < params.length(); i++) {
                        JSONObject param = params.getJSONObject(i);
                        String name = param.getString("name"),
                               value = param.getString("value");

                        if(name.equals("myFirstParam")) {
                            Log.d(getString(R.string.GCM_intent), "first param received with name = "+name+" value = "+value);
                        } else if(name.equals("mySecondParam")) {
                            Log.d(getString(R.string.GCM_intent), "second param received with name = "+name+" value = "+value);
                        }

                    }
                }
            } catch(JSONException e) {
                Log.d(getString(R.string.GCM_intent), "Could not parse through gcm params array");
            }
        }
    }

    private void SecondFunc(JSONArray params) {
        Log.d(getString(R.string.GCM_intent), "Bingo !!! GCM Function Invoked : SecondFunc");

        // Access the received parameters
        if(params == null) {
            Log.d(getString(R.string.GCM_intent), "GCM params array is empty");
        } else {
            try {
                if (params.length() > 0) {
                    for (Integer i = 0; i < params.length(); i++) {
                        JSONObject param = params.getJSONObject(i);
                        String name = param.getString("name"),
                                value = param.getString("value");

                        if(name.equals("myFirstParam")) {
                            Log.d(getString(R.string.GCM_intent), "first param received with name = "+name+" value = "+value);
                        } else if(name.equals("mySecondParam")) {
                            Log.d(getString(R.string.GCM_intent), "second param received with name = "+name+" value = "+value);
                        }

                    }
                }
            } catch(JSONException e) {
                Log.d(getString(R.string.GCM_intent), "Could not parse through gcm params array");
            }
        }
    }
}
