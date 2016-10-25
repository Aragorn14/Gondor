package com.scube.Gondor.Home.controllers.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scube.Gondor.Helpers.api.ShopApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.models.Shop;
import com.scube.Gondor.Home.views.ShopAdapter;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import java.util.ArrayList;

/**
 * Created by vashoka on 5/1/15.
 */
public class ShopsFragment extends Fragment {

    private ProgressDialog pDialog;
    private ArrayList<Shop> shops = new ArrayList<Shop>();
    private ShopAdapter shopAdapter;
    private SwipeRefreshLayout refreshLayout;
    Context context;
    private RecyclerView shopsList;

    public static final String TAB_INDEX = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shops, container, false);

        context = getActivity().getApplicationContext();

        shopsList = (RecyclerView) v.findViewById(R.id.shopsList);

        // Set Layout manager
        shopsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.shopsRefreshLayout);
        refreshLayout.setColorSchemeColors(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                Log.d(getString(R.string.Shops_Fragment), "Pulled to Refresh!");

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        refreshLayout.setRefreshing(false);

                        // Fetch latest shops data
                        //getShopsBasedOnLoc();
                    }
                }, 2000);
            }
        });

        // What format of items to Store ?
        shopAdapter = new ShopAdapter(context, shops);
        shopsList.setAdapter(shopAdapter);

        shopAdapter.SetOnItemClickListener(new ShopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("Clicked", "Clicked" + shopAdapter.getItem(position));
                // Get the Mall object from the Mall Adapter
                Shop shop = (Shop) shopAdapter.getItem(position);

                // Load mall into bundle to pass it over to fragment
                Bundle bundle = new Bundle();
                bundle.putString("profile", "shop");
                bundle.putParcelable("profileObject", shop);

                HomeActivity home = (HomeActivity) getActivity();
                ShopProfilesFragment profilesFragment = new ShopProfilesFragment();
                profilesFragment.setArguments(bundle);
                home.pushFragmentToStack(profilesFragment);
            }
        });


        // Display the progress dialog before making HTTP requests
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading Shops...");
        pDialog.show();

        getShopsBasedOnLoc();

        // Add tag for accessibility
        v.setTag("ShopsFragment");

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        hidePDialog();
    }

    public void hidePDialog() {
        if(pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void getShopsBasedOnLoc() {
        String loc_id = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location));
        ShopApiHelper shopApiHelper = new ShopApiHelper(context, "ShopsFragment");
        shopApiHelper.getShopsByLocation(Integer.parseInt(loc_id), new ApiResponse.Listener<ArrayList<Shop>>() {

            @Override
            public void onResponse(ArrayList<Shop> filledShopsList) {
                hidePDialog();
                Log.d(context.getString(R.string.Shops_Fragment), "Received shops response from shop api helper");
                shops.clear();
                shops.addAll(filledShopsList);

                // Notify shop adapter about data changes so that
                // it renders the list view with updated data
                shopAdapter.notifyDataSetChanged();
            }
        });
    }
}
