package com.scube.Gondor.Home.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vashoka on 5/15/15.
 */
public class ShopProfile implements Parcelable {

    private String brandName, shopName, mallName, address;
    private Integer shopProfileId, mallId, floor, scubitCount, brandId;
    private JSONArray shopProfileImages;

    public ShopProfile() {}

    private ShopProfile(Parcel in) {
        shopName = in.readString();
        shopProfileId = in.readInt();
        mallName = in.readString();
        mallId = in.readInt();
        address = in.readString();
        floor = in.readInt();
        scubitCount = in.readInt();
        brandId = in.readInt();
        brandName = in.readString();
    }

    public ShopProfile(JSONObject shopProfile, Context context) {
        try {
            shopName = (!shopProfile.isNull("shop_name")) ? shopProfile.getString("shop_name") : "";
            shopProfileId = (!shopProfile.isNull("shop_profile_id")) ? Integer.parseInt(shopProfile.getString("shop_profile_id")) : -1;
            mallName = (!shopProfile.isNull("mall_name")) ? shopProfile.getString("mall_name") : "";
            mallId = (!shopProfile.isNull("mall_id")) ? Integer.parseInt(shopProfile.getString("mall_id")) : -1;
            address = (!shopProfile.isNull("address")) ? shopProfile.getString("address") : "";
            floor = (!shopProfile.isNull("floor")) ? Integer.parseInt(shopProfile.getString("floor")) : -100;
            scubitCount = (!shopProfile.isNull("scubit_count")) ? Integer.parseInt(shopProfile.getString("scubit_count")) : -1;
            brandId = (!shopProfile.isNull("brand_id")) ? Integer.parseInt(shopProfile.getString("brand_id")) : -1;
            brandName = (!shopProfile.isNull("brand_name")) ? shopProfile.getString("brand_name") : "";
            shopProfileImages = shopProfile.getJSONArray("images");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getShopName() {
        return shopName;
    }

    public Integer getShopProfileId() {
        return  shopProfileId;
    }

    public String getMallName() {
        return mallName;
    }

    public Integer getMallId() {
        return  mallId;
    }

    public String getAddress() {
        return address;
    }

    public Integer getFloor() {
        return floor;
    }

    public Integer getScubitCount() {
        return scubitCount;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    // Setters
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopProfileId(Integer shopProfileId) {
        this.shopProfileId = shopProfileId;
    }

    public void setMallName(String mallName) {
        this.mallName = mallName;
    }

    public void setMallId(Integer mallId) {
        this.mallId = mallId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public void setScubitCount(Integer scubitCount) {
        this.scubitCount = scubitCount;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setShopProfileImages(JSONArray shopProfileImages) {
        this.shopProfileImages = shopProfileImages;
    }

    public JSONArray getShopProfileImages() {
        return shopProfileImages;
    }

    public JSONObject getShopProfileAllBackgrounds() throws JSONException {
        return shopProfileImages.getJSONObject(0);
    }

    public JSONObject getShopProfileAllLogos() throws JSONException {
        return shopProfileImages.getJSONObject(1);
    }

    public String getShopProfileBackground(String resolution) {
        String shopProfileBG = "";
        if( shopProfileImages.length() != 0) {
            try {
                shopProfileBG = shopProfileImages.getJSONObject(0).getJSONObject("image_resolution").getString(resolution);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return shopProfileBG;
    }

    public String getShopProfileLogo(String resolution) {
        String shopProfileLogo = "";
        try {
            shopProfileLogo = shopProfileImages.getJSONObject(1).getJSONObject("image_resolution").getString(resolution);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return shopProfileLogo;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // Write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(shopName);
        out.writeInt(shopProfileId);
        out.writeString(mallName);
        out.writeInt(mallId);
        out.writeString(address);
        out.writeInt(floor);
        out.writeInt(scubitCount);
        out.writeInt(brandId);
        out.writeString(brandName);
    }

    // This is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<ShopProfile> CREATOR = new Creator<ShopProfile>() {
        public ShopProfile createFromParcel(Parcel in) {
            return new ShopProfile(in);
        }
        public ShopProfile[] newArray(int size) {
            return new ShopProfile[size];
        }
    };
}
