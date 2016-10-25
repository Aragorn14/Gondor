package com.scube.Gondor.Util;

import android.content.Context;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.scube.Gondor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by vashoka on 7/5/15.
 */
public class Auth {

    public static JSONObject getLoginDetails(Context context) {
        JSONObject loginDetails = new JSONObject();

        // If session exists
        if(GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_session))) {
            String session = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_session));

            if(session.equals(context.getString(R.string.user_session_false))) {
                try {
                    loginDetails.put("status", context.getString(R.string.user_session_false));
                } catch (JSONException e) {
                    Log.d(context.getString(R.string.Login_Util), "Adding status false into Json Object response failed");
                    e.printStackTrace();
                }
                return loginDetails;
            }

            try {
                loginDetails.put("status", context.getString(R.string.user_session_true));
            } catch (JSONException e) {
                Log.d(context.getString(R.string.Login_Util), "Adding status true into Json Object response failed");
                e.printStackTrace();
            }

            // If exists, fetch userId and email Id and return to the requesting context
            if(GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.scube_id))) {
                Integer scubeId = GlobalUtils.getIntSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.scube_id));
                if(scubeId != null) {
                    try {
                        loginDetails.put("scubeId", scubeId);
                    } catch (JSONException e) {
                        Log.d(context.getString(R.string.Login_Util), "Adding scubeId into Json Object response failed");
                        e.printStackTrace();
                    }
                }
            }

            if(GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_id))) {
                String emailId = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_id));
                if(emailId != null) {
                    try {
                        loginDetails.put("emailId", emailId);
                    } catch (JSONException e) {
                        Log.d(context.getString(R.string.Login_Util), "Adding emailId into Json Object response failed");
                        e.printStackTrace();
                    }
                }
            }

            if(GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_first_name))) {
                String userFirstName = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_first_name));
                if(userFirstName != null) {
                    try {
                        loginDetails.put("userFirstName", userFirstName);
                    } catch (JSONException e) {
                        Log.d(context.getString(R.string.Login_Util), "Adding userFirstName into Json Object response failed");
                        e.printStackTrace();
                    }
                }
            }

            if(GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_image_url))) {
                String userImageUrl = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_image_url));
                if(userImageUrl != null) {
                    try {
                        loginDetails.put("userImageUrl", userImageUrl);
                    } catch (JSONException e) {
                        Log.d(context.getString(R.string.Login_Util), "Adding userImageUrl into Json Object response failed");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            try {
                loginDetails.put("status", context.getString(R.string.user_session_false));
            } catch (JSONException e) {
                Log.d(context.getString(R.string.Login_Util), "Adding status false into Json Object response failed");
                e.printStackTrace();
            }
        }

        return loginDetails;
    }

    // Signout of QB
    public static void logoutOfQb(Context context) {
        QBSettings.getInstance().fastConfigInit(context.getString(R.string.APP_ID), context.getString(R.string.AUTH_KEY), context.getString(R.string.AUTH_SECRET));
        if (!QBChatService.isInitialized()) {
            QBChatService.init(context);
        } else {
            Log.d(context.getString(R.string.CHAT_LOGIN_UTIL), "Chat is already initialized");
        }
        final QBChatService chatService = QBChatService.getInstance();
        boolean isLoggedIn = chatService.isLoggedIn();

        if (!isLoggedIn) {
            return;
        }
        chatService.logout(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                // success
                Log.d("Login Util", "User Logged out of QB");
                chatService.destroy();
            }

            @Override
            public void onError(final List list) {
                Log.e("Login Util", "ERROR logging out user from QB : " + list);
            }
        });
    }

    /*
        Return : Boolean
        Logged in : if session and user_id are present in shared preference (true)
        Logged out : All other scenarios (false)
     */
    public static boolean isLoggedIn(Context context) {
        if(GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_session))) {
            String session = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_session));

            if(session.equals(context.getString(R.string.user_session_false))) {
                return false;
            }

            if (GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_id))) {
                String userId = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_id));
                if (userId != null) {
                    return true;
                }
            }
        }

        return false;
    }
}
