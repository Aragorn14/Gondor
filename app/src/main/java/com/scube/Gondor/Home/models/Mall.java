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
public class Mall implements Parcelable {

    private String mallName;
    private Integer mallId, scubitCount;
    private JSONArray mallImages;

    SharedPreferences sharedPreferences;
    private Context context;

    public Mall() {}

    private Mall(Parcel in) {
        mallId = in.readInt();
        mallName = in.readString();
        scubitCount = in.readInt();
    }

    public Mall(JSONObject mall, Context context) {

        this.context = context;

        try {
            mallId = Integer.parseInt(mall.getString("mall_id"));
            mallName = mall.getString("mall_name");
            scubitCount = Integer.parseInt(mall.getString("scubit_count"));
            mallImages = mall.getJSONArray("images");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getMallId() {
        return  mallId;
    }

    public String getMallName() {
        return mallName;
    }

    public Integer getScubitCount() {
        return scubitCount;
    }

    public void setMallId(Integer id) {
        mallId = id;
    }

    public void setMallName(String name) {
        mallName = name;
    }

    public void setScubitCount(Integer count) {
        scubitCount = count;
    }

    public void setMallImages(JSONArray mallImages) {
        this.mallImages = mallImages;
    }

    public JSONArray getMallImages() {
        return mallImages;
    }

    public JSONObject getMallAllBackgrounds() throws JSONException {
        return mallImages.getJSONObject(0);
    }

    public JSONObject getMallAllLogos() throws JSONException {
        return mallImages.getJSONObject(1);
    }

    public String getMallBackground(String resolution) {
        String mallBG = "";
        if( mallImages.length() != 0) {
            try {
                mallBG = mallImages.getJSONObject(0).getJSONObject("image_resolution").getString(resolution);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return mallBG;
    }

    public String getMallLogo(String resolution) {
        String mallLogo = "";
        try {
            mallLogo = mallImages.getJSONObject(1).getJSONObject("image_resolution").getString(resolution);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return mallLogo;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // Write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mallId);
        out.writeString(mallName);
        out.writeInt(scubitCount);
    }

    // This is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Mall> CREATOR = new Parcelable.Creator<Mall>() {
        public Mall createFromParcel(Parcel in) {
            return new Mall(in);
        }

        public Mall[] newArray(int size) {
            return new Mall[size];
        }
    };
}

