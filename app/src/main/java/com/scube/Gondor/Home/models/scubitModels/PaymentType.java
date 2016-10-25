package com.scube.Gondor.Home.models.scubitModels;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by vashoka on 7/4/15.
 */
public class PaymentType {
    private String paymentTypeName;
    private Integer paymentTypeId;
    private Context context;

    public PaymentType() {}

    private PaymentType(Parcel in) {
        paymentTypeId = in.readInt();
        paymentTypeName = in.readString();
    }

    public PaymentType(JSONObject paymentType, Context context) {

        this.context = context;

        try {
            paymentTypeId = Integer.parseInt(paymentType.getString("payment_type_id"));
            paymentTypeName = paymentType.getString("payment_type_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getPaymentTypeId() {
        return  paymentTypeId;
    }

    public String getPaymentTypeName() {
        return paymentTypeName;
    }

    public void setPaymentTypeId(Integer id) {
        paymentTypeId = id;
    }

    public void setPaymentTypeName(String name) {
        paymentTypeName = name;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // Write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(paymentTypeId);
        out.writeString(paymentTypeName);
    }

    // This is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<PaymentType> CREATOR = new Parcelable.Creator<PaymentType>() {
        public PaymentType createFromParcel(Parcel in) {
            return new PaymentType(in);
        }

        public PaymentType[] newArray(int size) {
            return new PaymentType[size];
        }
    };
}
