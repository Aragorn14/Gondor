package com.scube.Gondor.Search.controllers.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Menu.views.SlidingTabLayout;
import com.scube.Gondor.Menu.views.SlidingTabStrip;
import com.scube.Gondor.R;

/**
 * Created by vashoka on 8/29/15.
 */
public class SearchViewPagerFragment extends NavigationFragment {

    private ProgressDialog pDialog;
    Context context;
    public static final String ARG_SECTION_NUMBER = "section_number";
    private SlidingTabStrip slidingTabStrip;
    private ViewPager viewPager;
    SearchViewPagerAdapter pagerAdapter;
    SlidingTabLayout tabLayout;

    public static SearchViewPagerFragment newInstance(String searchQuery, Context context)
    {
        SearchViewPagerFragment fragment = new SearchViewPagerFragment();

        Bundle args = new Bundle();
        args.putString(context.getString(R.string.search_query_string), searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_view_pager, container, false);
        context = getActivity().getApplicationContext();

        // Get the search query received from the invoking activity
        String searchQuery = "";
        if(getArguments() != null) {
            searchQuery = getArguments().getString(context.getString(R.string.search_query_string));
        }

        viewPager = (ViewPager) v.findViewById(R.id.pager);
        Search(searchQuery, getActivity().getApplicationContext());

        // Assiging the Sliding Tab Layout View
        tabLayout = (SlidingTabLayout) v.findViewById(R.id.tabLayout);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Set custom View for tabs
        tabLayout.setCustomTabView(R.layout.pager_view_tab, R.id.tabText);
        tabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting the ViewPager For the SlidingTabsLayout
        tabLayout.setViewPager(viewPager);

        this.setTitleName("SEARCH");

        // Add tag for accessibility
        v.setTag("SearchViewPagerFragment");

        return v;
    }

    public void handleIntent(Intent intent)
    {
        String currentQuery = intent.getStringExtra(SearchManager.QUERY);
        Search(currentQuery, context);
    }

    private void Search(String searchQuery, Context context) {
        pagerAdapter = new SearchViewPagerAdapter(getFragmentManager(), context, searchQuery);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
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
}

class SearchViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int numbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    int icons[] = {R.drawable.mall_icon_green, R.drawable.shop_icon_green, R.drawable.offer_icon};
    String[] tabTitles;
    Context context;
    String searchQuery;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public SearchViewPagerAdapter(FragmentManager fm, Context context, String searchQuery) {
        super(fm);

        this.context = context;
        tabTitles = context.getResources().getStringArray(R.array.pagerTabs);
        this.searchQuery = searchQuery;
    }

    @Override
    public Fragment getItem(int pageIndex) {
        Bundle args = new Bundle();

        // Load search string into bundle to pass it over to malls/shops/brands search results pager fragment
        args.putString(context.getString(R.string.search_query_string), searchQuery);

        switch (pageIndex) {
            // Malls Search Results
            case 0:
                MallsSearchFragment mallsSearchFragment = new MallsSearchFragment();
                args.putInt(MallsSearchFragment.TAB_INDEX, pageIndex + 1);
                mallsSearchFragment.setArguments(args);
                return mallsSearchFragment;

            // Shops Search Results
            case 1:
                ShopsSearchFragment shopsSearchFragment = new ShopsSearchFragment();
                args.putInt(ShopsSearchFragment.TAB_INDEX, pageIndex + 1);
                shopsSearchFragment.setArguments(args);
                return shopsSearchFragment;

            // Brands Search Results
            case 2:
                BrandsSearchFragment brandsSearchFragment = new BrandsSearchFragment();
                args.putInt(BrandsSearchFragment.TAB_INDEX, pageIndex + 1);
                brandsSearchFragment.setArguments(args);
                return brandsSearchFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(tabTitles[position] != null) {
            return tabTitles[position];
        } else {
            return "New Section";
        }
    }
}