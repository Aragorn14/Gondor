package com.scube.Gondor.Search.controllers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.scube.Gondor.Core.controllers.AppController;
import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Helpers.api.SearchApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.controllers.fragments.ShopProfilesFragment;
import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.Menu.model.FilterMenuListener;
import com.scube.Gondor.Menu.model.FilterMenuModel;
import com.scube.Gondor.Menu.views.FilterMenuView;
import com.scube.Gondor.R;
import com.scube.Gondor.Search.models.SearchAdapter;
import com.scube.Gondor.Search.models.SearchModel;
import com.scube.Gondor.UI.ProgressCell;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;
import com.scube.hoverboard.src.main.java.com.hoverboard.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vashoka on 05/30/15.
 */
public class SearchFragment extends NavigationFragment
        implements FilterMenuListener, AbsListView.OnScrollListener
{
    public static final String SEARCH_TAG = "search_tag";
    public static final String SEARCH_QUERY_ARG = "search_query";
    private static final int COUNT = 50;
    private final List<FilterMenuModel> searchFilters = defaultSearchFilters();
    private ArrayList<SearchModel> searchResults = new ArrayList<SearchModel>();
    private String currentQuery;
    private ProgressCell progressCell;
    private int pageIndex = 0;
    private int totalSearchResultsCount = 0;
    private FilterMenuModel currentFilter;
    private SearchAdapter searchAdapter;
    private TextView totalTextView;
    private FilterMenuView filterMenuView;
    private ArrayList searchModelsWaitingForWikiModelsToLoad = new ArrayList();
    private boolean isMore = true;
    private boolean isSearching = false;
    private Context context;

    private List<FilterMenuModel> defaultSearchFilters()
    {
        List<FilterMenuModel> searchFilters = new ArrayList<FilterMenuModel>();
        Integer filterIndex = 0;
        searchFilters.add(new FilterMenuModel("ALL", null, ++filterIndex));
        searchFilters.add(new FilterMenuModel("MALLS", null, ++filterIndex));
        searchFilters.add(new FilterMenuModel("SHOPS", null, ++filterIndex));
        searchFilters.add(new FilterMenuModel("BRANDS", null, ++filterIndex));

        return searchFilters;
    }

    public static SearchFragment newInstance(String searchQuery)
    {
        SearchFragment fragment = new SearchFragment();

        Bundle arguments = new Bundle();
        arguments.putString(SEARCH_QUERY_ARG, searchQuery);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        tagName = SEARCH_TAG;
        currentFilter = searchFilters.get(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup searchView = (ViewGroup) inflater.inflate(R.layout.search, null);

        progressCell = new ProgressCell(searchView.getContext());
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 200);
        progressCell.setLayoutParams(layoutParams);

        context = getActivity().getApplicationContext();

        searchAdapter = new SearchAdapter(searchView.getContext());
        ListView listView = (ListView) searchView.findViewById(R.id.searchListView);
        listView.addFooterView(progressCell);
        listView.setSmoothScrollbarEnabled(true);
        listView.setAdapter(searchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                listViewItemClickedAtPosition(position);
            }
        });

        filterMenuView = (FilterMenuView) searchView.findViewById(R.id.searchFilterView);
        filterMenuView.setFilterMenuListener(this);
        filterMenuView.addMenuItems(searchFilters);
        final ViewTreeObserver vto = filterMenuView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                filterMenuView.highlightCellAtPosition(0);
                // only need to highlight once, so remove listener
                ViewHelper.removeOnGlobalLayoutListener(filterMenuView, this);
            }
        });

        listView.setOnScrollListener(this);

        if (getArguments() != null) {
            currentQuery = getArguments().getString(SEARCH_QUERY_ARG);
            search();
        }

        // Add tag for accessibility
        searchView.setTag("SearchFragment");

        return searchView;
    }

    private void listViewItemClickedAtPosition(int position)
    {
        SearchModel searchModel = (SearchModel) searchAdapter.getItem(position);
        String searchModelType = searchModel.getSearchModelType();

        // Load all the shops profiles present in this mall
        if(searchModelType.equals("mall")) {
            Log.d(getString(R.string.Search_Fragment), "User Clicked a Mall item in search results");
            Mall mall = searchModel.getMall();

            // Load mall into bundle to pass it over to Shop profiles fragment
            Bundle bundle = new Bundle();
            bundle.putString("profile", "mall");
            bundle.putParcelable("profileObject", mall);

            ShopProfilesFragment shopProfilesFragment = new ShopProfilesFragment();
            shopProfilesFragment.setArguments(bundle);
            navigationController.pushFragmentToStack(shopProfilesFragment);
        } else if(searchModelType.equals("shopProfile")) {
            Log.d(getString(R.string.Search_Fragment), "User Clicked a Shop profile item in search results");
            // Load all scubits based on this shop profile
            ShopProfile shopProfile = searchModel.getShopProfile();

            if (GlobalUtils.containsSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_id))) {
                String userId = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_id));
                if (userId != null) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("shopProfile", shopProfile);
                    intent.putExtra("scubitsFragments", "scubitsFragments");
                    startActivity(intent);
                    return;
                } else {
                    // Display dialog to request user to login.
                    navigationController.showLoginDialog(context.getString(R.string.login_request_title_to_view_scubits));
                }
            } else {
                // Display dialog to request user to login.
                navigationController.showLoginDialog(context.getString(R.string.login_request_title_to_view_scubits));
            }
        } else if(searchModelType.equals("brandProfile")) {
            Log.d(getString(R.string.Search_Fragment), "User Clicked a Brand Profile item in search results");
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
                    navigationController.showLoginDialog(context.getString(R.string.login_request_title_to_view_scubits));
                }
            } else {
                // Display dialog to request user to login.
                navigationController.showLoginDialog(context.getString(R.string.login_request_title_to_view_scubits));
            }
        }
    }

    public void handleIntent(Intent intent)
    {
        currentQuery = intent.getStringExtra(SearchManager.QUERY);
        if (searchAdapter != null) {
            newSearch();
        }
    }

    @Override
    public void onItemSelected(FilterMenuModel filter)
    {
        currentFilter = filter;
        newSearch();
    }

    private void newSearch()
    {
        isMore = true;
        clearSearch();
        search();
    }

    private void search()
    {
        if (isMore) {
            switch (currentFilter.getIndex()) {
                case 1:
                    getSearchAllResultsBasedOnLoc(currentQuery);
                    break;
                case 2:
                    getMallSearchResultsBasedOnLoc(currentQuery);
                    break;
                case 3:
                    getShopSearchResultsBasedOnLoc(currentQuery);
                    break;
                case 4:
                    getBrandSearchResultsBasedOnLoc(currentQuery);
                    break;
            }

            progressCell.show();
            isSearching = true;
        }
    }

    private String totalTextString(int totalResults, String query)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(totalResults);
        sb.append(" Results for <b>");
        sb.append(query);
        sb.append("</b>");

        return sb.toString();
    }

    private void clearSearch()
    {
        pageIndex = 0;
        totalSearchResultsCount = 0;
        searchModelsWaitingForWikiModelsToLoad.clear();
        AppController.getInstance().cancelPendingRequests(SEARCH_TAG);
        if (searchAdapter != null) {
            searchAdapter.clear();
        }
        if (totalTextView != null) {
            totalTextView.setText("");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 2;
        if (loadMore && searchAdapter != null) {
            if (totalItemCount < totalSearchResultsCount && this.isMore && !isSearching) {
                search();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    // GET All search results
    private void getSearchAllResultsBasedOnLoc(String query) {
        SearchApiHelper searchApiHelper = new SearchApiHelper(context, "SearchFragment");
        searchApiHelper.getAllSearchResultsBasedOnLoc(query, new ApiResponse.Listener<ArrayList<SearchModel>>() {

            @Override
            public void onResponse(ArrayList<SearchModel> filledSearchResultsList) {
                Log.d(context.getString(R.string.Search_Fragment), "Received search-all response from search api helper");
                searchResults.clear();
                searchResults.addAll(filledSearchResultsList);

                // Notify search adapter about data changes so that
                // it renders the list view with updated data
                searchAdapter.addItems(searchResults);
                progressCell.hide();
                searchAdapter.notifyDataSetChanged();
            }
        });
    }

    // GET Mall search results
    private void getMallSearchResultsBasedOnLoc(String query) {
        SearchApiHelper searchApiHelper = new SearchApiHelper(context, "SearchFragment");
        searchApiHelper.getMallSearchResultsBasedOnLoc(query, new ApiResponse.Listener<ArrayList<SearchModel>>() {

            @Override
            public void onResponse(ArrayList<SearchModel> filledSearchResultsList) {
                Log.d(context.getString(R.string.Search_Fragment), "Received mall search response from search api helper");
                searchResults.clear();
                searchResults.addAll(filledSearchResultsList);

                // Notify search adapter about data changes so that
                // it renders the list view with updated data
                searchAdapter.addItems(searchResults);
                progressCell.hide();
                searchAdapter.notifyDataSetChanged();
            }
        });
    }

    // GET Shop search results
    private void getShopSearchResultsBasedOnLoc(String query) {
        SearchApiHelper searchApiHelper = new SearchApiHelper(context, "SearchFragment");
        searchApiHelper.getShopSearchResultsBasedOnLoc(query, new ApiResponse.Listener<ArrayList<SearchModel>>() {

            @Override
            public void onResponse(ArrayList<SearchModel> filledSearchResultsList) {
                Log.d(context.getString(R.string.Search_Fragment), "Received shop search response from search api helper");
                searchResults.clear();
                searchResults.addAll(filledSearchResultsList);

                // Notify search adapter about data changes so that
                // it renders the list view with updated data
                searchAdapter.addItems(searchResults);
                progressCell.hide();
                searchAdapter.notifyDataSetChanged();
            }
        });
    }

    // GET Brand search results
    private void getBrandSearchResultsBasedOnLoc(String query) {
        SearchApiHelper searchApiHelper = new SearchApiHelper(context, "SearchFragment");
        searchApiHelper.getBrandSearchResultsBasedOnLoc(query, new ApiResponse.Listener<ArrayList<SearchModel>>() {

            @Override
            public void onResponse(ArrayList<SearchModel> filledSearchResultsList) {
                Log.d(context.getString(R.string.Search_Fragment), "Received brand search response from search api helper");
                searchResults.clear();
                searchResults.addAll(filledSearchResultsList);

                // Notify search adapter about data changes so that
                // it renders the list view with updated data
                searchAdapter.addItems(searchResults);
                progressCell.hide();
                searchAdapter.notifyDataSetChanged();
            }
        });
    }
}
