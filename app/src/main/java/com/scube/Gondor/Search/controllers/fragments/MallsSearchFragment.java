package com.scube.Gondor.Search.controllers.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.scube.Gondor.Home.controllers.fragments.ShopProfilesFragment;
import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.R;
import com.scube.Gondor.Search.models.SearchModel;
import com.scube.Gondor.Search.models.SearchRecycleAdapter;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import java.util.ArrayList;

/**
 * Created by vashoka on 8/29/15.
 */
public class MallsSearchFragment extends Fragment {

    private ProgressDialog pDialog;
    private ArrayList<SearchModel> searchMallResults = new ArrayList<SearchModel>();
    private SearchRecycleAdapter searchRecycleAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView searchMallsList;
    Context context;

    public static final String TAB_INDEX = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_malls_search, container, false);

        context = getActivity().getApplicationContext();

        searchMallsList = (RecyclerView) v.findViewById(R.id.mallsList);
        // Set Layout manager
        searchMallsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        // What format of items to Store ?
        searchRecycleAdapter = new SearchRecycleAdapter(context, searchMallResults);
        searchMallsList.setAdapter(searchRecycleAdapter);

        searchRecycleAdapter.SetOnItemClickListener(new SearchRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("Clicked", "Clicked" + searchRecycleAdapter.getItem(position));
                // Get the Mall object from the Mall Adapter
                SearchModel searchModel = searchRecycleAdapter.getItem(position);

                Log.d(getString(R.string.Search_Fragment), "User Clicked a Mall item in search results");
                Mall mall = searchModel.getMall();

                // Load mall into bundle to pass it over to Shop profiles fragment
                Bundle bundle = new Bundle();
                bundle.putString("profile", "mall");
                bundle.putParcelable("profileObject", mall);

                ShopProfilesFragment shopProfilesFragment = new ShopProfilesFragment();
                shopProfilesFragment.setArguments(bundle);
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.pushFragmentToStack(shopProfilesFragment);
            }
        });

        // Display the progress dialog before making HTTP requests
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading Malls Search Results...");
        pDialog.show();

        Bundle args = getArguments();
        if(args.getString(getString(R.string.search_query_string)) != null) {
            getMallSearchResultsBasedOnLoc(args.getString(getString(R.string.search_query_string)));
        } else {
            Log.d(getString(R.string.Malls_Search_Fragment), "No query string provided to invoke mall api helper");
        }

        // Add tag for accessibility
        v.setTag("MallsSearchFragment");

        return v;
    }

    // GET Mall search results
    private void getMallSearchResultsBasedOnLoc(String query) {
        SearchApiHelper searchApiHelper = new SearchApiHelper(context, "MallsSearchFragment");
        searchApiHelper.getMallSearchResultsBasedOnLoc(query, new ApiResponse.Listener<ArrayList<SearchModel>>() {

            @Override
            public void onResponse(ArrayList<SearchModel> filledSearchResultsList) {
                Log.d(context.getString(R.string.Malls_Search_Fragment), "Received mall search response from search api helper");
                searchMallResults.clear();
                searchMallResults.addAll(filledSearchResultsList);

                pDialog.hide();

                // Notify search adapter about data changes so that
                // it renders the list view with updated data
                Log.d(getString(R.string.Malls_Search_Fragment), "Adding "+searchMallResults.size()+" mall search results to adapter");
                //searchRecycleAdapter.addItems(searchMallResults);
                searchRecycleAdapter.notifyDataSetChanged();
            }
        });
    }
}
