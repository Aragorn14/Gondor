package com.scube.Gondor.Login.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.scube.Gondor.Helpers.gcm.GcmController;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Login.controllers.fragments.LoginFragment;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.WebViewResponse;

public class MainActivity extends FragmentActivity {

    String environment = "dev", domain;
    GcmController gcmController;
    Context context;
    SharedPreferences sharedPreferences;
    Intent goToHomeIntent;
    Boolean doLogout = false;
    Boolean resendEmail = false;
    Boolean deviceIdAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(getString(R.string.sp_user), Context.MODE_PRIVATE);

        Log.d(getString(R.string.MAIN_ACTIVITY), "On Create");
        if(getIntent().getExtras() !=null) {
            doLogout = getIntent().getExtras().getBoolean("doLogout");
            resendEmail = getIntent().getExtras().getBoolean("resendEmail");
        }

        // Check if the latest activity loaded was null / splash screen / main activity , do not take any action
        // Else, remove session because looks like app was killed by the user
        if(GlobalUtils.containsSharedPreference(context, getString(R.string.sp_nav), getString(R.string.sp_latest_activity))) {
            String latestActivity = GlobalUtils.getSharedPreference(context, getString(R.string.sp_nav), getString(R.string.sp_latest_activity));
            if(latestActivity == null || latestActivity.equals(context.getClass().getSimpleName()) || latestActivity.equals(MainActivity.class.getSimpleName())) {
                // TODO
            } else {
                // TODO
            }
        }

        // Save Activity
        GlobalUtils.addSharedPreference(context, getString(R.string.sp_nav), getString(R.string.sp_latest_activity), context.getClass().getSimpleName());

        // Save Default Location
        GlobalUtils.addSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location), getString(R.string.user_default_location));

        if(environment.equals("dev")) {
            domain = getString(R.string.geny_dev_domain);
        } else if(environment.equals("prd")) {
            domain = getString(R.string.openshift_prod_domain);
        }

        // Save domain
        GlobalUtils.addSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_domain), domain);

        // Go to login immediately
        navigateForward();

        // Gcm controller is invoked only if necessary
        String gcmRegId = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.gcm_registration_id));
        String scubeDeviceId = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id));
        if(gcmRegId.isEmpty() || scubeDeviceId.isEmpty()) {
            // Instantiate GCM
            gcmController = new GcmController(environment, domain, this);

            // Check device for Play Services APK.
            if (checkPlayServices(context)) {
                // If check succeeds, proceed with GCM registration.
                gcmController.start(context);
            } else {
                Log.i(getString(R.string.GCM), "No valid Google Play Services APK found.");
            }
        } else {
            Log.d(getString(R.string.GCM), "Scube Device Id and Gcm Reg Id found; so skipping Gcm Module");
            informLoginFragment(scubeDeviceId);
        }
    }

    /* This method should be invoked after all the mandatory network calls
     * are completed. Please add the tasks that you are waiting to be completed to the below list
     *
     * 1. GCM Registration
     * 2. Device Registration with Sparkk Node server
     */
    public void navigateForward() {
        if(GlobalUtils.containsSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session)) &&
                GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session)).equals(getString(R.string.user_session_true))) {
            if(resendEmail) {
                Log.d(getString(R.string.MAIN_ACTIVITY), "Forward Resend email validation to login fragment");
                FragmentManager fragmentManager = getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("loadAs", "standard");
                bundle.putBoolean("doLogout", doLogout);
                bundle.putBoolean("resendEmail", resendEmail);
                // Pass profile and profile object for list context
                LoginFragment loginFragment = new LoginFragment();
                loginFragment.setArguments(bundle);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, loginFragment, "LoginFragment");
                ft.commit();
//                getSupportFragmentManager().executePendingTransactions();
            } else {
                Log.d(getString(R.string.MAIN_ACTIVITY), "Session Found. Scube User Logged in. Navigate to Home");
                goToHomeIntent = new Intent(this, HomeActivity.class);
                goToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                goToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(goToHomeIntent);
                finish();
            }
        } else {
            Log.d(getString(R.string.MAIN_ACTIVITY), "Session Not Found. Load Login Fragment");
            FragmentManager fragmentManager = getSupportFragmentManager();

            Bundle bundle = new Bundle();
            bundle.putString("loadAs", "standard");
            bundle.putBoolean("doLogout", doLogout);
            bundle.putBoolean("resendEmail", resendEmail);
            // Pass profile and profile object for list context
            LoginFragment loginFragment = new LoginFragment();

            // Provide interface to callback when browser is ready
            loginFragment.TellMeWhenReady(new WebViewResponse.Listener<String>() {

                @Override
                public void onReady(String status) {
                    // If GCM has already notified Main Activity before web view was ready,
                    // then pick scubeId from shared preferences and pass it to webview
                    // Javascript listener
                    if(deviceIdAvailable) {
                        String scubeDeviceId = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id));
                        if(!scubeDeviceId.isEmpty()) {
                            informLoginFragment(scubeDeviceId);
                        }
                    }
                }
            });

            loginFragment.setArguments(bundle);
            //loginFragment.setTagName("LoginFragment");
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, loginFragment, "LoginFragment");
            ft.commit();
        }
    }

    public void informLoginFragment(String scubeDeviceId) {
        deviceIdAvailable = true;
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("LoginFragment");
        if(loginFragment != null) {
            loginFragment.sendDeviceIdToJavascript();
        }
    }

    // Login fragment will invoke chat login and pass mainactivity context
    // Upon successful login, chat login util will callback this function
    public void chatLoginStatusCallback(Boolean status) {
        // Scenarios
        // 1. Login success : good for us both scube and chat login was successful, take user to home
        // 2. Login failure : cant help, lets try logging him again when the requirement arrives. Take user to home
        if(status) {
            Log.d(getString(R.string.MAIN_ACTIVITY), "Success : Chat Login callback");
        } else {
            Log.d(getString(R.string.MAIN_ACTIVITY), "Failure : Chat Login callback");
        }
        navigateForward();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        context.getResources().getInteger(R.integer.play_services_resolution_request)).show();
            } else {
                Log.i(getString(R.string.GCM), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    // You need to do the Play Services APK check here too.
    // ensures that if the user returns to the running app through
    // some other means, such as through the back button, the check
    // is still performed
    @Override
    protected void onResume() {
        super.onResume();
        context = getApplicationContext();
        checkPlayServices(context);
    }

}
