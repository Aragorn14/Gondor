package com.scube.Gondor.Home.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vashoka on 5/8/15.
 */
public class Shop implements Parcelable {

    private String shopName;
    private Integer shopId, scubitCount;
    private JSONArray shopImages;

    SharedPreferences sharedPreferences;
    private Context context;

    public Shop() {}

    private Shop(Parcel in) {
        shopId = in.readInt();
        shopName = in.readString();
        scubitCount = in.readInt();
    }

    public Shop(JSONObject shop, Context context) {

        this.context = context;

        try {
            shopId = Integer.parseInt(shop.getString("shop_id"));
            shopName = shop.getString("shop_name");
            scubitCount = Integer.parseInt(shop.getString("scubit_count"));
            shopImages = shop.getJSONArray("images");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getShopId() {
        return  shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public Integer getScubitCount() {
        return scubitCount;
    }

    public void setShopId(Integer id) {
        shopId = id;
    }

    public void setShopName(String name) {
        shopName = name;
    }

    public void setScubitCount(Integer count) {
        scubitCount = count;
    }

    public void setShopImages(JSONArray shopImages) {
        this.shopImages = shopImages;
    }

    public JSONArray getShopImages() {
        return shopImages;
    }

    public JSONObject getShopAllBackgrounds() throws JSONException {
        return shopImages.getJSONObject(0);
    }

    public JSONObject getShopAllLogos() throws JSONException {
        return shopImages.getJSONObject(1);
    }

    public String getShopBackground(String resolution) {
        String shopBG = "";
        if( shopImages.length() != 0) {
            try {
                shopBG = shopImages.getJSONObject(0).getJSONObject("image_resolution").getString(resolution);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return shopBG;
    }

    public String getShopLogo(String resolution) {
        String shopLogo = "";
        try {
            shopLogo = shopImages.getJSONObject(1).getJSONObject("image_resolution").getString(resolution);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return shopLogo;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // Write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(shopId);
        out.writeString(shopName);
        out.writeInt(scubitCount);
    }

    // This is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };
}
