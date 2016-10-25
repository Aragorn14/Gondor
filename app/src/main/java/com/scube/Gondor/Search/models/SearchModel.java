package com.scube.Gondor.Search.models;

import android.content.Context;
import android.util.Log;

import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.Home.models.ShopProfile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vashoka on 05/30/15.
 */
public class SearchModel
{
    private static final String ALL_FILTER = "ALL";
    private static final String MALLS_FILTER = "MALLS";
    private static final String SHOPS_FILTER = "SHOPS";
    private static final String BRANDS_FILTER = "BRANDS";

    public String modelContext;
    public String title;
    public String imageUrl;
    public Integer scubitCount;
    private String searchModelType = "";
    private Mall mall;
    private ShopProfile shopProfile, brandProfile;

    public SearchModel(JSONObject object, Context context)
    {
        try {
            if(object.has("brand_id")) {
                searchModelType = "brandProfile";
                brandProfile = new ShopProfile(object, context);
                // Brand search result
                title = object.getString("brand_name");
                // Mall Name Shop Name
                modelContext = object.has("mall_name") ? object.getString("mall_name") : "";
                modelContext += object.has("shop_name") ? " " + object.getString("shop_name") : "";
            } else if(object.has("shop_profile_id")) {
                searchModelType = "shopprofile";
                shopProfile = new ShopProfile(object, context);
                // Shop search result
                title = object.getString("shop_name");
                // Mall Name
                modelContext = object.has("mall_name") ? object.getString("mall_name") : "";
            } else if(object.has("mall_id")) {
                searchModelType = "mall";
                mall = new Mall(object, context);
                // Mall search result
                title = object.getString("mall_name");
                // NA
                modelContext = "";
            } else {
                Log.d("Search Model", "Invalid search result type");
            }
            // Scubit count common attribute for all types
            scubitCount = Integer.parseInt(object.has("scubit_count") ? object.getString("scubit_count") : "0");

            // TODO : Image url for each search result
            imageUrl = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getFilterParams(String filter)
    {
        String params = "";
        if (filter == ALL_FILTER) {
            params = "loc_id=3&q_case=1";
        } else if (filter == MALLS_FILTER) {
            params = "loc_id=3&q_case=2";
        } else if (filter == SHOPS_FILTER) {
            params = "loc_id=3&q_case=2";
        } else if (filter == BRANDS_FILTER) {
            params = "loc_id=3&q_case=2";
        }

        return params;
    }

    public String getSearchModelType() {
        return this.searchModelType;
    }

    public Mall getMall() {
        return mall;
    }

    public ShopProfile getShopProfile() {
        return shopProfile;
    }

    public ShopProfile getBrandProfile() {
        return brandProfile;
    }

    public String getTitle() {
        if(this.searchModelType.equals("mall")) {
            return mall.getMallName();
        } else if(this.searchModelType.equals("shopProfile")) {
            return shopProfile.getShopName();
        } else if(this.searchModelType.equals("brandProfile")) {
            return brandProfile.getBrandName();
        }

        return "";
    }
}

