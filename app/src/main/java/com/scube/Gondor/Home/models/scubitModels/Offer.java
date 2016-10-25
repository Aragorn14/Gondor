package com.scube.Gondor.Home.models.scubitModels;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by vashoka on 7/4/15.
 */
public class Offer {
    private String offerName;
    private Integer offerId;
    private Context context;

    public Offer() {}

    private Offer(Parcel in) {
        offerId = in.readInt();
        offerName = in.readString();
    }

    public Offer(JSONObject offer, Context context) {

        this.context = context;

        try {
            offerId = Integer.parseInt(offer.getString("offer_id"));
            offerName = offer.getString("offer_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getOfferId() {
        return  offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferId(Integer id) {
        offerId = id;
    }

    public void setOfferName(String name) {
        offerName = name;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // Write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(offerId);
        out.writeString(offerName);
    }

    // This is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Offer> CREATOR = new Parcelable.Creator<Offer>() {
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };
}
