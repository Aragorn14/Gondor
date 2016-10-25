package com.scube.Gondor.Home.models.scubitModels;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.scube.Gondor.Chat.models.DataHolder;
import com.scube.Gondor.Chat.models.Dialog;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.Auth;
import com.scube.Gondor.Util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by srikanthsridhara on 5/9/15.
 */
public class ScubitModel {

    private String scubitId, originUserName, originUserQBId, originUserScubeId, lastMessage;
    private QBDialog dialog;
    private Context context;

    private Integer userId;
    private List<Dialog> dialogs = new ArrayList<Dialog>();
    private Integer brandId;
    private String  brandName;
    private Integer brandValid;
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

    public ScubitModel(JSONObject scubit, List<QBDialog> userDialogs, Context thisContext) throws JSONException {

        context = thisContext;
        this.originUserQBId = (scubit.has("qb_user_id") && !scubit.isNull("qb_user_id")) ? scubit.getString("qb_user_id") : null;
        this.originUserScubeId = (scubit.has("user_id") && !scubit.isNull("user_id")) ? scubit.getString("user_id") : null;
        this.userId = Auth.getLoginDetails(context).getInt("scubeId");
        this.scubitId = (scubit.has("scubit_id") && !scubit.isNull("scubit_id")) ? scubit.getString("scubit_id") : null;
        this.brandId = (scubit.has("brand_id") && !scubit.isNull("brand_id")) ? scubit.getInt("brand_id") : -1;
        this.brandName = (scubit.has("brand_name") && !scubit.isNull("brand_name")) ? scubit.getString("brand_name") : "";
        this.brandValid = (scubit.has("brand_valid") && !scubit.isNull("brand_valid")) ? scubit.getInt("brand_valid") : -1;
        this.shopProfileId = (scubit.has("shop_profile_id") && !scubit.isNull("shop_profile_id")) ? scubit.getInt("shop_profile_id") : -1;
        this.shopName = (scubit.has("shop_name") && !scubit.isNull("shop_name")) ? scubit.getString("shop_name") : "";
        this.mallId = (scubit.has("mall_id") && !scubit.isNull("mall_id")) ? scubit.getInt("mall_id") : -1;
        this.mallName = (scubit.has("mall_name") && !scubit.isNull("mall_name")) ? scubit.getString("mall_name") : "";
        this.offerId = (scubit.has("offer_id") && !scubit.isNull("offer_id")) ? scubit.getInt("offer_id") : -1;
        this.offerName = (scubit.has("offer_name") && !scubit.isNull("offer_name")) ? scubit.getString("offer_name") : "";
        this.priceRangeId = (scubit.has("price_range_id") && !scubit.isNull("price_range_id")) ? scubit.getInt("price_range_id") : -1;
        this.priceRangeName = (scubit.has("price_range_name") && !scubit.isNull("price_range_name")) ? scubit.getString("price_range_name") : "";
        this.paymentId = (scubit.has("payment_type_id") && !scubit.isNull("payment_type_id")) ? scubit.getInt("payment_type_id") : -1;
        this.paymentName = (scubit.has("payment_type_name") && !scubit.isNull("payment_type_name")) ? scubit.getString("payment_type_name") : "";
        this.numItems = (scubit.has("num_items") && !scubit.isNull("num_items")) ? scubit.getInt("num_items") : -1;
        this.startDate = (scubit.has("start_date") && !scubit.isNull("start_date")) ? scubit.getString("start_date") : "";
        this.endDate = (scubit.has("end_date") && !scubit.isNull("end_date")) ? scubit.getString("end_date") : "";
        this.notes = (scubit.has("notes") && !scubit.isNull("notes")) ? scubit.getString("notes") : "";
        this.photoUrl = (scubit.has("photo_url") && !scubit.isNull("photo_url")) ? scubit.getString("photo_url") : "";

        // get dialogs
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        // populate my dialogs list
        QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> userDialogs, Bundle args) {

                // Out of all my scubit dialogs, if the scubit id matches the current scubit,
                // set the dialog of the scubit model and the last message
                for (QBDialog userDialog : userDialogs) {
                    Map<String, String> data = userDialog.getData();
                    String result_scubit_id = data.get("scubit_id");
                    String result_qb_user_id = data.get("qb_user_id");

                    // Scubit Id should match and I should not be the creator of that scubit
                    if (result_scubit_id.equals(scubitId) && !result_qb_user_id.equals(Integer.toString(DataHolder.getDataHolder().getSignInUserId()))) {
                        Log.d("Scubit Model", "scubit_data :" + data);
                        Log.d("Scubit Model", "scubit_id   :" + result_scubit_id);
                        Log.d("Scubit Model", "scubit_owner:" + result_qb_user_id);
                        Log.d("Scubit Model", "Scubit ID match found: " + scubitId);
                        dialog = userDialog;
                        lastMessage = userDialog.getLastMessage();
                        break;
                    }
                }
            }

            @Override
            public void onError(List<String> errors) {
                Log.e("Scubit Model", "getChatDialogs failure");
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("get dialogs errors: " + errors).create().show();
            }
        });
    }

    public String getScubitId() {
        return scubitId;
    }

    public void setScubitId(String scubitId) {
        this.scubitId = scubitId;
    }

    public String getOriginUserName() {
        return originUserName;
    }

    public void setOriginUserName(String originUserName) {
        this.originUserName = originUserName;
    }

    public String getOriginUserScubeId() {
        return originUserScubeId;
    }

    public void setOriginUserScubeId(String originUserId) {
        this.originUserScubeId = originUserId;
    }

    public String getOriginUserQBId() {
        return originUserQBId;
    }

    public void setOriginUserQBId(String originUserId) {
        this.originUserQBId = originUserId;
    }

    public String getLastMessage() {
        if (this.lastMessage == null) {
            return "-";
        } else {
            return lastMessage;
        }
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public QBDialog getDialog() {
        return dialog;
    }

    public void setDialog(QBDialog dialog) {
        this.dialog = dialog;
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
        return StringUtil.PascalCase(brandName);
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Integer getBrandValid() {
        return brandValid;
    }

    public void setBrandValid(Integer brandValid) {
        this.brandValid = brandValid;
    }

    public String getShopName() {
        return StringUtil.PascalCase(shopName);
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMallName() {
        return StringUtil.PascalCase(mallName);
    }

    public void setMallName(String mallName) {
        this.mallName = mallName;
    }

    public String getOfferName() {
        return StringUtil.PascalCase(offerName);
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
        return StringUtil.PascalCase(paymentName);
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public Date getStartDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");
        try {
            Date startDate = format.parse(this.startDate);
            return startDate;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");
        try {
            Date endDate = format.parse(this.endDate);
            return endDate;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
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

    // start_date : "2015-07-04T11:16:00.000Z"
    // end_date   : "2015-07-04T15:16:00.000Z"
    public Integer getStatus() {
        Date startDate = this.getStartDate();
        Date endDate = this.getEndDate();

        Date currentDate = new Date();
        if(currentDate.before(startDate)) {
            // Scubit schedules to start in the future.
            return context.getResources().getInteger(R.integer.scubit_not_started);
        } else if(currentDate.after(startDate) && currentDate.before(endDate)) {
            // Scubit currently active
            return context.getResources().getInteger(R.integer.scubit_active);
        } else if(currentDate.equals(endDate) || currentDate.after(endDate)) {
            // Scubit Expired
            return context.getResources().getInteger(R.integer.scubit_expired);
        }

        return context.getResources().getInteger(R.integer.scubit_status_unknown);
    }
}
