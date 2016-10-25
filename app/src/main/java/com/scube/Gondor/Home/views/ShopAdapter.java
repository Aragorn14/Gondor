package com.scube.Gondor.Home.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scube.Gondor.Home.models.Shop;
import com.scube.Gondor.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vashoka on 5/1/15.
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private List<Shop> shops;
    private Context context;
    View view;
    OnItemClickListener itemClickListener;

    public ShopAdapter(Context context, List<Shop> shops) {
        this.context = context;
        this.shops = shops;
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }
    public Shop getItem(int position) {
        return shops.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the current Shop object
        Shop shop = shops.get(position);

        // TODO: Set the Shop location from API
        holder.shopLocation.setText("San Jose");

        if(shop.getShopName() != null) {
            holder.shopName.setText(shop.getShopName());
        }
        if(shop.getScubitCount() != null) {
            holder.scubitCount.setText(shop.getScubitCount().toString());
        }

        String shopBackground = shop.getShopBackground("hdpi");

        if(!shopBackground.isEmpty()) {
            Picasso.with(context)
                    .load(shopBackground)
                    .into(holder.shopBackgroundImage);
        } else {
            Picasso.with(context)
                    .load(R.drawable.image_placeholder)
                    .into(holder.shopBackgroundImage);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView shopLocation, shopName, scubitCount;
        public View view;
        public ImageView shopBackgroundImage;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            shopLocation = (TextView) itemView.findViewById(R.id.shopLocation);
            shopName = (TextView) itemView.findViewById(R.id.shopName);
            scubitCount = (TextView) itemView.findViewById(R.id.scubitCount);
            shopBackgroundImage = (ImageView) itemView.findViewById(R.id.shopBackgroundImage);
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

    public long getItemId(int position) { return position;}
}
