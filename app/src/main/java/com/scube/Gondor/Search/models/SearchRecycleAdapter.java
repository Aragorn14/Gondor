package com.scube.Gondor.Search.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scube.Gondor.R;

import java.util.List;

/**
 * Created by vashoka on 8/29/15.
 */
public class SearchRecycleAdapter extends RecyclerView.Adapter<SearchRecycleAdapter.ViewHolder> {

    private List<SearchModel> searchModels;
    private Context context;
    View view;
    OnItemClickListener itemClickListener;

    public SearchRecycleAdapter(Context context, List<SearchModel> searchModels) {
        this.context = context;
        this.searchModels = searchModels;
    }

    public void addItems(List items)
    {
        searchModels.clear();
        searchModels.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchModels.size();
    }

    public SearchModel getItem(int position) {
        return searchModels.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the current Mall object
        SearchModel searchModel = searchModels.get(position);

        // TODO: Set the Mall location from API
        holder.itemLocation.setText("San Jose");

        if(searchModel.getTitle() != null) {
            holder.itemName.setText(searchModel.getTitle());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView itemName, itemLocation;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemLocation = (TextView) itemView.findViewById(R.id.itemLocation);
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
