package com.scube.Gondor.Home.models.scubitModels;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by vashoka on 7/4/15.
 */
public class PriceRange {
    private String priceRangeName;
    private Integer priceRangeId;
    private Context context;

    public PriceRange() {}

    private PriceRange(Parcel in) {
        priceRangeId = in.readInt();
        priceRangeName = in.readString();
    }

    public PriceRange(JSONObject priceRange, Context context) {

        this.context = context;

        try {
            priceRangeId = Integer.parseInt(priceRange.getString("price_range_id"));
            priceRangeName = priceRange.getString("price_range_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getPriceRangeId() {
        return  priceRangeId;
    }

    public String getPriceRangeName() {
        return priceRangeName;
    }

    public void setPriceRangeId(Integer id) {
        priceRangeId = id;
    }

    public void setPriceRangeName(String name) {
        priceRangeName = name;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // Write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(priceRangeId);
        out.writeString(priceRangeName);
    }

    // This is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<PriceRange> CREATOR = new Parcelable.Creator<PriceRange>() {
        public PriceRange createFromParcel(Parcel in) {
            return new PriceRange(in);
        }

        public PriceRange[] newArray(int size) {
            return new PriceRange[size];
        }
    };
}
