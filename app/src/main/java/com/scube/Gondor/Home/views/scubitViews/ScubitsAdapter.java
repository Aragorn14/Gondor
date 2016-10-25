package com.scube.Gondor.Home.views.scubitViews;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scube.Gondor.R;
import com.scube.Gondor.Home.models.scubitModels.ScubitModel;
import com.scube.Gondor.UI.CircleTransform;
import com.scube.Gondor.UI.TriangleShapeView;
import com.scube.Gondor.Util.DateTimeUtils;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by srikanthsridhara on 5/3/15.
 */
public class ScubitsAdapter extends RecyclerView.Adapter<ScubitsAdapter.ViewHolder> {

    private List<ScubitModel> scubitModelList;
    Context context;
    OnItemClickListener itemClickListener;

    public ScubitsAdapter(List<ScubitModel> scubitList, Activity activity) {
        this.scubitModelList = scubitList;
        this.context = activity;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return scubitModelList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_scubit, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ScubitModel scubitModel = scubitModelList.get(position);

        // Fill Text Data
        if(scubitModel.getOfferName() != null) {
            holder.offerName.setText(scubitModel.getOfferName());
        }
        if(scubitModel.getMallName() != null) {
            holder.mallName.setText(scubitModel.getMallName());
        }
        if(scubitModel.getShopName() != null) {
            holder.shopName.setText(scubitModel.getShopName());
        }
        if(scubitModel.getBrandName() != null) {
            holder.brandName.setText(scubitModel.getBrandName());
        }

        // Compute and set timer
        //computeAndSetTimer(scubitModel, holder);

        // Load Images

        // User profile
        Picasso.with(context)
                .load("https://avatars3.githubusercontent.com/u/7959400?v=3&s=96")
                .placeholder(R.drawable.profile_placeholder)
                .transform(new CircleTransform())
                .into(holder.ownerProfileImage);

        // Brand Wall
        Picasso.with(context)
                .load("http://www.wgsn.com/blogs/wp-content/uploads/2013/03/adidas-boost-promo-hero3-1.jpg")
                .into(holder.brandWall);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView offerName, mallName, shopName, brandName, timerHeading, countdownTimer;
        public ImageView mallIcon, shopIcon, brandWall, ownerProfileImage;
        public TriangleShapeView triangleShapeView;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            offerName = (TextView) itemView.findViewById(R.id.offerName);
            brandName = (TextView) itemView.findViewById(R.id.brandName);
            shopName = (TextView) itemView.findViewById(R.id.shopName);
            mallName = (TextView) itemView.findViewById(R.id.mallName);
            timerHeading = (TextView) itemView.findViewById(R.id.timerHeading);
            countdownTimer = (TextView) itemView.findViewById(R.id.countdownTimer);

            ownerProfileImage = (ImageView) itemView.findViewById(R.id.ownerProfileImage);
            mallIcon = (ImageView) itemView.findViewById(R.id.mallIcon);
            shopIcon = (ImageView) itemView.findViewById(R.id.shopIcon);
            brandWall = (ImageView) itemView.findViewById(R.id.brandWall);

            triangleShapeView = (TriangleShapeView) itemView.findViewById(R.id.timerTriangle);
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

    public void computeAndSetTimer(ScubitModel scubitModel, final ViewHolder holder) {
        Integer status = scubitModel.getStatus();
        long dateDiffMilliseconds = 0;
        Date currentDate = new Date();
        TriangleShapeView triangleShapeView = holder.triangleShapeView;

        if(status.equals(R.integer.scubit_not_started)) {
            // Set Color : Orange
            triangleShapeView.setBgColor(R.color.scubit_not_started);
            holder.timerHeading.setText(R.string.scubit_not_started);
            dateDiffMilliseconds = DateTimeUtils.dateDifferenceMilli(currentDate, scubitModel.getStartDate());
        } else if(status.equals(R.integer.scubit_active)) {
            // Set Color : Green
            triangleShapeView.setBgColor(R.color.scubit_active);
            holder.timerHeading.setText(R.string.scubit_active);
            dateDiffMilliseconds = DateTimeUtils.dateDifferenceMilli(currentDate, scubitModel.getEndDate());
        } else if(status.equals(R.integer.scubit_expired) || status.equals(R.integer.scubit_status_unknown)) {
            // Set Color : Grey
            triangleShapeView.setBgColor(R.color.scubit_expired);
            dateDiffMilliseconds = 0;
        }

        if(dateDiffMilliseconds == 0) {
            holder.timerHeading.setVisibility(View.INVISIBLE);
            holder.countdownTimer.setText("Expired");
        }

        new CountDownTimer(dateDiffMilliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                holder.countdownTimer.setText(DateTimeUtils.getTimeString(millisUntilFinished));
            }

            public void onFinish() {
                holder.countdownTimer.setText("Expired");
            }
        }.start();
    }
}
