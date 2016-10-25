package com.scube.Gondor.Home.models.scubitModels;

import android.content.Context;

import com.scube.Gondor.Chat.models.Dialog;
import com.scube.Gondor.Util.Auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikanthsridhara on 6/27/15.
 */
public class MyScubitModel {

    private Integer scubitId;
    private Integer userId;
    private List<Dialog> dialogs = new ArrayList<Dialog>();
    private Integer brandId;
    private String  brandName;
    private Integer shopProfileId;
    private String  shopName;
    private Integer mallId;
    private String  mallName;
    private Integer offerId;
    private String  offerName;
    private Integer priceRangeId;
    private String  priceRangeName;
    private Integer paymentId;
    private String  paymentName;
    private Integer numItems;
    private String  startDate;
    private String  endDate;
    private String  notes;
    private String  photoUrl;
    private Integer valid;


    public MyScubitModel(JSONObject scubit, Context thisContext) {
        final Context context = thisContext;
        try {
            this.userId         = Auth.getLoginDetails(context).getInt("scubeId");
            this.scubitId       = (scubit.has("scubit_id") && !scubit.isNull("scubit_id")) ? scubit.getInt("scubit_id") : null;
            this.brandId        = (scubit.has("brand_id") && !scubit.isNull("brand_id")) ? scubit.getInt("brand_id") : -1;
            this.brandName      = (scubit.has("brand_name") && !scubit.isNull("brand_name")) ? scubit.getString("brand_name") : "";
            this.shopProfileId  = (scubit.has("shop_profile_id") && !scubit.isNull("shop_profile_id")) ? scubit.getInt("shop_profile_id") : -1;
            this.shopName       = (scubit.has("shop_name") && !scubit.isNull("shop_name")) ? scubit.getString("shop_name") : "";
            this.mallId         = (scubit.has("mall_id") && !scubit.isNull("mall_id")) ? scubit.getInt("mall_id") : -1;
            this.mallName       = (scubit.has("mall_name") && !scubit.isNull("mall_name")) ? scubit.getString("mall_name") : "";
            this.offerId        = (scubit.has("offer_id") && !scubit.isNull("offer_id")) ? scubit.getInt("offer_id") : -1;
            this.offerName      = (scubit.has("offer_name") && !scubit.isNull("offer_name")) ? scubit.getString("offer_name") : "";
            this.priceRangeId   = (scubit.has("price_range_id") && !scubit.isNull("price_range_id")) ? scubit.getInt("price_range_id") : -1;
            this.priceRangeName = (scubit.has("price_range_name") && !scubit.isNull("price_range_name")) ? scubit.getString("price_range_name") : "";
            this.paymentId      = (scubit.has("payment_type_id") && !scubit.isNull("payment_type_id")) ? scubit.getInt("payment_type_id") : -1;
            this.paymentName    = (scubit.has("payment_type_name") && !scubit.isNull("payment_type_name")) ? scubit.getString("payment_type_name") : "";
            this.numItems       = (scubit.has("num_items") && !scubit.isNull("num_items")) ? scubit.getInt("num_items") : -1;
            this.startDate      = (scubit.has("start_date") && !scubit.isNull("start_date")) ? scubit.getString("start_date") : "";
            this.endDate        = (scubit.has("end_date") && !scubit.isNull("end_date")) ? scubit.getString("end_date") : "";
            this.notes          = (scubit.has("notes") && !scubit.isNull("notes")) ? scubit.getString("notes") : "";
            this.photoUrl       = (scubit.has("photo_url") && !scubit.isNull("photo_url")) ? scubit.getString("photo_url") : "";
            this.valid          = (scubit.has("valid") && !scubit.isNull("valid")) ? scubit.getInt("valid") : -1;



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getScubitId() {
        return this.scubitId;
    }

    public void setScubitId(Integer scubitId) {
        this.scubitId = scubitId;
    }

    public Integer getUserId() {
        return userId;
    }

    public List<Dialog> getDialogs() {
        return this.dialogs;
    }

    public void addDialog(Dialog dialog) {
        dialogs.add(dialog);
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMallName() {
        return mallName;
    }

    public void setMallName(String mallName) {
        this.mallName = mallName;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getPriceRangeName() {
        return priceRangeName;
    }

    public void setPriceRangeName(String priceRangeName) {
        this.priceRangeName = priceRangeName;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getStartDate () {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getShopProfileId() {
        return shopProfileId;
    }

    public void setShopProfileId(Integer shopProfileId) {
        this.shopProfileId = shopProfileId;
    }

    public Integer getMallId() {
        return mallId;
    }

    public void setMallId(Integer mallId) {
        this.mallId = mallId;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Integer getPriceRangeId() {
        return priceRangeId;
    }

    public void setPriceRangeId(Integer priceRangeId) {
        this.priceRangeId = priceRangeId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getNumItems() {
        return numItems;
    }

    public void setNumItems(Integer numItems) {
        this.numItems = numItems;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

}
