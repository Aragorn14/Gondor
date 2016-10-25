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
import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vashoka on 7/17/15.
 */
public class MallApiHelper {

    public Context context;
    private String fragmentTag;

    public MallApiHelper(Context context, String fragmentTag) {
        this.context = context;
        this.fragmentTag = fragmentTag;
    }

    public void getMallsByLocation(Integer locationId, ApiResponse.Listener<ArrayList<Mall>> apiListener) {
        final ApiResponse.Listener<ArrayList<Mall>> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_malls);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+locationId.toString()+"&q_case=1";

        Log.d(context.getString(R.string.Mall_Api_Helper), apiEndPoint);

        // Create Volley request to fetch the Malls based on location (city)
        JsonObjectRequest getMallsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray mallArray = new JSONArray();
                        try {
                            mallArray = response.getJSONArray("malls");
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get malls endpoint response
                                   JSON-API format
                        {
                            malls: [
                                {
                                    mall_name: "Phoenix Market City",
                                    mall_id: 5,
                                    scubit_count: 45
                                },
                                {
                                    mall_name: "Garuda Mall",
                                    mall_id: 2,
                                    scubit_count: 30
                                },
                                {
                                    mall_name: "The Forum",
                                    mall_id: 1,
                                    scubit_count: 25
                                }
                            ]
                        }
                        */

                        ArrayList<Mall> malls = new ArrayList<Mall>();

                        // Parse Malls array and load into list view
                        for(int i = 0; i < mallArray.length(); i++) {
                            try {
                                JSONObject mallObj = mallArray.getJSONObject(i);

                                // Create a mall instance
                                Mall mall = new Mall(mallObj, context);

                                // Add Mall to Mall Array
                                malls.add(mall);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(context.getString(R.string.Mall_Api_Helper), "Responding listener with data : "+mallArray.toString());
                        mListener.onResponse(malls);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.Mall_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Get malls by Location JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getMallsBasedOnLocReq);
    }
}
