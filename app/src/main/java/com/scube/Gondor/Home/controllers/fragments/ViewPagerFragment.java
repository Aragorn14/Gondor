package com.scube.Gondor.Home.controllers.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
 * Created by vashoka on 5/1/15.
 */
public class ViewPagerFragment extends NavigationFragment {

    private ProgressDialog pDialog;
    Context context;
    public static final String ARG_SECTION_NUMBER = "section_number";
    private SlidingTabStrip slidingTabStrip;
    private ViewPager viewPager;
    ViewPagerAdapter pagerAdapter;
    SlidingTabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);
        context = getActivity().getApplicationContext();

        pagerAdapter = new ViewPagerAdapter(getFragmentManager(), context);

        viewPager = (ViewPager) v.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

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

        this.setTitleName("SCUBE");

        // Add tag for accessibility
        v.setTag("ViewPagerFragment");

        return v;
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

class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int numbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    int icons[] = {R.drawable.mall_icon_green, R.drawable.shop_icon_green, R.drawable.offer_icon};
    String[] tabTitles;
    Context context;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.context = context;
        tabTitles = context.getResources().getStringArray(R.array.pagerTabs);
    }

    @Override
    public Fragment getItem(int pageIndex) {
        Bundle args = new Bundle();

        switch (pageIndex) {
            // Malls Section
            case 0:
                MallsFragment mallFragment = new MallsFragment();
                args.putInt(MallsFragment.TAB_INDEX, pageIndex + 1);
                mallFragment.setArguments(args);
                return mallFragment;

            // Shops Section
            case 1:
                ShopsFragment shopFragment = new ShopsFragment();
                args.putInt(MallsFragment.TAB_INDEX, pageIndex + 1);
                shopFragment.setArguments(args);
                return shopFragment;

            // Brands Section
            case 2:
                BrandsFragment brandFragment = new BrandsFragment();
                args.putInt(MallsFragment.TAB_INDEX, pageIndex + 1);
                brandFragment.setArguments(args);
                return brandFragment;
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
