package com.scube.Gondor.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.server.BaseService;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.scube.Gondor.Chat.models.ChatNotificationManager;
import com.scube.Gondor.Chat.models.DataHolder;
import com.scube.Gondor.Helpers.api.UserApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Login.controllers.MainActivity;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatLogin {

    private static String USER_LOGIN;
    private static String USER_PASSWORD;

    static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;

    private static QBChatService chatService;
    private static Context chatContext;
    static String dialogId;
    private static Activity thisActivity;
    
    public static void loginToChat(Context context, String requestedDialogId, Activity activity) {
        chatContext = context;
        dialogId = requestedDialogId;
        thisActivity = activity;

        Log.d(chatContext.getString(R.string.Chat_Login_Util), "Entered Chat Login");

        // Chat Login Activity entered from Notification bar. Populate proper Dialog Id
        if (dialogId != null) {
            Log.d(chatContext.getString(R.string.Chat_Login_Util), "Received Chat Login request from notification bar. DialogID: " + dialogId);

            // Notification handling: clear dialog IDs from the maps.
            ChatNotificationManager.getChatNotificationManager().removeStringFromMaps(dialogId);
        }

        // Here we get the chatLogin flag. If it is set to auto, we just propagate the user login;
        // If not, then it is for debugging purpose and we provide an intermediate activity to feed in the username

        JSONObject loginDetails = Auth.getLoginDetails(chatContext);
        try {
            if (loginDetails.getString("status").equals(chatContext.getString(R.string.user_session_true))) {
                String emailId = loginDetails.getString("emailId");
                if(emailId != null) {
                    USER_LOGIN = emailId;
                    USER_PASSWORD = USER_LOGIN;
                    Log.d(chatContext.getString(R.string.Chat_Login_Util), "User Logging into chat with emailId = "+USER_LOGIN+" & password = "+USER_PASSWORD);

                    // Initialize Chat
                    QBChatService.setDebugEnabled(true);
                    QBSettings.getInstance().fastConfigInit(chatContext.getString(R.string.APP_ID), chatContext.getString(R.string.AUTH_KEY), chatContext.getString(R.string.AUTH_SECRET));
                    QBSettings.getInstance().setLogLevel(LogLevel.NOTHING);
                    if (!QBChatService.isInitialized()) {
                        QBChatService.init(context);
                    } else {
                        Log.d(chatContext.getString(R.string.Chat_Login_Util), "Chat is already initialized");
                    }
                    chatService = QBChatService.getInstance();

                    // Init Chat Session
                    if (!chatService.isLoggedIn()) {
                        // Initialize the session
                        initQBSession(USER_LOGIN, USER_PASSWORD);
                    } else {
                        Log.d(chatContext.getString(R.string.Chat_Login_Util), "Already logged into chat.");
                        afterChatLogin();
                    }
                }
            } else if(loginDetails.getString("status").equals(chatContext.getString(R.string.user_session_false))) {
                notifyLoginStatus(false);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    // Create QuickBlox session
    public static void initQBSession(final String uName, final String uPassword) {
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                Log.d(chatContext.getString(R.string.Chat_Login_Util), "Session Created. Token: " + qbSession.getToken());
                try {
                    Date expirationDate = BaseService.getBaseService().getTokenExpirationDate();
                    Log.d(chatContext.getString(R.string.Chat_Login_Util), "Session Expiration: " + expirationDate);
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                signInUser(uName, uPassword);
            }

            @Override
            public void onError(List<String> errors) {
                // print errors that came from server
                DialogUtils.showLong(chatContext, errors.get(0));
                Log.e(chatContext.getString(R.string.Chat_Login_Util), "Session Creation Error: " + errors.get(0));
                notifyLoginStatus(false);
            }
        });
    }

    // Get all the users' details
    public static void getAllUsers() {
        QBUsers.getUsers(null, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                DataHolder.getDataHolder().setQbUsersList(qbUsers);
                Log.d(chatContext.getString(R.string.Chat_Login_Util), "Users: " + qbUsers);
            }

            @Override
            public void onError(List<String> errors) {
                DialogUtils.showLong(chatContext, errors.get(0));
                Log.e(chatContext.getString(R.string.Chat_Login_Util), "Users not found. Error: " + errors.get(0));
                notifyLoginStatus(false);
            }
        });
    }

    // Sign into application with user details
    public static void signInUser(final String userName, final String password) {
        QBUser qbUser = new QBUser(userName, password);
        QBUsers.signIn(qbUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

                Log.d(chatContext.getString(R.string.Chat_Login_Util), "Successful Login!");
                DataHolder.getDataHolder().setSignInQbUser(qbUser);
                DataHolder.getDataHolder().setSignInUserPassword(password);
                DialogUtils.showLong(chatContext, "User was successfully signed in");
                Log.d(chatContext.getString(R.string.Chat_Login_Util), String.valueOf(qbUser));

                chatServiceLogin(qbUser);
            }

            @Override
            public void onError(List<String> errors) {
                DialogUtils.show(chatContext, errors.get(0));
                Log.d(chatContext.getString(R.string.Chat_Login_Util), "Login failed. Reason: " + errors.get(0));
                Log.d(chatContext.getString(R.string.Chat_Login_Util), "Trying to signup. . .");
                signUpUser(userName, password);
            }
        });
    }

    // Sign Up a new user
    public static void signUpUser(String userName, final String password) {
        QBUser qbUser = new QBUser();
        qbUser.setLogin(userName);
        qbUser.setPassword(password);
        QBUsers.signUpSignInTask(qbUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Log.d(chatContext.getString(R.string.Chat_Login_Util), "Successful Signup!");
                Log.d(chatContext.getString(R.string.Chat_Login_Util), String.valueOf(qbUser));
                DataHolder.getDataHolder().addQbUserToList(qbUser);
                DataHolder.getDataHolder().setSignInQbUser(qbUser);
                DataHolder.getDataHolder().setSignInUserPassword(password);

                chatServiceLogin(qbUser);
            }

            @Override
            public void onError(List<String> strings) {
                DialogUtils.showLong(chatContext, strings.get(0));
                Log.e(chatContext.getString(R.string.Chat_Login_Util), "Signup failed. Reason: " + strings.get(0));
                notifyLoginStatus(false);
            }
        });
    }

    // Signout of QB
    public static void signOutUsers(){
        QBUsers.signOut(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                Log.d(chatContext.getString(R.string.Chat_Login_Util), "Users Logged out");
                DialogUtils.showLong(chatContext, "Users Logged out");
                DataHolder.getDataHolder().setSignInQbUser(null);
            }

            @Override
            public void onError(List list) {
                Log.e(chatContext.getString(R.string.Chat_Login_Util), "Logout failed");
                DialogUtils.showLong(chatContext, list.get(0).toString());
                notifyLoginStatus(false);
            }
        });
    }

    // After user has been logged in, initialize chat login
    private static void chatServiceLogin(final QBUser user){

        // If user is not subscribed to push notifications, subscribe him/her
        subscribeToQBPushNotifications();

        Log.d(chatContext.getString(R.string.Chat_Login_Util), "Login to chat Start. User: " + user);
        chatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                Log.d(chatContext.getString(R.string.Chat_Login_Util), "Login to chat successful");
                afterChatLogin();
            }

            @Override
            public void onError(List errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(chatContext);
                dialog.setMessage("SCUBE_CHAT_LOGIN: " + errors).create().show();
                Log.e(chatContext.getString(R.string.Chat_Login_Util), "Couldn't login to chat. Errors: " + errors);
                notifyLoginStatus(false);
            }
        });
    }

    // Subscribe to Push Notifications
    public static void subscribeToQBPushNotifications() {
        if(GlobalUtils.containsSharedPreference(chatContext, chatContext.getString(R.string.sp_device), chatContext.getString(R.string.scube_device_id))) {
            String deviceId = GlobalUtils.getSharedPreference(chatContext, chatContext.getString(R.string.sp_device), chatContext.getString(R.string.scube_device_id));

            if(GlobalUtils.containsSharedPreference(chatContext, chatContext.getString(R.string.sp_device), chatContext.getString(R.string.gcm_registration_id))) {
                String gcmRegId = GlobalUtils.getSharedPreference(chatContext, chatContext.getString(R.string.sp_device), chatContext.getString(R.string.gcm_registration_id));

                QBMessages.subscribeToPushNotificationsTask(gcmRegId, deviceId, QBEnvironment.DEVELOPMENT, new QBEntityCallbackImpl<ArrayList<QBSubscription>>() {
                    @Override
                    public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                        Log.d("QB_GCM", "Subscribed to QB push notifications");
                    }

                    @Override
                    public void onError(List<String> errors) {
                        Log.e("QB_GCM", "Failed to subscribe to QB push notifications: " + errors);
                    }
                });
            }
        }
    }

    public static void afterChatLogin() {
        SmackConfiguration.DEBUG_ENABLED = false;

        // Start sending presences
        try {
            chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);

        } catch (SmackException.NotLoggedInException e) {
            Log.e(chatContext.getString(R.string.Chat_Login_Util), "Autopresence not sent!");
            e.printStackTrace();
        }

        updateUserProfile();

        // Callback the Login requester
        Log.d(chatContext.getString(R.string.Chat_Login_Util), "Callback the Login requester");
        notifyLoginStatus(true);
    }

    private static void notifyLoginStatus(Boolean status) {

        if(thisActivity.getClass().getName().equals(HomeActivity.class.getName())) {
            HomeActivity requestingActivity = (HomeActivity) thisActivity;
            requestingActivity.chatLoginStatusCallback(status);
//        } else if(thisActivity.getClass().getName().equals(ScubitsActivity.class.getName())) {
//            ScubitsActivity requestingActivity = (ScubitsActivity) thisActivity;
//            requestingActivity.chatLoginStatusCallback(status);
        } else {
            MainActivity requestingActivity = (MainActivity) thisActivity;
            requestingActivity.chatLoginStatusCallback(status);
        }
    }

    // After chat login, we need to update the user profile with the user's chat ID
    private static void updateUserProfile() {
        try {
            Integer qbUserId = DataHolder.getDataHolder().getSignInUserId();
            Integer userId = Auth.getLoginDetails(chatContext).getInt("scubeId");

            // Create POST JSON object
            JSONObject updateUserProfileObject = new JSONObject();
            updateUserProfileObject.put("user_id", userId);
            updateUserProfileObject.put("first_name",    JSONObject.NULL);
            updateUserProfileObject.put("last_name",     JSONObject.NULL);
            updateUserProfileObject.put("gender",        JSONObject.NULL);
            updateUserProfileObject.put("date_of_birth", JSONObject.NULL);
            updateUserProfileObject.put("location",      JSONObject.NULL);
            updateUserProfileObject.put("phone_num",     JSONObject.NULL);
            updateUserProfileObject.put("image_url",     JSONObject.NULL);
            updateUserProfileObject.put("qb_user_id",    qbUserId);

            // Chat login util can be invoked from any fragment / activity, so passing empty fragment tag name
            UserApiHelper userApiHelper = new UserApiHelper(thisActivity, "");
            userApiHelper.updateProfile(updateUserProfileObject, new ApiResponse.Listener<Boolean>() {

                @Override
                public void onResponse(Boolean response) {
                    if (response) {
                        Log.d(chatContext.getString(R.string.Chat_Login_Util), "User profile updated successfully with qbuser ID");
                    } else {
                        Log.d(chatContext.getString(R.string.Chat_Login_Util), "User profile update request failed to update with qbuser ID");
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();

            // Callback the Login requester
            notifyLoginStatus(false);
        }
    }
}