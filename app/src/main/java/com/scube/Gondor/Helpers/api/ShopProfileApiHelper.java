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
import com.scube.Gondor.Home.models.ShopProfile;
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
public class ShopProfileApiHelper {
    public Context context;
    private String fragmentTag;
    private String profile;
    private Integer entityId;

    public ShopProfileApiHelper(Context context, String fragmentTag, String profile, Integer entityId) {
        this.context = context;
        this.fragmentTag = fragmentTag;
        this.profile = profile;
        this.entityId = entityId;
    }

    public void getShopProfilesByLocation(Integer locationId, ApiResponse.Listener<ArrayList<ShopProfile>> apiListener) {
        final ApiResponse.Listener<ArrayList<ShopProfile>> mListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_shops);

        if(profile.equals("mall")) {
            apiEndPoint += "?q_case=" + context.getString(R.string.query_case_by_mall_id) + "&mall_id=" + entityId;
        } else if(profile.equals("shop")) {
            apiEndPoint += "?loc_id="+locationId.toString()+"&q_case=" + context.getString(R.string.query_case_by_shop_name) + "&shop_id=" + entityId;
        } else if(profile.equals("brand")) {
            apiEndPoint += "?loc_id="+locationId.toString()+"&q_case=" + context.getString(R.string.query_case_by_brand_id) + "&brand_id=" + entityId;
        }

        Log.d(context.getString(R.string.ShopProfile_Api_Helper), apiEndPoint);

        // Create Volley request to fetch the ShopProfiles based on location (city)
        JsonObjectRequest getShopProfilesBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray shopProfileArray = new JSONArray();
                        try {
                            shopProfileArray = response.getJSONArray("shops");
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get shops endpoint response
                                       JSON-API format
                            {
                                 shops: [
                                 {
                                   shop_name: "Adidas",
                                   shop_profile_id: 1,
                                   address: null,
                                   floor: 3,
                                   scubit_count: 11
                                 }
                               ]
                            }
                            */

                        ArrayList<ShopProfile> shops = new ArrayList<ShopProfile>();

                        // Parse ShopProfiles array and load into list view
                        for(int i = 0; i < shopProfileArray.length(); i++) {
                            try {
                                JSONObject shopProfileObj = shopProfileArray.getJSONObject(i);

                                // Create a shop instance
                                ShopProfile shop = new ShopProfile(shopProfileObj, context);

                                // Add ShopProfile to ShopProfile Array
                                shops.add(shop);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(context.getString(R.string.ShopProfile_Api_Helper), "Responding listener with data : " + shopProfileArray.toString());
                        mListener.onResponse(shops);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.ShopProfile_Api_Helper) + ": Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Get shops by Location JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getShopProfilesBasedOnLocReq);
    }
}
