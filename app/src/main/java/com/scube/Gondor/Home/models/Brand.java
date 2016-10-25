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
public class Brand implements Parcelable {

    private String brandName;
    private Integer brandId, scubitCount;
    private JSONArray brandImages;

    SharedPreferences sharedPreferences;
    private Context context;

    public Brand() {}

    private Brand(Parcel in) {
        brandId = in.readInt();
        brandName = in.readString();
        scubitCount = in.readInt();
    }

    public Brand(JSONObject brand, Context context) {

        this.context = context;

        try {
            brandId = Integer.parseInt(brand.getString("brand_id"));
            brandName = brand.getString("brand_name");
            scubitCount = Integer.parseInt(brand.getString("scubit_count"));
            brandImages = brand.getJSONArray("images");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getBrandId() {
        return  brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public Integer getScubitCount() {
        return scubitCount;
    }

    public void setBrandId(Integer id) {
        brandId = id;
    }

    public void setBrandName(String name) {
        brandName = name;
    }

    public void setScubitCount(Integer count) {
        scubitCount = count;
    }

    public void setBrandImages(JSONArray brandImages) {
        this.brandImages = brandImages;
    }

    public JSONArray getBrandImages() {
        return brandImages;
    }

    public JSONObject getBrandAllBackgrounds() throws JSONException {
        return brandImages.getJSONObject(0);
    }

    public JSONObject getBrandAllLogos() throws JSONException {
        return brandImages.getJSONObject(1);
    }

    public String getBrandBackground(String resolution) {
        String brandBG = "";
        if( brandImages.length() != 0) {
            try {
                brandBG = brandImages.getJSONObject(0).getJSONObject("image_resolution").getString(resolution);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return brandBG;
    }

    public String getBrandLogo(String resolution) {
        String brandLogo = "";
        try {
            brandLogo = brandImages.getJSONObject(1).getJSONObject("image_resolution").getString(resolution);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return brandLogo;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // Write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(brandId);
        out.writeString(brandName);
        out.writeInt(scubitCount);
    }

    // This is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Brand> CREATOR = new Parcelable.Creator<Brand>() {
        public Brand createFromParcel(Parcel in) {
            return new Brand(in);
        }

        public Brand[] newArray(int size) {
            return new Brand[size];
        }
    };
}

