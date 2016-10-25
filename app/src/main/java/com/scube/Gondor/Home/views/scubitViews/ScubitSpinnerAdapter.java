package com.scube.Gondor.Home.views.scubitViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scube.Gondor.Home.models.Brand;
import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.R;

import java.util.List;

/**
 * Created by srikanthsridhara on 8/8/15.
 */
public class ScubitSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

    LayoutInflater inflater;
    private List<Mall> malls;
    private List<Brand> brands;
    private List<ShopProfile> shopProfiles;
    private Context context;
    private String entityType = "";

    public ScubitSpinnerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        switch (entityType) {
            case "malls":        return malls.size();
            case "shopProfiles": return shopProfiles.size();
            case "brands":       return brands.size();
            default:             return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        switch (entityType) {
            case "malls":        return malls.get(position);
            case "shopProfiles": return shopProfiles.get(position);
            case "brands":       return brands.get(position);
            default:             return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.scube_spinner_item, null);
        }

        TextView entityName = (TextView) convertView.findViewById(R.id.spinnerItemText);
        switch (entityType) {
            case "malls":
                entityName.setText(malls.get(position).getMallName());
                convertView.setTag(R.id.spinnerItemText, malls.get(position));
                break;
            case "shopProfiles":
                entityName.setText(shopProfiles.get(position).getShopName());
                convertView.setTag(R.id.spinnerItemText, shopProfiles.get(position));
                break;
            case "brands":
                entityName.setText(brands.get(position).getBrandName());
                convertView.setTag(R.id.spinnerItemText, brands.get(position));
                break;
        }
        return convertView;
    }

    public void setList(List genericList, String entityType) {
        this.entityType = entityType;

        switch (entityType) {
            case "malls":
                this.malls = genericList;
                break;
            case "shopProfiles":
                this.shopProfiles = genericList;
                break;
            case "brands":
                this.brands = genericList;
                break;
        }
    }
}
