package com.scube.Gondor.Helpers.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.quickblox.chat.model.QBDialog;
import com.scube.Gondor.Core.controllers.AppController;
import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.R;
import com.scube.Gondor.Home.models.scubitModels.MyScubitModel;
import com.scube.Gondor.Home.models.scubitModels.ScubitModel;
import com.scube.Gondor.Util.Auth;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vashoka on 7/17/15.
 */
public class ScubitApiHelper {

    public Context context;
    private String fragmentTag;

    public ScubitApiHelper(Context context, String fragmentTag) {
        this.context = context;
        this.fragmentTag = fragmentTag;
    }

    public void addScubit(JSONObject scubitData, ApiResponse.Listener<Boolean> apiListener) {
        final ApiResponse.Listener<Boolean> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.add_update_delete_scubits);

        Log.d(context.getString(R.string.Scubit_Api_Helper), apiEndPoint);

        // Create Volley request to Add scubit
        JsonObjectRequest postAddScubitReq = new JsonObjectRequest(apiEndPoint, scubitData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Post scubit Add response
                               JSON-API format
                                SUCCESS CASE
                            {
                                "scubit_id": 13,
                                "scubit_status": 1,
                                "scubit_reason": "success- new scubit added"
                            }
                                FAILURE CASE
                            {
                                "scubit_id": 13,
                                "scubit_status": -1,
                                "scubit_reason": "<custom error based on what exactly failed in DB/Node>"
                            }
                        */
                        try {
                            Log.d(context.getString(R.string.Scubit_Api_Helper), response.toString());
                            Integer status = response.getInt("scubit_status");

                            if(status == 1) {
                                Log.d(context.getString(R.string.Scubit_Api_Helper), "Scubit was successfully completed");
                                mListener.onResponse(true);
                            } else {
                                Log.d(context.getString(R.string.Scubit_Api_Helper), "Something went wrong. Could not complete scubit");
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
                            VolleyLog.d(context.getString(R.string.Scubit_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Scubit Add JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(postAddScubitReq);
    }

    private void completeScubit(Integer scubitId, ApiResponse.Listener<Boolean> apiListener) {
        final ApiResponse.Listener<Boolean> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.add_update_delete_scubits);

        Log.d(context.getString(R.string.Scubit_Api_Helper), apiEndPoint);

        // Scubit Complete POST data
        JSONObject scubitCompleteData = new JSONObject();
        try {
            scubitCompleteData.put("action_type", 2);
            scubitCompleteData.put("scubit_id", scubitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create Volley request to Complete scubit
        JsonObjectRequest postScubitCompleteReq = new JsonObjectRequest(apiEndPoint, scubitCompleteData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Post scubit Complete response
                               JSON-API format
                               SUCCESS CASE
                            {
                                "scubit_id": 13,
                                "scubit_status": 1,
                                "scubit_reason": "success- new scubit deleted"
                            }
                                FAILURE CASE
                            {
                                "scubit_id": 13,
                                "scubit_status": -1,
                                "scubit_reason": "<custom error based on what exactly failed in DB/Node>"
                            }
                        */
                        try {
                            Log.d(context.getString(R.string.Scubit_Api_Helper), response.toString());
                            Integer status = response.getInt("scubit_status");

                            if(status == 1) {
                                Log.d(context.getString(R.string.Scubit_Api_Helper), "Scubit was successfully completed");
                                mListener.onResponse(true);
                            } else {
                                Log.d(context.getString(R.string.Scubit_Api_Helper), "Something went wrong. Could not complete scubit");
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
                            VolleyLog.d(context.getString(R.string.Scubit_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Scubit Complete JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(postScubitCompleteReq);
    }

    private void updateScubit(JSONObject scubitUpdateData, ApiResponse.Listener<Boolean> apiListener) {
        final ApiResponse.Listener<Boolean> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.add_update_delete_scubits);

        Log.d(context.getString(R.string.Scubit_Api_Helper), apiEndPoint);

        // Create Volley request to Complete scubit
        JsonObjectRequest postScubitUpdateReq = new JsonObjectRequest(apiEndPoint, scubitUpdateData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Post scubit Update response
                               JSON-API format
                                SUCCESS CASE
                            {
                                "scubit_id": 13,
                                "scubit_status": 1,
                                "scubit_reason": "success"
                            }
                                FAILURE CASE
                            {
                                "scubit_id": 13,
                                "scubit_status": -1,
                                "scubit_reason": "<custom error based on what exactly failed in DB/Node>"
                            }
                        */
                        try {
                            Log.d(context.getString(R.string.Scubit_Api_Helper), response.toString());
                            Integer status = response.getInt("scubit_status");

                            if(status == 1) {
                                Log.d(context.getString(R.string.Scubit_Api_Helper), "Scubit was successfully updated");
                                mListener.onResponse(true);
                            } else {
                                Log.d(context.getString(R.string.Scubit_Api_Helper), "Something went wrong. Could not update scubit");
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
                            VolleyLog.d(context.getString(R.string.Scubit_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Scubit Update JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(postScubitUpdateReq);
    }


    public void getScubitsFromShopProfile(ShopProfile shopProfile, final List<QBDialog> userDialogs, ApiResponse.Listener<List<ScubitModel>> apiListener) {
        final ApiResponse.Listener<List<ScubitModel>> mListener = apiListener;
        if (GlobalUtils.containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_session))) {
            int user_id = GlobalUtils.getIntSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.scube_id));

            // Form the end point
            String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_scubits_by_shop_profile);

            apiEndPoint += "?shop_profile_id=" + shopProfile.getShopProfileId() + "&q_case=1"+ "&user_id="+user_id;
            Log.d(context.getString(R.string.Scubit_Api_Helper), "Populating Scubits with Shop Id:" + shopProfile.getShopProfileId());
            Log.d(context.getString(R.string.Scubit_Api_Helper), "API End point: " + apiEndPoint);

            // Create Volley request to fetch the Scubits based on shop profile
            JsonObjectRequest getScubitsOfShop = new JsonObjectRequest(apiEndPoint, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            final List<ScubitModel> scubitModelList = new ArrayList<ScubitModel>();
                            JSONArray scubitsArray = new JSONArray();
                            try {
                                scubitsArray = response.getJSONArray("scubits");
                                Log.d(context.getString(R.string.Scubit_Api_Helper), scubitsArray.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            /* Get scubit endpoint response in JSON
                            {
                              scubits: [
                                {
                                  scubit_id: 27,
                                  user_id: 2,
                                  qb_user_id: 0,
                                  brand_id: 1,
                                  brand_name: "Adidas",
                                  brand_valid: 1,
                                  shop_profile_id: 1,
                                  shop_name: "Adidas",
                                  mall_id: 1,
                                  mall_name: "The Forum",
                                  offer_id: 1,
                                  offer_name: "Buy 1 get 1 free",
                                  price_range_id: 1,
                                  price_range_name: "0-500",
                                  payment_type_id: 1,
                                  payment_type_name: "Cash",
                                  num_items: null,
                                  start_date: "2015-07-04T11:16:00.000Z",
                                  end_date: "2015-07-04T15:16:00.000Z",
                                  notes: null,
                                  photo_url: null
                                }
                              ]
                            }
                            */

                            // Parse Scubits array and load into list view
                            for (int i = 0; i < scubitsArray.length(); i++) {
                                try {
                                    // Create a myScubitModel instance and add it to the model list
                                    ScubitModel scubitModel = new ScubitModel(scubitsArray.getJSONObject(i), userDialogs, context);
                                    scubitModelList.add(scubitModel);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mListener.onResponse(scubitModelList);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Check if the calling fragment is still in the stack or removed.
                            HomeActivity homeActivity = (HomeActivity) context;
                            NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                            if (context != null && callingFragment != null && callingFragment.isAdded()) {
                                VolleyLog.d(context.getString(R.string.Scubit_Api_Helper) + ": Error : " + error.getMessage());
                            }
                        }
                    });

            // Add the json request to volley request queue
            RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
            Log.d("Request Queue : ", requestQueue.toString());
            AppController.getInstance().addToRequestQueue(getScubitsOfShop);
        }
    }

    public void getScubitsOfUser(ApiResponse.Listener<List<MyScubitModel>> apiListener) {
        final ApiResponse.Listener<List<MyScubitModel>> mListener = apiListener;

        JSONObject loginDetails = Auth.getLoginDetails(context);
        // Make sure the user is logged in
        try {
            if (loginDetails.getString("status").equals(context.getString(R.string.user_session_true))) {
                int user_id = Auth.getLoginDetails(context).getInt("scubeId");

                // Form the end point
                String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_scubits_by_user);
                apiEndPoint += "?q_case=2" + "&user_id=" + user_id;
                Log.d(context.getString(R.string.Scubit_Api_Helper), "API End point: " + apiEndPoint);

                // Create Volley request to fetch the Scubits based on Scube Id
                JsonObjectRequest getScubitsOfUser = new JsonObjectRequest(apiEndPoint, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                final List<MyScubitModel> scubitModelList = new ArrayList<MyScubitModel>();
                                JSONArray scubitsArray = new JSONArray();
                                try {
                                    scubitsArray = response.getJSONArray("scubits");
                                    Log.d(context.getString(R.string.Scubit_Api_Helper), scubitsArray.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            /* Get scubit endpoint response in JSON
                            {
                               scubits: [
                                {
                                    scubit_id: 4,
                                    brand_id: 3,
                                    brand_name: "Reebok",
                                    shop_profile_id: 4,
                                    shop_name: "Shoppers Stop",
                                    mall_id: 4,
                                    mall_name: "Mantri Square",
                                    offer_id: 1,
                                    offer_name: "Buy 1 get 1 free",
                                    price_range_id: 1,
                                    price_range_name: "0-500",
                                    payment_id: 1,
                                    payment_name: "Cash",
                                    num_items: 2,
                                    start_date: "2015-08-05T19:00:01.000Z",
                                    end_date: "2015-08-06T19:00:01.000Z",
                                    notes: null,
                                    photo_url: null,
                                    valid: 1
                               },
                              ]
                            }
                            */

                                // Parse Scubits array and load into list view
                                for (int i = 0; i < scubitsArray.length(); i++) {
                                    try {
                                        // Create a myScubitModel instance and add it to the model list
                                        MyScubitModel myScubitModel = new MyScubitModel(scubitsArray.getJSONObject(i), context);
                                        scubitModelList.add(myScubitModel);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                mListener.onResponse(scubitModelList);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Check if the calling fragment is still in the stack or removed.
                                HomeActivity homeActivity = (HomeActivity) context;
                                NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                                if (context != null && callingFragment != null && callingFragment.isAdded()) {
                                    VolleyLog.d(context.getString(R.string.Scubit_Api_Helper) + ": Error : " + error.getMessage());
                                }
                            }
                        });

                // Add the json request to volley request queue
                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
                Log.d("Request Queue : ", requestQueue.toString());
                AppController.getInstance().addToRequestQueue(getScubitsOfUser);
            }
        } catch (JSONException e) {
            Log.e(context.getString(R.string.Scubit_Api_Helper), "Valid session not found!");
        }
    }
}