package com.scube.Gondor.Home.views.scubitViews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scube.Gondor.R;
import com.scube.Gondor.Home.models.scubitModels.PaymentType;

import java.util.List;

/**
 * Created by vashoka on 7/4/15.
 */
public class PaymentTypeAdapter extends BaseAdapter {
    private Fragment fragment;
    private LayoutInflater inflater;
    private List<PaymentType> paymentTypes;
    private Context context;
    private int viewMode;

    public PaymentTypeAdapter(Fragment fragment, Context context, List<PaymentType> paymentTypes, int viewMode) {
        this.fragment = fragment;
        this.context = context;
        this.paymentTypes = paymentTypes;
        this.viewMode = viewMode;
    }

    @Override
    public int getCount() {
        return paymentTypes.size();
    }

    @Override
    public Object getItem(int location) {
        return paymentTypes.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * Called every time a row is to be added in the ListView
     * @params
     * row item position count (position)
     * custom layout view object (convertView)
     * parent ListView object (parent)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(this.context.getResources().getInteger(R.integer.offer_list_item_mode) == this.viewMode) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.card_view_brand, null);
            }

            // TODO
        } else if(context.getResources().getInteger(R.integer.offer_spinner_item_mode) == this.viewMode) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.scube_spinner_item, null);
            }

            TextView offerName = (TextView) convertView.findViewById(R.id.spinnerItemText);
            // Get the current PaymentType object
            PaymentType paymentType = paymentTypes.get(position);
            offerName.setText(paymentType.getPaymentTypeName());

            convertView.setTag(R.id.spinnerItemText, paymentType);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
