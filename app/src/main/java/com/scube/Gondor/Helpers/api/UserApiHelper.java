package com.scube.Gondor.Helpers.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.scube.Gondor.Core.controllers.AppController;
import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vashoka on 7/18/15.
 */
public class UserApiHelper {

    public Context context;
    private String fragmentTag;

    public UserApiHelper(Context context, String fragmentTag) {
        this.context = context;
        this.fragmentTag = fragmentTag;
    }

    /*
        Success : returns JSON object with all the info from user profile
        Failure : returns empty JSON object
     */
    public void getProfileByScubeId(final Integer scubeId, ApiResponse.Listener<JSONObject> apiListener) {
        final ApiResponse.Listener<JSONObject> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.user_profile);

        // Add user id to fetch from
        apiEndPoint += "?user_id="+scubeId.toString()+"&q_case=1";

        Log.d(context.getString(R.string.User_Api_Helper), apiEndPoint);

        // Create Volley request to Get user profile information
        JsonObjectRequest getUserProfile = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
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
                            }
                            For failure:
                            {
                               "user_status" : -1,
                               "user_reason" : "failure
                            }
                        */
                        try {
                            Log.d(context.getString(R.string.User_Api_Helper), response.toString());
                            if(response.has("user_status")) {
                                if(response.getInt("user_status") == -1) {
                                    Log.d(context.getString(R.string.User_Api_Helper), "Something went wrong. Failed to fetch user profile by Scube Id : "+ scubeId +". Error message : "+response.getString("user_reason"));
                                    // On receiving end check for : response.toString().equals("{}") to indicate failure response;
                                    mListener.onResponse(new JSONObject());
                                }
                            } else {
                                // Success case
                                Log.d(context.getString(R.string.User_Api_Helper), "Successfully fetched user profile info for scube user id : "+scubeId);
                                mListener.onResponse(response);
                            }
                        } catch(JSONException e) {
                            // On receiving end check for : response.toString().equals("{}") to indicate failure response;
                            mListener.onResponse(new JSONObject());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Check if the calling fragment is still in the stack or removed.
                        // TODO : Support for dynamic activity. Currently all fragments are loaded from home, so its not an issue.
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.User_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "User GET profile by Scube Id JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getUserProfile);
    }

    /*
    Success : returns JSON object with all the info from user profile
    Failure : returns empty JSON object
    */
    public void getProfileByQBUserId(final Integer qbUserId, ApiResponse.Listener<JSONObject> apiListener) {
        final ApiResponse.Listener<JSONObject> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.user_profile);

        // Add user id to fetch from
        apiEndPoint += "?qb_user_id="+qbUserId.toString()+"&q_case=2";

        Log.d(context.getString(R.string.User_Api_Helper), apiEndPoint);

        // Create Volley request to Get user profile information
        JsonObjectRequest getUserProfile = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
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
                            }
                            For failure:
                            {
                               "user_status" : -1,
                               "user_reason" : "failure
                            }
                        */
                        try {
                            Log.d(context.getString(R.string.User_Api_Helper), response.toString());
                            if(response.has("user_status")) {
                                if(response.getInt("user_status") == -1) {
                                    Log.d(context.getString(R.string.User_Api_Helper), "Something went wrong. Failed to fetch user profile for qb user id : "+ qbUserId +". Error message : "+response.getString("user_reason"));
                                    // On receiving end check for : response.toString().equals("{}") to indicate failure response;
                                    mListener.onResponse(new JSONObject());
                                }
                            } else {
                                // Success case
                                Log.d(context.getString(R.string.User_Api_Helper), "Successfully fetched user profile info for qb user user id : "+qbUserId);
                                mListener.onResponse(response);
                            }
                        } catch(JSONException e) {
                            // On receiving end check for : response.toString().equals("{}") to indicate failure response;
                            mListener.onResponse(new JSONObject());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Check if the calling fragment is still in the stack or removed.
                        // TODO : Support for dynamic activity. Currently all fragments are loaded from home, so its not an issue.
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.User_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "User GET profile By QB User Id JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getUserProfile);
    }

    /*
    Success : returns Boolean true
    Failure : returns Boolean false
    */
    public void updateProfile(JSONObject userProfileData, ApiResponse.Listener<Boolean> apiListener) {
        final ApiResponse.Listener<Boolean> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.user_profile);

        Log.d(context.getString(R.string.User_Api_Helper), apiEndPoint);

        // Create Volley request to Get user profile information
        JsonObjectRequest getUserProfile = new JsonObjectRequest(apiEndPoint, userProfileData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Get user profile info
                           JSON-API format
                            For Success:
                            {
                                "user_status": 1,
                                "user_reason": "success"
                            }

                            For Failure:
                            {
                               "user_status" : -1,
                               "user_reason" : "failure"
                            }
                        */
                        try {
                            Log.d(context.getString(R.string.User_Api_Helper), response.toString());
                            if(response.has("user_status")) {
                                if(response.getInt("user_status") == 1) {
                                    Log.d(context.getString(R.string.User_Api_Helper), "User profile successfully updated");
                                    // On receiving end check for : response.toString().equals("{}") to indicate failure response;
                                    mListener.onResponse(true);
                                } else {
                                    Log.d(context.getString(R.string.User_Api_Helper), "Something went wrong. Failed to fetch user profile. Error message : "+response.getString("user_reason"));
                                    // On receiving end check for : response.toString().equals("{}") to indicate failure response;
                                    mListener.onResponse(false);
                                }
                            } else {
                                // Success case
                                Log.d(context.getString(R.string.User_Api_Helper), "user_status not found in response");
                                mListener.onResponse(false);
                            }
                        } catch(JSONException e) {
                            // On receiving end check for : response.toString().equals("{}") to indicate failure response;
                            mListener.onResponse(false);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Check if the calling fragment is still in the stack or removed.
                        // TODO : Support for dynamic activity. Currently all fragments are loaded from home, so its not an issue.
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.User_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "User Update profile JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getUserProfile);
    }

    /*
    Success : returns Boolean true
    Failure : returns Boolean false
    */
    public void postDeviceId(JSONObject postDeviceIdData, ApiResponse.Listener<Boolean> apiListener) {
        final ApiResponse.Listener<Boolean> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.post_device_id);

        Log.d(context.getString(R.string.User_Api_Helper), apiEndPoint);

        // Create Volley request to Add scubit
        JsonObjectRequest postDeviceIdReq = new JsonObjectRequest(apiEndPoint, postDeviceIdData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Post Device Id response
                               JSON-API format
                                SUCCESS CASE
                            { 'success' : "Device Id Update successful" }
                                FAILURE CASE
                            { 'failure' : "Device Id update failure" }
                        */
                        try {
                            Log.d(context.getString(R.string.User_Api_Helper), response.toString());
                            if(response.getString("success") != null) {
                                Log.d(context.getString(R.string.User_Api_Helper), "Device Id was successfully sent");
                                mListener.onResponse(true);
                            } else {
                                Log.d(context.getString(R.string.User_Api_Helper), "Something went wrong. Could not complete Device Id post");
                                mListener.onResponse(false);
                            }
                        } catch(JSONException e) {
                            mListener.onResponse(false);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Check if the calling fragment is still in the stack or removed.
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.User_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Scubit Add JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(postDeviceIdReq);
    }

    /*
    Success: returns JSON with status code and status description
    Failure: returns JSON with error reason message
     */
    public void getEmailValidationStatus(final Integer userId, ApiResponse.Listener<JSONObject> apiListener) {
        final ApiResponse.Listener<JSONObject> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_user_email_validation_status);

        // Add user id to fetch from
        apiEndPoint += "?user_id="+userId.toString();

        Log.d(context.getString(R.string.User_Api_Helper), apiEndPoint);

        // Create Volley request to Get user profile information
        JsonObjectRequest getUserEmailValidationStatus = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Get user profile info
                           JSON-API format
                            For success:
                            {
                                user_id: 1,
                                status_code: 2,
                                status_description: "Active"
                            }
                            For failure:
                            {
                                error: "No User Id provided for get /user/account/status"
                            }
                        */
                        Log.d(context.getString(R.string.User_Api_Helper), response.toString());
                        if(!response.has("status_description")) {
                            Log.d(context.getString(R.string.User_Api_Helper), "Something went wrong. Failed to fetch user email validation status for user id : "+ userId +". Response : "+response);
                            mListener.onResponse(new JSONObject());
                        } else {
                            // Success case
                            Log.d(context.getString(R.string.User_Api_Helper), "Successfully fetched user email validation status for user id : "+ userId);
                            mListener.onResponse(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Check if the calling fragment is still in the stack or removed.
                        // TODO : Support for dynamic activity. Currently all fragments are loaded from home, so its not an issue.
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.User_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "User GET Email Validation Status JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getUserEmailValidationStatus);
    }
}
