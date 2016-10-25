package com.scube.Gondor.Home.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vashoka on 5/1/15.
 */
public class MallAdapter extends RecyclerView.Adapter<MallAdapter.ViewHolder> {

    private List<Mall> malls;
    private Context context;
    View view;
    OnItemClickListener itemClickListener;

    public MallAdapter(Context context, List<Mall> malls) {
        this.context = context;
        this.malls = malls;
    }

    @Override
    public int getItemCount() {
        return malls.size();
    }
    public Mall getItem(int position) {
        return malls.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mall, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the current Mall object
        Mall mall = malls.get(position);

        // TODO: Set the Mall location from API
        holder.mallLocation.setText("San Jose");

        if(mall.getMallName() != null) {
            holder.mallName.setText(mall.getMallName());
        }
        if(mall.getScubitCount() != null) {
            holder.scubitCount.setText(mall.getScubitCount().toString());
        }

        String mallBackground = mall.getMallBackground("hdpi");

        if(!mallBackground.isEmpty()) {
            Picasso.with(context)
                    .load(mallBackground)
                    .into(holder.mallBackgroundImage);
        } else {
            Picasso.with(context)
                    .load(R.drawable.image_placeholder)
                    .into(holder.mallBackgroundImage);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mallLocation, mallName, scubitCount;
        public View view;
        public ImageView mallBackgroundImage;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            mallLocation = (TextView) itemView.findViewById(R.id.mallLocation);
            mallName = (TextView) itemView.findViewById(R.id.mallName);
            scubitCount = (TextView) itemView.findViewById(R.id.scubitCount);
            mallBackgroundImage = (ImageView) itemView.findViewById(R.id.mallBackgroundImage);
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
