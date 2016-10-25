package com.scube.Gondor.Helpers.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.scube.Gondor.Helpers.api.GcmApiHelper;
import com.scube.Gondor.Login.controllers.MainActivity;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;
import com.scube.Gondor.Util.PasswordUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vashoka on 10/19/14.
 */
public class GcmController {
    // Global private
    private String environment, domain;
    private Activity creator;

    // Global public
    Context context;

    // Shared Preferences
    private SharedPreferences sharedPreferences;

    // GCM
    String gcmRegId;
    GoogleCloudMessaging gcm;

    public GcmController(String environment, String domain, Activity creator) {

        this.environment = environment;
        this.domain = domain;
        // Activity/Class that ceates an instance of the GCMController.
        // Required to fire the callback if necessary
        this.creator = creator;
    }

    public void start(Context context) {
        this.context = context;
        gcmRegId = getRegistrationId(context);
        gcm = GoogleCloudMessaging.getInstance(context.getApplicationContext());

        if(gcmRegId.isEmpty()) {
            registerInBackground();
        } else {
            // Register the device
            this.sendRegistrationIdToBackend(gcmRegId);
        }
    }

    public JSONObject getDeviceData() {
        String userAgent = System.getProperty("http.agent");
        JSONObject regData = new JSONObject();

        Log.i(context.getString(R.string.GCM), "UserAgent string = " + userAgent);

        // Useragent regex pattern to parse the device OS version and Device Variant
        // Examples:
        // UA : Dalvik/1.6.0 (Linux; U; Android 4.2.2; Google Galaxy Nexus - 4.2.2 - API 17 - 720x1280 Build/JDQ39E)
        //      group(1) : Android 4.2.2
        //      group(5) : Google Galaxy Nexus
        //
        // UA : Dalvik/1.6.0 (Linux; U; Android 4.4.2; SAMSUNG-SGH-I747 Build/KOT49H)
        //      group(1) : Android 4.4.2
        //      group(5) : SAMSUNG-SGH-I747
        //
        String pattern = "(Android ([0-9]\\.[0-9](\\.[0-9])?))(;\\s)([a-zA-Z0-9-\\s]*((?=\\sBuild)|(?=\\s-\\s)))";
        Pattern regEx = Pattern.compile(pattern);

        // Find instance of pattern matches
        Matcher m = regEx.matcher(userAgent);
        if (m.find()) {
            // Fetch the Device IMEI number
            String imeiNumber = this.getIMEINumber(context);
            if(imeiNumber.equals("000000000000000")) {
//                imeiNumber = "123456123090090";
                if (GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.geny_imei))) {
                    imeiNumber = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.geny_imei));
                } else {
                    //imeiNumber = "123456123090090";
                    imeiNumber = PasswordUtils.randomString(15);
                    GlobalUtils.addSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.geny_imei), imeiNumber);
                }
                Log.d(context.getString(R.string.GCM), "Genymotion IMEI: " + imeiNumber);
            }
            try {
                // Add IMEI number to global shared preferences.
                GlobalUtils.addSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.imei), imeiNumber);

                // Populate Post Data
                // TO DO : Dynamic value for device type
                regData.put("IMEI", imeiNumber);
                regData.put("deviceType", "Cellular Device 3G+");
                regData.put("deviceVariant", m.group(5));
                regData.put("deviceOs", m.group(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(context.getString(R.string.GCM), "User agent string parsing could not match the pattern for device OS & Variant");
        }
        return regData;
    }

    public static String getIMEINumber(Context context) {
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }

    /**
     * GCM HELPERS
     */

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(context.getString(R.string.gcm_registration_id), "");
        if (registrationId.isEmpty()) {
            Log.i(context.getString(R.string.GCM), "Registration ID not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(context.getString(R.string.app_version), Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(context.getString(R.string.GCM), "App version has changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(context.getString(R.string.sp_nav),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    if(environment.equals("dev")) {
                        gcmRegId = gcm.register(context.getString(R.string.gcm_dev_sender_id));
                    } else if(environment.equals("prd")) {
                        gcmRegId = gcm.register(context.getString(R.string.gcm_prd_sender_id));
                    }

                    try
                    {
                        // Leave some time here for the register to be
                        // registered before going to the next line
                        Thread.sleep(2000);   // Set this timing based on trial.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    msg = "Device registered, registration ID=" + gcmRegId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend(gcmRegId);

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, gcmRegId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(context.getString(R.string.GCM), msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String gcmRegId) {
        // Get the device details for registration with Node
        JSONObject deviceData = this.getDeviceData();
        Log.i(context.getString(R.string.GCM), "Fetched device Data " + deviceData);

        // Add GCM Reg ID to the post data
        try {
            deviceData.put("gcmRegId", gcmRegId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GcmApiHelper gcmApiHelper = new GcmApiHelper(context);
        gcmApiHelper.postGcmRegistration(deviceData, new ApiResponse.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject reader) {
                try {
                    Log.d(context.getString(R.string.GCM), "deviceIdJson String = "+ reader);

                    // Save the device ID in shared preferences
                    // json format received from Node : [{"device_id":18}]
                    String deviceId = reader.getString("device_id");
                    if(deviceId != null && !deviceId.isEmpty()) {
                        storeDeviceId(reader.getString("device_id"));
                    } else {
                        Log.d(context.getString(R.string.GCM), "device_id data not found in registration response json");
                    }
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
            }
        });
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param gcmRegId GCM registration ID
     */
    private void storeRegistrationId(Context context, String gcmRegId) {
        int appVersion = getAppVersion(context);
        Log.i(context.getString(R.string.GCM), "Saving regId on app version " + appVersion);

        GlobalUtils.addSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.gcm_registration_id), gcmRegId);
        GlobalUtils.addIntSharedPreference(context, context.getString(R.string.sp_nav), context.getString(R.string.app_version), appVersion);
    }

    /**
     * Stores the device ID
     * {@code SharedPreferences}.
     *
     * @param deviceId device ID
     */
    private void storeDeviceId(String deviceId) {
        Log.i(context.getString(R.string.GCM), "Saving deviceId " + deviceId);

        GlobalUtils.addSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id), deviceId);

        // Move away from the splash screen to the main activity,
        // GCM and Node device registration is complete>
        Log.i(context.getString(R.string.GCM), "GCM and Node registration complete. Navigating into Main Activity");
        MainActivity mainActivity = (MainActivity) creator;
        mainActivity.informLoginFragment(deviceId);
    }

    public String getDeviceId() {
        String deviceId = "";

        if(GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id))) {
            deviceId = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id));
        }

        return deviceId;
    }
}
