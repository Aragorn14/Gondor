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

import com.scube.Gondor.Helpers.api.MallApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.Home.views.MallAdapter;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import java.util.ArrayList;

// Network : Volley

/**
 * Created by vashoka on 5/1/15.
 */
public class MallsFragment extends Fragment {

    private ProgressDialog pDialog;
    private ArrayList<Mall> malls = new ArrayList<Mall>();
    private MallAdapter mallAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mallsList;
    Context context;

    public static final String TAB_INDEX = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_malls, container, false);

        context = getActivity().getApplicationContext();

        mallsList = (RecyclerView) v.findViewById(R.id.mallsList);

        // Set Layout manager
        mallsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.mallsRefreshLayout);
        refreshLayout.setColorSchemeColors(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                Log.d(getString(R.string.Malls_Fragment), "Pulled to Refresh!");

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        refreshLayout.setRefreshing(false);

                        // Fetch latest malls data
                        //getMallsBasedOnLoc();
                    }
                }, 2000);
            }
        });

        // What format of items to Store ?
        mallAdapter = new MallAdapter(context, malls);
        mallsList.setAdapter(mallAdapter);

        mallAdapter.SetOnItemClickListener(new MallAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("Clicked", "Clicked" + mallAdapter.getItem(position));
                // Get the Mall object from the Mall Adapter
                Mall mall = (Mall) mallAdapter.getItem(position);

                // Load mall into bundle to pass it over to fragment
                Bundle bundle = new Bundle();
                bundle.putString("profile", "mall");
                bundle.putParcelable("profileObject", mall);

                HomeActivity home = (HomeActivity) getActivity();
                ShopProfilesFragment profilesFragment = new ShopProfilesFragment();
                profilesFragment.setArguments(bundle);
                home.pushFragmentToStack(profilesFragment);
            }
        });

        // Display the progress dialog before making HTTP requests
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading Malls...");
        pDialog.show();

        getMallsBasedOnLoc();

        // Add tag for accessibility
        v.setTag("MallsFragment");

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

    private void getMallsBasedOnLoc() {
        String loc_id = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location));
        MallApiHelper mallApiHelper = new MallApiHelper(context, "MallsFragment");
        mallApiHelper.getMallsByLocation(Integer.parseInt(loc_id), new ApiResponse.Listener<ArrayList<Mall>>() {

            @Override
            public void onResponse(ArrayList<Mall> filledMallsList) {
                hidePDialog();
                Log.d(context.getString(R.string.Malls_Fragment), "Received malls response from mall api helper");
                malls.clear();
                malls.addAll(filledMallsList);

                // Notify mall adapter about data changes so that
                // it renders the list view with updated data
                mallAdapter.notifyDataSetChanged();
            }
        });
    }
}
