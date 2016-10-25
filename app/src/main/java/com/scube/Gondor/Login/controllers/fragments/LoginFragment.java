package com.scube.Gondor.Login.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.scube.Gondor.Chat.models.DataHolder;
import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Helpers.api.UserApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.models.Brand;
import com.scube.Gondor.Login.controllers.MainActivity;
import com.scube.Gondor.Login.models.UserProfile;
import com.scube.Gondor.R;
import com.scube.Gondor.UI.CircleTransform;
import com.scube.Gondor.Util.Auth;
import com.scube.Gondor.Util.ChatLogin;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;
import com.scube.Gondor.Util.Interfaces.WebViewResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginFragment extends NavigationFragment {
    protected WebView browser;
    protected FrameLayout browserPlaceHolder;
    private GestureDetector gestureDetector;
    Context context;
    Intent homeIntent, goToMainIntent;
    public WebViewResponse.Listener<String> webViewListener;


    Boolean doLogout = false;
    Boolean resendEmail = false;

    String domain = "", url_custom = "", url_to_load = "", deviceId = "";

    //This obj is used to send data from android to node as part of get
    //requests.
    JSONObject android_node_json_obj = new JSONObject();

    // Loaded as
    String loadedAs = "standard";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.activity_login, container, false);
        homeIntent = new Intent(getActivity(), HomeActivity.class);
        goToMainIntent  = new Intent(getActivity(), MainActivity.class);
        context = getActivity().getApplicationContext();
        loadedAs = "standard";

        //Populate the device id from device-SP used to send to node in url
        if(GlobalUtils.containsSharedPreference(context, getString(R.string.sp_device), getString(R.string.scube_device_id))) {
            deviceId = GlobalUtils.getSharedPreference(context, getString(R.string.sp_device), getString(R.string.scube_device_id));
            Log.d(getString(R.string.Login_Fragment), "Device id is " + deviceId);
        }
        //Populate the domain from user-sp
        if(GlobalUtils.containsSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_domain))) {
            domain = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_domain));
            Log.d(getString(R.string.Login_Fragment), "User Domain is " + domain);
        }

        // This will get the custom URL when called from an external link (ex: verify account link)
        Uri data = getActivity().getIntent().getData();
        try {
            if(data != null) {
                url_custom = data.toString();
                Log.d(getString(R.string.Login_Fragment), "Custom URL: " + url_custom);
            }
        } catch (Exception e) {
            Log.e("Exception: ",e.toString());
        }

        if (getActivity().getIntent().getExtras() != null) {
            doLogout = getActivity().getIntent().getExtras().getBoolean("doLogout");
            if (doLogout) {
                // Logout of Chat
                Auth.logoutOfQb(context);
                GlobalUtils.removeSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session));
            }
            resendEmail = getActivity().getIntent().getExtras().getBoolean("resendEmail");
        }

        // Check the session. If we have session, go to home page
        if(GlobalUtils.containsSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session))) {
            String session = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session));
            Log.d(getString(R.string.Login_Fragment), "Session is " + session);

            if(session.equals(getString(R.string.user_session_true)) && !resendEmail) {
                Log.d(getString(R.string.Login_Fragment), "Session exists, App Home view loaded.");
                navigateToHome();
            } else {
                Log.d(getString(R.string.Login_Fragment), "Session not true. Loading login webview");
                // Now initialize the UI
                initializeUI(v);
            }

        } else {
            Log.d(getString(R.string.Login_Fragment), "No session. Loading login webview");
            // Now initialize the UI
            initializeUI(v);
        }

        // Add tag for accessibility
        v.setTag("LoginFragment");

        return v;
    }

    public void TellMeWhenReady(WebViewResponse.Listener<String> webViewListenerInterface) {
        webViewListener = webViewListenerInterface;
    }

    // This is used to initialize (or reinitialize in case of orientation change) the web page
    protected void initializeUI(View v) {
        browserPlaceHolder = (FrameLayout) v.findViewById(R.id.webViewPlaceholder);
        if (browser == null) {
            // Create the webview
            browser = new WebView(getActivity());
            WebSettings browserSettings = browser.getSettings();
            browserSettings.setJavaScriptEnabled(true);
            browserSettings.setSupportZoom(true);
            browserSettings.setBuiltInZoomControls(true);
            browserSettings.setLoadsImagesAutomatically(true);
            browserSettings.setLoadWithOverviewMode(true);
            browserSettings.setUseWideViewPort(true);
            browser.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            browser.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            browser.setScrollbarFadingEnabled(true);
            // Load the URLs inside the WebView, not in the external web browser
            //browser.setWebViewClient(new WebViewClient());
            browser.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    if(webViewListener != null) {
                        // Since browser is ready, notify listeners via interface
                        webViewListener.onReady("ready");
                    }
                }
            });
            browser.addJavascriptInterface(new JavascriptInterface(), getString(R.string.javascript_interface));
            // for debugging, this will handle the console.log() in javascript
            browser.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage cm) {
                    Log.d(getString(R.string.Console_Log_From_Node), cm.message() + " #" + cm.lineNumber() + " --" + cm.sourceId());
                    return true;
                }
            });


            // Load the appropriate URL
            try {
                if(!url_custom.equals("")) {
                    // Custom URL (example intent coming from email validation)
                    url_to_load = url_custom;
                } else {
                    // Check if it is a resend email route
                    if(resendEmail) {
                        url_to_load = domain + getString(R.string.user_endpoint_resend);
                    } else {
                        // Either load index url or logout url
                        url_to_load = (doLogout) ? domain + getString(R.string.user_endpoint_logout) : domain + getString(R.string.user_endpoint_signup);
                    }

                }

                //Append the device Id
                android_node_json_obj.put("deviceId", deviceId);

                // Append the domain as genymotion if using dev environment
                if (domain.equals(getString(R.string.geny_dev_domain))) {
                    android_node_json_obj.put("domain","genymotion");
                }

                // Pass deviceid and domain data to node via query params
                url_to_load += "?consumer_app_data=" + android_node_json_obj.toString();

                Log.d(getString(R.string.Login_Fragment), "Webview now loads URL: " + url_to_load);
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl(url_to_load);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Attach the WebView to its placeholder
        browserPlaceHolder.addView(browser);
    }

    public void sendDeviceIdToJavascript() {
        Log.d(getString(R.string.Login_Fragment), "Sending Device id " + deviceId + " to Javascript");
        if (browser != null) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    String scubeDeviceId = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id));
                    browser.loadUrl("javascript:updateDeviceId(\""+ scubeDeviceId +"\")");
                }
            });
        }
    }

    // We use this function to go to the home page after we are done with login module webview
    public class JavascriptInterface {

        JavascriptInterface() {
        }

        /*
            loginType can be one among
            1. "namma"
            2. "Facebook"
            3. "Google"
         */
        @android.webkit.JavascriptInterface
        public void goToHomePageAfterAuth(final String userId, String emailId, String locationId, String loginType) {
            Log.d(getString(R.string.Login_Activity_JS_Interface), "Now we will Load the app with login-success state. User id" + userId + " User email" + emailId);

            // Write the session variable into shared preferences before exiting
            GlobalUtils.addSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session), getString(R.string.user_session_true));

            // Add location id to shared preferences
            GlobalUtils.addSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location), locationId);

            // Save user email Id
            GlobalUtils.addSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_id), emailId);

            // Save user login type : Use this to display facebook/google/namma icon in side drawer for user to know which account he is using for login
            GlobalUtils.addSharedPreference(context, getString(R.string.sp_user), getString(R.string.login_type), loginType);

            try {
                final Integer userIdInt = Integer.parseInt(userId);
                GlobalUtils.addIntSharedPreference(context, getString(R.string.sp_user), getString(R.string.scube_id), userIdInt);

                // We GET the full user profile here
                final UserApiHelper userApiHelper = new UserApiHelper(context, "LoginFragment");
                userApiHelper.getProfileByScubeId(userIdInt, new ApiResponse.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject userProfileObj) {
                        /* Get user profile info
                           JSON-API format
                            For success:
                            {
                               username: "maithu31@gmail.com",
                               firstname: null,
                               lastname: null,
                               gender: "Male",
                               dob: "1988-12-12T08:00:00.000Z",
                               location: "milpitas",
                               phone: null,
                               imageurl: null,
                               qb_user_id: 1000
                               user_type: "namma"
                            }
                            For failure:
                            {
                               "user_status" : -1,
                               "user_reason" : "failure
                            }
                        */
                        UserProfile userProfile = new UserProfile(userProfileObj, context);
                        String userFirstName = userProfile.getFirstName();
                        String userImageUrl = userProfile.getImageUrl();
                        String userType = userProfile.getUserType();

                        // Write the first name into shared preferences
                        GlobalUtils.addSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_first_name), userFirstName);
                        // Write the user image url into shared preferences
                        GlobalUtils.addSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_image_url), userImageUrl);

                        // In case of Social login, we need to send the Device Id to server
                        if (userType.equalsIgnoreCase("facebook") || userType.equalsIgnoreCase("google+")) {
                            JSONObject postData = new JSONObject();
                            if (GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id))) {
                                Integer deviceId = Integer.parseInt(GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id)));

                                try {
                                    postData.put("device_id", deviceId);
                                    postData.put("user_id", userIdInt);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.d(getString(R.string.Login_Activity_JS_Interface), "Sending Post data:" + postData);
                                // Call the API helper to POST the device id data
                                UserApiHelper userApiHelper1 = new UserApiHelper(context, "LoginFragment");
                                userApiHelper1.postDeviceId(postData, new ApiResponse.Listener<Boolean>() {

                                    @Override
                                    public void onResponse(Boolean response) {
                                        Log.d(getString(R.string.Login_Activity_JS_Interface), "Post Device Id Response: " + response);
                                    }
                                });
                            } else {
                                Log.e(getString(R.string.Login_Activity_JS_Interface), "Can't load device Id, not stored in shared pref");
                            }
                        }
                    }
                });

            } catch (NumberFormatException e) {
                System.out.println("Could not parse userId to integer ");
                e.printStackTrace();
                navigateToHome();
                return;
            }


            // Chat Login Util will navigate to home upon successful QB Login
            ChatLogin.loginToChat(context, null, getActivity());
        }

        @android.webkit.JavascriptInterface
        public void goToHomePageAfterSkip() {
            Log.d(getString(R.string.Login_Activity_JS_Interface), "User pressed Skip button. We now navigate to the home page");
            navigateToHome();
        }

        @android.webkit.JavascriptInterface
        public void goToPreviousPageAfterResendingEmail() {
            Log.d(getString(R.string.Login_Activity_JS_Interface), "Email Validation successful! We go back to the previous screen");
            navigateToHome();
        }

        /*
            Will be invoked by login/signup web view on load/redirect to fetch deviceId from android if present
            If not present right away, wait for GCM to complete and then send the device ID
         */
        @android.webkit.JavascriptInterface
        public void getDeviceId() {
            Log.d(getString(R.string.Login_Fragment), "Get DeviceId request received from webview login/signup onload/redirect");
            String scubeDeviceId = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_device), context.getString(R.string.scube_device_id));
            if(!scubeDeviceId.isEmpty()) {
                sendDeviceIdToJavascript();
            }
        }
    }

    public void navigateToHome() {
        // If standard load, then load the next activity and close login
        if(loadedAs.equals("standard")) {
            // Leave the activity and go to the Deals activity
            startActivity(homeIntent);
            Log.d(getString(R.string.Login_Fragment), "Login Skipped by User / Session Exists : App Home view loaded.");
            getActivity().finish();
        } else if(loadedAs.equals("intermediate")) {
            // If intermediate, then just close the login activity to go back to the previous screen
            Log.d(getString(R.string.Login_Fragment), "Intermediate Login Skipped by User : Navigating back");
            navigateBack();
        }
    }

    public void navigateBack() {
        getActivity().finish();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        if (browser != null) {
            browser.saveState(outState);
        }
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState)
//    {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (browser != null) {
//            // Restore the state of the WebView
//            browser.restoreState(savedInstanceState);
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (browser != null) {
//            if ((keyCode == KeyEvent.KEYCODE_BACK) && browser.canGoBack()) {
//                browser.goBack();
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
