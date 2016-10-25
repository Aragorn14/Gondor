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
import com.scube.Gondor.Search.models.SearchModel;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vashoka on 8/29/15.
 */
public class SearchApiHelper {

    public Context context;
    private String fragmentTag;
    public String loc_id;

    public SearchApiHelper(Context context, String fragmentTag) {
        this.context = context;
        this.fragmentTag = fragmentTag;
        this.loc_id = GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_location));

    }

    public void getAllSearchResultsBasedOnLoc(String query, ApiResponse.Listener<ArrayList<SearchModel>> apiListener) {
        final ApiResponse.Listener<ArrayList<SearchModel>> searchListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.search);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+loc_id+"&q_case=1&q_search=" + query;

        Log.d(context.getString(R.string.Search_Api_Helper), apiEndPoint);

        // Create Volley request to search malls/shops/brands across a location (city) based on search query
        JsonObjectRequest getSearchResultsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray mallArray = new JSONArray();
                        JSONArray shopArray = new JSONArray();
                        JSONArray brandArray = new JSONArray();
                        try {
                            mallArray = (response.has("malls")) ? response.getJSONArray("malls") : new JSONArray();
                            shopArray = (response.has("shops")) ? response.getJSONArray("shops") : new JSONArray();
                            brandArray = (response.has("brands")) ? response.getJSONArray("brands") : new JSONArray();
                            Log.d(context.getString(R.string.Search_Fragment), "Search Mall Results : " + mallArray.toString());
                            Log.d(context.getString(R.string.Search_Fragment), "Search Shop Results : " + shopArray.toString());
                            Log.d(context.getString(R.string.Search_Fragment), "Search Brand Results : " + brandArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayList<SearchModel> searchResults = new ArrayList<SearchModel>();

                        /* Search endpoint response
                        {
                          malls: [
                          {
                             mall_id: 5,
                             mall_name: "Phoenix Market City",
                             scubit_count: 45
                          },
                          {
                             mall_id: 2,
                             mall_name: "Garuda Mall",
                             scubit_count: 33
                          }
                        ],
                        shops: [
                          {
                             shop_profile_id: 5,
                             shop_name: "Calvin Klein",
                             mall_id: 4,
                             mall_name: "Mantri Square",
                             address: null,
                             floor: 1,
                             scubit_count: 30
                          },
                          {
                             shop_profile_id: 1,
                             shop_name: "Adidas",
                             mall_id: 1,
                             mall_name: "The Forum",
                             address: null,
                             floor: 3,
                             scubit_count: 18
                          }
                        ],
                        brands: [
                        {
                             shop_profile_id: 1,
                             shop_name: "Adidas",
                             brand_id: 1,
                             brand_name: "Adidas",
                             mall_id: 1,
                             mall_name: "The Forum",
                             address: null,
                             floor: 3,
                             scubit_count: 20
                        },
                        {
                             shop_profile_id: 4,
                             shop_name: "Shoppers Stop",
                             brand_id: 8,
                             brand_name: "Indian Terrain",
                             mall_id: 4,
                             mall_name: "Mantri Square",
                             address: null,
                             floor: 1,
                             scubit_count: 1
                        }
                        ]
                        }
                        */

                        // CLEAR BEFORE RELOADING
                        searchResults.clear();

                        // Parse Brands array and load into list view
                        for (int i = 0; i < mallArray.length(); i++) {
                            try {
                                JSONObject searchObj = mallArray.getJSONObject(i);
                                SearchModel searchModel = new SearchModel(searchObj, context);
                                searchResults.add(searchModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        for (int i = 0; i < shopArray.length(); i++) {
                            try {
                                JSONObject searchObj = shopArray.getJSONObject(i);
                                SearchModel searchModel = new SearchModel(searchObj, context);
                                searchResults.add(searchModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        for (int i = 0; i < brandArray.length(); i++) {
                            try {
                                JSONObject searchObj = brandArray.getJSONObject(i);
                                SearchModel searchModel = new SearchModel(searchObj, context);
                                searchResults.add(searchModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(context.getString(R.string.Search_Api_Helper), "getAllSearchResultsBasedOnLoc : Responding listener with data : " + searchResults.toString());
                        searchListener.onResponse(searchResults);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.Search_Api_Helper) + ": getAllSearchResultsBasedOnLoc Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Search results-all by Location JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getSearchResultsBasedOnLocReq);
    }

    public void getMallSearchResultsBasedOnLoc(String query, ApiResponse.Listener<ArrayList<SearchModel>> apiListener) {
        final ApiResponse.Listener<ArrayList<SearchModel>> searchMallsListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_malls);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+loc_id+"&q_case=2&q_search=" + query;

        Log.d(context.getString(R.string.Search_Api_Helper), apiEndPoint);

        // Create Volley request to search malls across a location (city) based on search query
        JsonObjectRequest getMallSearchResultsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray mallArray = new JSONArray();
                        try {
                            mallArray = (response.has("malls")) ? response.getJSONArray("malls") : mallArray;
                            Log.d(context.getString(R.string.Search_Api_Helper), "Search Mall Results : " + mallArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayList<SearchModel> searchResults = new ArrayList<SearchModel>();

                        /* Shops search endpoint response
                        {
                            malls: [
                          {
                             mall_id: 5,
                             mall_name: "Phoenix Market City",
                             scubit_count: 45
                          },
                          {
                             mall_id: 2,
                             mall_name: "Garuda Mall",
                             scubit_count: 33
                          }
                        }
                        */

                        // CLEAR BEFORE RELOADING
                        searchResults.clear();

                        // Parse Malls array and load into list view
                        for (int i = 0; i < mallArray.length(); i++) {
                            try {
                                JSONObject searchObj = mallArray.getJSONObject(i);
                                SearchModel searchModel = new SearchModel(searchObj, context);
                                searchResults.add(searchModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(context.getString(R.string.Search_Api_Helper), "getMallSearchResultsBasedOnLoc : Responding listener with data : " + searchResults.toString());
                        searchMallsListener.onResponse(searchResults);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.Search_Api_Helper) + ": getMallSearchResultsBasedOnLoc Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Search malls by Location JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getMallSearchResultsBasedOnLocReq);
    }

    public void getShopSearchResultsBasedOnLoc(String query, ApiResponse.Listener<ArrayList<SearchModel>> apiListener) {
        final ApiResponse.Listener<ArrayList<SearchModel>> searchShopsListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_shops);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+loc_id+"&q_case=2&q_search=" + query;

        Log.d(context.getString(R.string.Search_Api_Helper), apiEndPoint);

        // Create Volley request to search malls/shops/brands across a location (city) based on search query
        JsonObjectRequest getShopSearchResultsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray shopArray = new JSONArray();
                        try {
                            shopArray = (response.has("shops")) ? response.getJSONArray("shops") : new JSONArray();
                            Log.d(context.getString(R.string.Search_Api_Helper), "Search Shop Results : " + shopArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayList<SearchModel> searchResults = new ArrayList<SearchModel>();

                        /* Search endpoint response
                        {
                            shops: [
                              {
                                 shop_profile_id: 5,
                                 shop_name: "Calvin Klein",
                                 mall_id: 4,
                                 mall_name: "Mantri Square",
                                 address: null,
                                 floor: 1,
                                 scubit_count: 30
                              },
                              {
                                 shop_profile_id: 1,
                                 shop_name: "Adidas",
                                 mall_id: 1,
                                 mall_name: "The Forum",
                                 address: null,
                                 floor: 3,
                                 scubit_count: 18
                              }
                            ]
                        }
                        */

                        // CLEAR BEFORE RELOADING
                        searchResults.clear();

                        // Parse Shops array and load into list view
                        for (int i = 0; i < shopArray.length(); i++) {
                            try {
                                JSONObject searchObj = shopArray.getJSONObject(i);
                                SearchModel searchModel = new SearchModel(searchObj, context);
                                searchResults.add(searchModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(context.getString(R.string.Search_Api_Helper), "getShopSearchResultsBasedOnLoc : Responding listener with data : " + searchResults.toString());
                        searchShopsListener.onResponse(searchResults);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.Search_Api_Helper) + ": getShopSearchResultsBasedOnLoc Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Search shops by Location JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getShopSearchResultsBasedOnLocReq);
    }


    public void getBrandSearchResultsBasedOnLoc(String query, ApiResponse.Listener<ArrayList<SearchModel>> apiListener) {
        final ApiResponse.Listener<ArrayList<SearchModel>> searchBrandsListener = apiListener;

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + context.getString(R.string.get_brands);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+loc_id+"&q_case=2&q_search=" + query;

        Log.d(context.getString(R.string.Search_Api_Helper), apiEndPoint);

        // Create Volley request to search brands across a location (city) based on search query
        JsonObjectRequest getBrandSearchResultsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray brandArray = new JSONArray();
                        try {
                            brandArray = (response.has("brands")) ? response.getJSONArray("brands") : new JSONArray();
                            Log.d(context.getString(R.string.Search_Api_Helper), "Search Brand Results : " + brandArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayList<SearchModel> searchResults = new ArrayList<SearchModel>();

                        /* Search endpoint response
                        {
                            brands: [
                            {
                                 shop_profile_id: 1,
                                 shop_name: "Adidas",
                                 brand_id: 1,
                                 brand_name: "Adidas",
                                 mall_id: 1,
                                 mall_name: "The Forum",
                                 address: null,
                                 floor: 3,
                                 scubit_count: 20
                            },
                            {
                                 shop_profile_id: 4,
                                 shop_name: "Shoppers Stop",
                                 brand_id: 8,
                                 brand_name: "Indian Terrain",
                                 mall_id: 4,
                                 mall_name: "Mantri Square",
                                 address: null,
                                 floor: 1,
                                 scubit_count: 1
                            }
                            ]
                        }
                        */

                        // CLEAR BEFORE RELOADING
                        searchResults.clear();

                        // Parse Shops array and load into list view
                        for (int i = 0; i < brandArray.length(); i++) {
                            try {
                                JSONObject searchObj = brandArray.getJSONObject(i);
                                SearchModel searchModel = new SearchModel(searchObj, context);
                                searchResults.add(searchModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(context.getString(R.string.Search_Api_Helper), "getBrandSearchResultsBasedOnLoc : Responding listener with data : " + searchResults.toString());
                        searchBrandsListener.onResponse(searchResults);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HomeActivity homeActivity = (HomeActivity) context;
                        NavigationFragment callingFragment = homeActivity.fragmentInStackFromTag(fragmentTag);
                        if (context != null && callingFragment != null && callingFragment.isAdded()) {
                            VolleyLog.d(context.getString(R.string.Search_Api_Helper) + ": getBrandSearchResultsBasedOnLoc Error : " + error.getMessage());
                        }
                    }
                });

        // Add the json request to volley request queue
        Log.d(context.getString(R.string.Scube_Volley), "Search brands by Location JSON Request Placed in Queue");
        AppController.getInstance().addToRequestQueue(getBrandSearchResultsBasedOnLocReq);
    }
}
