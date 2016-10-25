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
import com.scube.Gondor.Home.models.Brand;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by srikanthsridhara on 8/1/15.
 */
public class BrandApiHelper {
    public Context context;
    private String fragmentTag;

    public BrandApiHelper(Context context, String fragmentTag) {
        this.context = context;
        this.fragmentTag = fragmentTag;
    }

    public void getBrandsByLocation(Integer locationId, ApiResponse.Listener<ArrayList<Brand>> apiListener) {
        final ApiResponse.Listener<ArrayList<Brand>> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_brands);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+locationId.toString()+"&q_case=1";

        Log.d(context.getString(R.string.Brand_Api_Helper), apiEndPoint);

        // Create Volley request to fetch the Brands based on location (city)
        JsonObjectRequest getBrandsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray brandArray = new JSONArray();
                        try {
                            brandArray = response.getJSONArray("brands");
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get brands endpoint response
                                   JSON-API format
                        {
                            brands: [
                            {
                                brand_name: "Shoppers Stop",
                                brand_id: 4,
                                scubit_count: 52
                            },
                            {
                                brand_name: "Nike",
                                brand_id: 2,
                                scubit_count: 26
                            },
                            {
                                brand_name: "Reebok",
                                brand_id: 3,
                                scubit_count: 26
                            }
                            ]
                        }
                        */

                        ArrayList<Brand> brands = new ArrayList<Brand>();

                        // Parse Brands array and load into list view
                        for(int i = 0; i < brandArray.length(); i++) {
                            try {
                                JSONObject brandObj = brandArray.getJSONObject(i);

                                // Create a brand instance
                                Brand brand = new Brand(brandObj, context);

                                // Add Brand to Brand Array
                                brands.add(brand);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(context.getString(R.string.Brand_Api_Helper), "Responding listener with data : " + brandArray.toString());
                        mListener.onResponse(brands);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.Brand_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Get brands by Location JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getBrandsBasedOnLocReq);
    }
}
