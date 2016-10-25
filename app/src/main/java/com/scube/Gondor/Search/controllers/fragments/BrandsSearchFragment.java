package com.scube.Gondor.Search.controllers.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scube.Gondor.Helpers.api.SearchApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.R;
import com.scube.Gondor.Search.models.SearchModel;
import com.scube.Gondor.Search.models.SearchRecycleAdapter;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import java.util.ArrayList;

/**
 * Created by vashoka on 8/29/15.
 */
public class BrandsSearchFragment extends Fragment {

    private ProgressDialog pDialog;
    private ArrayList<SearchModel> searchBrandResults = new ArrayList<SearchModel>();
    private SearchRecycleAdapter searchRecycleAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView searchBrandsList;
    Context context;

    public static final String TAB_INDEX = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_brands_search, container, false);

        context = getActivity().getApplicationContext();

        searchBrandsList = (RecyclerView) v.findViewById(R.id.brandsList);
        // Set Layout manager
        searchBrandsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        // What format of items to Store ?
        searchRecycleAdapter = new SearchRecycleAdapter(context, searchBrandResults);
        searchBrandsList.setAdapter(searchRecycleAdapter);

        searchRecycleAdapter.SetOnItemClickListener(new SearchRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("Clicked", "Clicked" + searchRecycleAdapter.getItem(position));
                // Get the Mall object from the Mall Adapter
                SearchModel searchModel = (SearchModel) searchRecycleAdapter.getItem(position);

                Log.d(getString(R.string.Brands_Search_Fragment), "User Clicked a Brand Profile item in search results");
                // Load all the scubits based on this brand profile
                ShopProfile brandProfile = searchModel.getBrandProfile();

                if (GlobalUtils.containsSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_id))) {
                    String userId = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_id));
                    if (userId != null) {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.putExtra("shopProfile", brandProfile);
                        intent.putExtra("scubitsFragments", "scubitsFragments");
                        startActivity(intent);
                        return;
                    } else {
                        // Display dialog to request user to login.
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.showLoginDialog(context.getString(R.string.login_request_title_to_view_scubits));
                    }
                } else {
                    // Display dialog to request user to login.
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.showLoginDialog(context.getString(R.string.login_request_title_to_view_scubits));
                }
            }
        });

        // Display the progress dialog before making HTTP requests
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading Brands Search Results...");
        pDialog.show();

        Bundle args = getArguments();
        if(args.getString(getString(R.string.search_query_string)) != null) {
            getBrandSearchResultsBasedOnLoc(args.getString(getString(R.string.search_query_string)));
        } else {
            Log.d(getString(R.string.Malls_Search_Fragment), "No query string provided to invoke brand api helper");
        }

        // Add tag for accessibility
        v.setTag("BrandsSearchFragment");

        return v;
    }

    // GET Brand search results
    private void getBrandSearchResultsBasedOnLoc(String query) {
        SearchApiHelper searchApiHelper = new SearchApiHelper(context, "BrandsSearchFragment");
        searchApiHelper.getBrandSearchResultsBasedOnLoc(query, new ApiResponse.Listener<ArrayList<SearchModel>>() {

            @Override
            public void onResponse(ArrayList<SearchModel> filledSearchResultsList) {
                Log.d(context.getString(R.string.Brands_Search_Fragment), "Received brand search response from search api helper");
                searchBrandResults.clear();
                searchBrandResults.addAll(filledSearchResultsList);

                pDialog.hide();

                // Notify search adapter about data changes so that
                // it renders the list view with updated data
                searchRecycleAdapter.addItems(searchBrandResults);
                searchRecycleAdapter.notifyDataSetChanged();
            }
        });
    }
}
