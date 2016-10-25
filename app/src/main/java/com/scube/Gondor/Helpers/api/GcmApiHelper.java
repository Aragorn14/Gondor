package com.scube.Gondor.Helpers.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.scube.Gondor.Core.controllers.AppController;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.json.JSONObject;

/**
 * Created by srikanthsridhara on 9/6/15.
 */
public class GcmApiHelper {
    public Context context;

    public GcmApiHelper(Context context) {
        this.context = context;
    }

    public void postGcmRegistration(JSONObject gcmData, ApiResponse.Listener<JSONObject> apiListener) {
        final ApiResponse.Listener<JSONObject> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.sparkk_device_registration);
        Log.d(context.getString(R.string.Gcm_Api_Helper), apiEndPoint);

        // Create Volley request to Post Gcm Registration
        JsonObjectRequest postGcmRegistrationReq = new JsonObjectRequest(apiEndPoint, gcmData,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(context.getString(R.string.Gcm_Api_Helper), response.toString());
                    mListener.onResponse(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(context.getString(R.string.Gcm_Api_Helper) + ": Error : " + error.getMessage());
                }
            });
        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Gcm Registration JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(postGcmRegistrationReq);
    }
}
