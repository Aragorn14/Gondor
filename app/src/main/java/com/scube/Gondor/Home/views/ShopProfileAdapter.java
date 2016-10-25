package com.scube.Gondor.Home.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vashoka on 5/15/15.
 */
public class ShopProfileAdapter extends RecyclerView.Adapter<ShopProfileAdapter.ViewHolder> {

    private List<ShopProfile> shopProfiles;
    private String profile;
    private Context context;
    View view;
    OnItemClickListener itemClickListener;

    public ShopProfileAdapter(Context context, List<ShopProfile> shopProfiles, String profile) {
        this.context = context;
        this.shopProfiles = shopProfiles;
        this.profile = profile;
    }

    public ShopProfile getItem(int position) {
        return shopProfiles.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflating our layout for shop profile based on the received shop profile adapter,
        // creating a View class & returning it to be appended to the parent Shop profiles ListView.
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_shopprofile, parent, false);

        // TODO: Use 'profile' to decide what all to show.
        /*
        if (profile.equals("mall")) {

        } else if (profile.equals("brand")) {

        } else if (profile.equals("shop")) {

        }
        */

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the current Shop profile object
        ShopProfile shopProfile = shopProfiles.get(position);

        if(shopProfile.getShopName() != null) {
            holder.mallName.setText(shopProfile.getMallName());
        }
        if(shopProfile.getShopName() != null) {
            holder.shopName.setText(shopProfile.getShopName());
        }
        if(shopProfile.getScubitCount() != null) {
            holder.scubitCount.setText(shopProfile.getScubitCount().toString());
        }

        String shopProfileBackground = shopProfile.getShopProfileBackground("hdpi");

        if(!shopProfileBackground.isEmpty()) {
            Picasso.with(context)
                    .load(shopProfileBackground)
                    .into(holder.shopProfileBackgroundImage);
        } else {
            Picasso.with(context)
                    .load(R.drawable.image_placeholder)
                    .into(holder.shopProfileBackgroundImage);
        }
    }

    @Override
    public int getItemCount() {
        return shopProfiles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mallName, shopName, scubitCount;
        public View view;
        public ImageView shopProfileBackgroundImage;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            mallName = (TextView) itemView.findViewById(R.id.mallName);
            shopName = (TextView) itemView.findViewById(R.id.shopName);
            scubitCount = (TextView) itemView.findViewById(R.id.scubitCount);
            shopProfileBackgroundImage = (ImageView) itemView.findViewById(R.id.shopProfileBackgroundImage);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}

