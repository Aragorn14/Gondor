package com.scube.Gondor.Home.controllers.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Helpers.api.ShopProfileApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.controllers.fragments.scubitFragments.ScubitsListFragment;
import com.scube.Gondor.Home.models.Brand;
import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.Home.models.Shop;
import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.Home.views.ShopProfileAdapter;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.Auth;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;
import com.scube.Gondor.Util.StringUtil;

import java.util.ArrayList;

/**
 * Created by vashoka on 7/11/15.
 */
public class ShopProfilesFragment extends NavigationFragment {
    private Context context;
    public Bundle bundle = new Bundle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopprofiles, container, false);
        context = getActivity().getApplicationContext();
        // Save Activity
        GlobalUtils.addSharedPreference(context, getString(R.string.sp_nav), getString(R.string.sp_latest_activity), context.getClass().getSimpleName());

        bundle = this.getArguments();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        ShopProfilesListFragment shopProfilesListFragment = new ShopProfilesListFragment();

        // Pass profile and profile object for list context
        shopProfilesListFragment.setArguments(bundle);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.shopprofiles_list_fragment_container, shopProfilesListFragment);
        ft.commit();

        // Add tag for accessibility
        v.setTag("ShopProfilesFragment");

        return v;
    }

    /**
     * Created by vashoka on 5/15/15.
     */
    public static class ShopProfilesListFragment extends Fragment {

        private ProgressDialog pDialog;
        private ArrayList<ShopProfile> shopProfiles = new ArrayList<ShopProfile>();
        private ShopProfileAdapter shopProfileAdapter;
        private SwipeRefreshLayout refreshLayout;
        Context context;
        private String profile;
        private Mall mall;
        private Shop shop;
        private Brand brand;
        private RecyclerView shopProfilesList;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_shopprofiles_list, container, false);

            context = getActivity().getApplicationContext();
            String profileName = "";

            // Fetch the bundle received to get profile and profileObject
            Bundle bundle = this.getArguments();
            profile = "mall"; // default value if not received from activity
            if (bundle != null) {
                profile = bundle.getString("profile", "mall");

                if(profile.equals("mall")) {
                    mall = bundle.getParcelable("profileObject");
                    profileName = mall.getMallName();
                } else if(profile.equals("shop")) {
                    shop = bundle.getParcelable("profileObject");
                    profileName = shop.getShopName();
                } else if(profile.equals("brand")) {
                    brand = bundle.getParcelable("profileObject");
                    profileName = brand.getBrandName();
                } else {
                    Log.d(getString(R.string.ShopProfiles_Fragment), "Correct profile object not received");
                }
            }

            shopProfilesList = (RecyclerView) v.findViewById(R.id.shopProfilesList);

            // Set Layout manager
            shopProfilesList.setLayoutManager(new LinearLayoutManager(getActivity()));

            refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.shopProfilesRefreshLayout);
            refreshLayout.setColorSchemeColors(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshLayout.setRefreshing(true);
                    Log.d(getString(R.string.ShopProfiles_Fragment), "Pulled to Refresh!");

                    new Handler().postDelayed(new Runnable() {
                        @Override public void run() {
                            refreshLayout.setRefreshing(false);

                            // Fetch latest shops data based on profile
                            //getShopsBasedOnProfile(true);
                        }
                    }, 2000);
                }
            });

            // Load the appropriate profile context to load the list of shop profiles
            shopProfileAdapter = new ShopProfileAdapter(context, shopProfiles, profile);
            shopProfilesList.setAdapter(shopProfileAdapter);

            shopProfileAdapter.SetOnItemClickListener(new ShopProfileAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {
                    Log.d("Clicked", "Clicked" + shopProfileAdapter.getItem(position));

                    // Upon clicking a scubit, if user is not logged in, make him login
                    Intent intent;

                    // If logged in, navigate to Scubits activity. Load list of all the scubits based on shop profile id
                    if(Auth.isLoggedIn(context)) {
//
                        // Get the Shop object from the Shop Adapter
                        ShopProfile shopProfile = (ShopProfile) shopProfileAdapter.getItem(position);
//                        intent = new Intent(getActivity(), HomeActivity.class);
//                        // Pass the parcelled shop object
//                        intent.putExtra("shopProfile", shopProfile);
//                        intent.putExtra("scubitsFragments", "scubitsFragments");
//                        startActivity(intent);

                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.pushFragmentToStack(ScubitsListFragment.class, shopProfile, "shopProfile");

                    } else {
                        // Not logged in : Display alert login dialog to request user to login.
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.showLoginDialog(context.getString(R.string.login_request_title_to_add_scubit));
                    }
                }
            });

            // Display the progress dialog before making HTTP requests
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Your Favourite Shops based on " + profileName);
            pDialog.show();

            getShopsBasedOnProfile(false);

            // Add tag for accessibility
            v.setTag("ShopProfilesListFragment");

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

        private void getShopsBasedOnProfile(Boolean refreshing) {

            Integer entityId = -1;
            if(profile.equals("mall")) {
                entityId = mall.getMallId();
            } else if(profile.equals("shop")) {
                entityId = shop.getShopId();
            } else if(profile.equals("brand")) {
                entityId = brand.getBrandId();
            }

            ShopProfileApiHelper shopProfileApiHelper = new ShopProfileApiHelper(context, "ShopProfilesFragment", profile, entityId);
            String loc_id = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location));

            shopProfileApiHelper.getShopProfilesByLocation(Integer.parseInt(loc_id), new ApiResponse.Listener<ArrayList<ShopProfile>>() {

                @Override
                public void onResponse(ArrayList<ShopProfile> filledShopProfilesList) {
                    hidePDialog();
                    Log.d(context.getString(R.string.ShopProfiles_Fragment), "Received shopProfiles response from shopProfile api helper");
                    shopProfiles.clear();
                    shopProfiles.addAll(filledShopProfilesList);

                    // Notify shop adapter about data changes so that
                    // it renders the list view with updated data
                    shopProfileAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
