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
import com.scube.Gondor.Home.models.Shop;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by srikanthsridhara on 8/7/15.
 */
public class ShopApiHelper {
    public Context context;
    private String fragmentTag;

    public ShopApiHelper(Context context, String fragmentTag) {
        this.context = context;
        this.fragmentTag = fragmentTag;
    }

    public void getShopsByLocation(Integer locationId, ApiResponse.Listener<ArrayList<Shop>> apiListener) {
        final ApiResponse.Listener<ArrayList<Shop>> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_shops);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+locationId.toString()+"&q_case=1";

        Log.d(context.getString(R.string.Shop_Api_Helper), apiEndPoint);

        // Create Volley request to fetch the Shops based on location (city)
        JsonObjectRequest getShopsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray shopArray = new JSONArray();
                        try {
                            shopArray = response.getJSONArray("shops");
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get shops endpoint response
                                   JSON-API format
                        {
                            shops: [
                                {
                                    shop_name: "Shoppers Stop",
                                    shop_id: 4,
                                    scubit_count: 50
                                },
                                {
                                    shop_name: "Calvin Klein",
                                    shop_id: 5,
                                    scubit_count: 30
                                },
                                {
                                    shop_name: "Nike",
                                    shop_id: 2,
                                    scubit_count: 14
                                }
                            ]
                        }
                        */

                        ArrayList<Shop> shops = new ArrayList<Shop>();

                        // Parse Shops array and load into list view
                        for(int i = 0; i < shopArray.length(); i++) {
                            try {
                                JSONObject shopObj = shopArray.getJSONObject(i);

                                // Create a shop instance
                                Shop shop = new Shop(shopObj, context);

                                // Add Shop to Shop Array
                                shops.add(shop);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(context.getString(R.string.Shop_Api_Helper), "Responding listener with data : " + shopArray.toString());
                        mListener.onResponse(shops);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.Shop_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Get shops by Location JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getShopsBasedOnLocReq);
    }
}
