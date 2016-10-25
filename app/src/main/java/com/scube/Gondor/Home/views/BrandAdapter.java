package com.scube.Gondor.Home.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scube.Gondor.Home.models.Brand;
import com.scube.Gondor.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vashoka on 5/1/15.
 */
public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    private List<Brand> brands;
    private Context context;
    View view;
    OnItemClickListener itemClickListener;

    public BrandAdapter(Context context, List<Brand> brands) {
        this.context = context;
        this.brands = brands;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public Brand getItem(int position) {
        return brands.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_brand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the current Brand object
        Brand brand = brands.get(position);

        if(brand.getBrandId() != null) {
            holder.brandLocation.setText(brand.getBrandId().toString());
        }
        if(brand.getBrandName() != null) {
            holder.brandName.setText(brand.getBrandName());
        }
        if(brand.getScubitCount() != null) {
            holder.scubitCount.setText(brand.getScubitCount().toString());
        }

        String brandBackground = brand.getBrandBackground("hdpi");

        if(!brandBackground.isEmpty()) {
            Picasso.with(context)
                    .load(brandBackground)
                    .into(holder.brandBackgroundImage);
        } else {
            Picasso.with(context)
                    .load(R.drawable.image_placeholder)
                    .into(holder.brandBackgroundImage);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView brandLocation, brandName, scubitCount;
        public View view;
        private ImageView brandBackgroundImage;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            brandLocation = (TextView) itemView.findViewById(R.id.brand);
            brandName = (TextView) itemView.findViewById(R.id.brandName);
            scubitCount = (TextView) itemView.findViewById(R.id.scubitCount);
            brandBackgroundImage = (ImageView) itemView.findViewById(R.id.brandBackgroundImage);
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
