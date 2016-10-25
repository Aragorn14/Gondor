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

import com.scube.Gondor.Helpers.api.BrandApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.models.Brand;
import com.scube.Gondor.Home.views.BrandAdapter;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import java.util.ArrayList;

/**
 * Created by vashoka on 5/1/15.
 */
public class BrandsFragment extends Fragment {

    private ProgressDialog pDialog;
    private ArrayList<Brand> brands = new ArrayList<Brand>();
    private BrandAdapter brandAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView brandsList;
    Context context;

    public static final String TAB_INDEX = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_brands, container, false);

        context = getActivity().getApplicationContext();
        brandsList = (RecyclerView) v.findViewById(R.id.brandsList);

        // Set Layout manager
        brandsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.brandsRefreshLayout);
        refreshLayout.setColorSchemeColors(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                Log.d(getString(R.string.Brands_Fragment), "Pulled to Refresh!");

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        refreshLayout.setRefreshing(false);

                        // Fetch latest brands data
                        //getBrandsBasedOnLoc();
                    }
                }, 2000);
            }
        });

        // What format of items to Store ?
        brandAdapter = new BrandAdapter(context, brands);
        brandsList.setAdapter(brandAdapter);

        brandAdapter.SetOnItemClickListener(new BrandAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("Clicked", "Clicked" + brandAdapter.getItem(position));
                // Get the Brand object from the Brand Adapter
                Brand brand = brandAdapter.getItem(position);

                // Load brand into bundle to pass it over to fragment
                Bundle bundle = new Bundle();
                bundle.putString("profile", "brand");
                bundle.putParcelable("profileObject", brand);

                HomeActivity home = (HomeActivity) getActivity();
                ShopProfilesFragment profilesFragment = new ShopProfilesFragment();
                profilesFragment.setArguments(bundle);
                home.pushFragmentToStack(profilesFragment);
            }
        });

        // Display the progress dialog before making HTTP requests
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading Brands...");
        pDialog.show();

        getBrandsBasedOnLoc();

        // Add tag for accessibility
        v.setTag("BrandsFragment");

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

    private void getBrandsBasedOnLoc() {
        String loc_id = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location));
        BrandApiHelper brandApiHelper = new BrandApiHelper(context, "BrandsFragment");
        brandApiHelper.getBrandsByLocation(Integer.parseInt(loc_id), new ApiResponse.Listener<ArrayList<Brand>>() {

            @Override
            public void onResponse(ArrayList<Brand> filledBrandsList) {
                hidePDialog();
                Log.d(context.getString(R.string.Brands_Fragment), "Received brands response from brand api helper");
                brands.clear();
                brands.addAll(filledBrandsList);

                // Notify brand adapter about data changes so that
                // it renders the list view with updated data
                brandAdapter.notifyDataSetChanged();
            }
        });
    }
}
