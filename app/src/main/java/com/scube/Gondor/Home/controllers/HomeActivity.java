package com.scube.Gondor.Home.controllers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyLog;
import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Home.controllers.fragments.ViewPagerFragment;
import com.scube.Gondor.Home.controllers.fragments.scubitFragments.ScubitsListFragment;
import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.Login.controllers.MainActivity;
import com.scube.Gondor.Menu.views.DrawerAdapter;
import com.scube.Gondor.R;
import com.scube.Gondor.Home.controllers.fragments.scubitFragments.MyScubitsFragment;
import com.scube.Gondor.Home.controllers.fragments.scubitFragments.ScubitAddFragment;
import com.scube.Gondor.Search.controllers.SearchFragment;
import com.scube.Gondor.Search.controllers.fragments.SearchViewPagerFragment;
import com.scube.Gondor.UI.EmailValidationAlertDialog;
import com.scube.Gondor.UI.LoginRequestAlertDialog;
import com.scube.Gondor.Util.ChatLogin;
import com.scube.Gondor.Util.GlobalUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

/**
 * Created by vashoka on 5/1/15.
 */
public class HomeActivity extends AppCompatActivity implements MenuItemCompat.OnActionExpandListener {
    private MenuItem searchItem;
    private SearchView searchView;
    private String queryText;
    protected NavigationFragment topFragment;
    private SearchFragment searchFragment;
    private SearchViewPagerFragment searchViewPagerFragment;
    private Stack<NavigationFragment> navigationFragmentStack = new Stack<NavigationFragment>();
    private String drawerItem = null;
    private ActionBarDrawerToggle drawerToggle;
    Context context;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    String dialogId = null;
    ShopProfile shopProfile;
    Boolean activityActive = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_material);
        Log.d(getString(R.string.scube_home), "Home Activity Created");
        context = getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        if(getIntent().getSerializableExtra("dialogId") != null) {
            // Remember the dialog Id before checking for chat login status.
            // Upon successful login. user dialogId to load the appropriate chat screen
            dialogId = getIntent().getSerializableExtra("dialogId").toString();
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable("shopProfile") != null) {
            shopProfile = getIntent().getExtras().getParcelable("shopProfile");
            Log.d(getString(R.string.ACTIVITY_SCUBITS), shopProfile.toString());
        }

        // Save Activity
        GlobalUtils.addSharedPreference(context, getString(R.string.sp_nav), getString(R.string.sp_latest_activity), context.getClass().getSimpleName());
        sharedPreferences = getSharedPreferences(getString(R.string.sp_user), Context.MODE_PRIVATE);

        // Material Drawer
        setupSideDrawer();

        // Material Toolbar
        setupToolBar();

        handleIntent(getIntent());


    }

//    public int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }

    private void setupToolBar() {
        // Creating The Toolbar and setting it as the Toolbar for the activity

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationFragmentStack.size() > 1) {
                    popTopFragment();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    private void setupSideDrawer() {
        RecyclerView drawerRecyclerView;
        final RecyclerView.Adapter drawerAdapter;
        RecyclerView.LayoutManager layoutManager;

        // Init recycler view
        drawerRecyclerView = (RecyclerView) findViewById(R.id.drawer_list_view);
        drawerRecyclerView.setHasFixedSize(true);

        // Set layout manager to recycler view
        layoutManager = new LinearLayoutManager(this);
        drawerRecyclerView.setLayoutManager(layoutManager);

        // Set adapter to recycler view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerAdapter = new DrawerAdapter(context, R.drawable.profile_placeholder, this, drawerLayout);
        drawerRecyclerView.setAdapter(drawerAdapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }
        };

        // Drawer Toggle Object Made
        drawerLayout.setDrawerListener(drawerToggle); // Drawer Listener set to the Drawer toggle
        drawerToggle.syncState();
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu : this adds items to the tool bar
        toolbar.getMenu().clear();
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);

        // Associate searchable configuration with the SearchView

        // TODO : Refer this link http://stackoverflow.com/questions/27556623/creating-a-searchview-that-looks-like-the-material-design-guidelines
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if(searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    queryText = query;
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        } else {
            Log.d(getString(R.string.Home_Activity), "No SearchView found to setQueryTextListener");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            if (queryText != null) {
                searchView.setQuery(queryText, false);
                if (searchView.hasFocus()) {
                    searchView.clearFocus();
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean itemSelected;
        switch (item.getItemId()) {
            case R.id.action_search:
                searchView.clearFocus();
                itemSelected = true;
                break;
            default:
                itemSelected = super.onOptionsItemSelected(item);
        }

        return itemSelected;
    }

    // Search Destination
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_SEARCH.equals(action)) {
            String query = intent.getStringExtra(SearchManager.QUERY);

//            if (searchFragment == null) {
//                searchFragment = SearchFragment.newInstance(query);
//                searchFragment.navigationController = this;
//                searchFragment.setTagName("SearchFragment");
//                pushFragmentToStackWithTag(searchFragment, searchFragment.getTag());
//            }
//            if (topFragment instanceof SearchFragment) {
//                searchFragment.handleIntent(intent);
//            } else {
//                fragmentWillDisappear(topFragment);
//                searchFragment = SearchFragment.newInstance(query);
//                searchFragment.navigationController = this;
//                searchFragment.setTagName("SearchFragment");
//                pushFragmentToStackWithTag(searchFragment, searchFragment.getTag());
//            }

            if (searchViewPagerFragment == null) {
                searchViewPagerFragment = SearchViewPagerFragment.newInstance(query, this);
                searchViewPagerFragment.navigationController = this;
                searchViewPagerFragment.setTagName("SearchViewPagerFragment");
                pushFragmentToStackWithTag(searchViewPagerFragment, searchViewPagerFragment.getTag());
            }
            else if (topFragment instanceof SearchViewPagerFragment) {
                searchViewPagerFragment.handleIntent(intent);
            } else {
                fragmentWillDisappear(topFragment);
                searchFragment = SearchFragment.newInstance(query);
                searchFragment.navigationController = this;
                searchFragment.setTagName("SearchFragment");
                pushFragmentToStackWithTag(searchFragment, searchFragment.getTag());
            }

            Log.d(getString(R.string.Home_Activity), "Search String : " + query);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            popTopFragment();
        } else {
            if(GlobalUtils.containsSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session)) &&
                    GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session)).equals(getString(R.string.user_session_true))) {
                finish();
            } else {
                navigateToLogin("back");
            }
        }

        syncBackButton();
    }

    // Navigation
    public void navigateToLogin(String extrasToSend) {
        Intent main = new Intent(this, MainActivity.class);
        switch (extrasToSend) {
            case "logout":
                main.putExtra("doLogout", true);
                break;
            case "false":
                main.putExtra("doLogout", false);
                break;
            case "resendEmail":
                Log.d(getString(R.string.Home_Activity), "We need to Resend Email Validation");
                main.putExtra("doLogout", false);
                main.putExtra("resendEmail", true);
                break;
            default:
                break;
        }
        startActivity(main);
        finish();
    }

    // FRAGMENTS HANDLING

    // Navigation Controller : Home Activity
    public NavigationFragment getTopFragment() {
        return topFragment;
    }

    public void clearFragmentManagerBackStack() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
            getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    // Move to a new fragment
    public void transitionToFragment(Class<? extends Fragment> fragmentClass, String tag) {
        transitionToFragment((NavigationFragment) Fragment.instantiate(HomeActivity.this, fragmentClass.getName()), tag);
    }

    public void transitionToFragment(NavigationFragment newFragment, String tag) {
        topFragment = newFragment;
        newFragment.navigationController = this;
        newFragment.setTagName(tag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, newFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();

        if(navigationFragmentStack.size() == 0) {
            toolbar.setTitle("SCUBE");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAsRoot(ViewPagerFragment.class, "MallsFragment");
                    // Show tabs when moving back to pager view
                    if (navigationFragmentStack.size() == 1) {
                        setBackIcon(R.drawable.ic_menu_white_36dp);
                    }
                }
            });

        }

        navigationFragmentStack.push(newFragment);
        fragmentWillAppear(newFragment);
    }

    // Show This Fragment as root of the fragment stack by removing all the already existing fragments in the stack
    public void showAsRoot(Class<? extends NavigationFragment> fragmentClass, String tag) {
        showAsRoot((NavigationFragment) Fragment.instantiate(HomeActivity.this, fragmentClass.getName()), tag);
    }

    public void showAsRoot(NavigationFragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        navigationFragmentStack.clear();
        transitionToFragment(fragment, tag);
    }

    // Show Fragment and add to stack
    public void pushFragmentToStack(Class<? extends NavigationFragment> fragmentClass, Object fragmentExtras, String comingFrom) {

        Bundle bundle = new Bundle();
        if (comingFrom != null) {
            if(comingFrom.equals("shopProfile")) {
                bundle.putParcelable("shopProfile", (ShopProfile) fragmentExtras);
            }
        }
        NavigationFragment navigationFragment = (NavigationFragment) Fragment.instantiate(HomeActivity.this, fragmentClass.getName());
        navigationFragment.setArguments(bundle);
        pushFragmentToStack(navigationFragment);
    }

    public void pushFragmentToStack(NavigationFragment fragment) {
        pushFragmentToStackWithTag(fragment, fragment.getTag());
    }

    public void pushFragmentToStackWithTag(NavigationFragment fragment, String tag) {
        // Navigate using home activity nav helpers
        fragment.navigationController = this;

        // All UI updates should run on the main thread
        if(checkMainThread()) {
            if (fragment != null && !fragment.isDetached()) {

                if (navigationFragmentStack.empty()) {
                    transitionToFragment(fragment, tag);
                } else {
                    fragmentWillDisappear(topFragment);
                    topFragment = fragment;
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    fragment.navigationController = this;
                    ft.setCustomAnimations(R.anim.right_slide_in, R.anim.left_slide_out, R.anim.left_slide_out, R.anim.left_slide_out);
                    ft.add(R.id.content_frame, fragment, tag);
                    ft.addToBackStack(tag);
                    ft.commitAllowingStateLoss();

                    if (navigationFragmentStack.size() > 0) {
                        setBackIcon(R.drawable.ic_chevron_left_white_36dp);
                    }

                    navigationFragmentStack.push(fragment);
                    fragmentWillAppear(fragment);
                }

            } else {
                Log.d(getString(R.string.Home_Activity), "Could not push "+tag+" to stack");
            }
        } else {
            HomeRunnable runnable = new HomeRunnable();
            runnable.setData(fragment, tag);
            runOnUiThread(runnable);
        }
    }

    public NavigationFragment fragmentInStackFromTag(String tag) {
        return (NavigationFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    // Update Top Fragment in stack : Replace / Remove
    public void replaceFragment(NavigationFragment fragment) {
        replaceFragment(fragment, fragment.getTag());
    }

    public void removeFragment(NavigationFragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.remove(fragment);
    }

    public void replaceFragment(NavigationFragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(tag, 0);

        if (!fragmentPopped) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(tag);
            ft.commitAllowingStateLoss();
            fragmentWillAppear(fragment);
        }
    }

    public void popTopFragment() {
        if (navigationFragmentStack.size() > 1) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentWillDisappear(topFragment);
            navigationFragmentStack.pop();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            fragmentManager.popBackStack();
            ft.commitAllowingStateLoss();
            if (!navigationFragmentStack.empty()) {
                topFragment = navigationFragmentStack.peek();
                fragmentWillAppear(topFragment);
            } else {
                topFragment = null;
            }
        }

        // Show tabs when moving back to pager view
        if (navigationFragmentStack.size() == 1) {
            setBackIcon(R.drawable.ic_menu_white_36dp);
        }
    }

    // Show / Hide Fragments
    private void syncBackButton() {
        //drawerToggle.setDrawerIndicatorEnabled(navigationFragmentStack.size() <= 1);
    }

    private void fragmentWillAppear(NavigationFragment fragment) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (fragment != null) {
            fragment.viewWillAppear();
            if (fragment.getTitleName() != null) {
                toolbar.setTitle(fragment.getTitleName());
                if (searchItem != null && searchItem.isActionViewExpanded()) {
                    searchItem.collapseActionView();
                }
            }

            if (searchView != null && searchView.hasFocus()) {
                searchView.clearFocus();
            }
            syncBackButton();
        }
    }

    private void fragmentWillDisappear(NavigationFragment fragment) {
        fragment.viewWillDisappear();
    }

    // Very important for Drawer icon to be displayed
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void drawerItemClicked(String item) {
        drawerItem = item;
    }

    // 1. Scubit Add/ My Scubits / Scubits List Fragment : Check chat login status before loading the appropriate fragment to user
    // Upon successful login, chat login util will callback this function
    public void chatLoginStatusCallback(Boolean status) {
        // Make sure the activity still exists and is not destroyed
        if (!activityActive) {
            return;
        }

        // Login success : Check on what user action, did we try logging into chat
        //                        Add Scubits : load add scubits fragment to user
        //                        My Scubits  : load my scubits fragment
        if(status) {
            // if add scubit
            if(drawerItem.equals("ADD SCUBIT")) {
                Log.d(getString(R.string.Home_Activity), "Success : Chat Login callback from Add scubit click action");
                pushFragmentToStack(ScubitAddFragment.class, null, null);

            } else if(drawerItem.equals("MY SCUBITS")) {
                Log.d(getString(R.string.Home_Activity), "Success : Chat Login callback from My scubits click action");
                pushFragmentToStack(MyScubitsFragment.class, null, null);
            }
        } else {
            // if add scubit
            if(drawerItem.equals("ADD SCUBIT")) {
                Log.d(getString(R.string.Home_Activity), "Failed : Chat Login callback from Add scubit click action");
                // Nothing
            } else if(drawerItem.equals("MY SCUBITS")) {
                Log.d(getString(R.string.Home_Activity), "Failed : Chat Login callback from Add scubit click action");
                // Nothing
            }
        }
    }

    // Login Request Alert Box
    public void showLoginDialog(String message) {
        DialogFragment newFragment = LoginRequestAlertDialog.newInstance(
                message, this);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    // Email Validation Alert Box
    public void showEmailValidationDialog(String message) {
        DialogFragment newFragment = EmailValidationAlertDialog.newInstance(
                message, this);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void userAcceptedLoginRequest() {
        navigateToLogin("false");
    }

    public void userCancelledLoginRequest() {
        // Do nothing
    }

    public void userAcceptedResendEmailValidation() {
        navigateToLogin("resendEmail");
    }

    public void userCancelledResendEmailValidation() {
        // Do nothing for now
        if (navigationFragmentStack.size() > 1) {
            popTopFragment();
        }
    }

    // Check if main thread
    public Boolean checkMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d(getString(R.string.Home_Activity), "You are on UI / main thread");
            return true;
        } else {
            Log.d(getString(R.string.Home_Activity), "You are NOT on UI / main thread");
            return false;
        }
    }

    public class HomeRunnable implements Runnable {
        private NavigationFragment fragment;
        private String tag;
        public void setData(NavigationFragment _fragment, String _tag) {
            this.fragment = _fragment;
            this.tag = _tag;
        }

        public void run() {
            if (fragment != null && !fragment.isDetached()) {
                //sideMenuDrawer.unselectCurrentItem();
                //sideMenuDrawer.closeDrawer(sideMenuListView);

                if (navigationFragmentStack.empty()) {
                    transitionToFragment(fragment, tag);
                } else {
                    fragmentWillDisappear(topFragment);
                    topFragment = fragment;
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.right_slide_in, R.anim.left_slide_out, R.anim.left_slide_out, R.anim.left_slide_out);
                    ft.add(R.id.content_frame, fragment, tag);
                    ft.addToBackStack(tag);
                    ft.commitAllowingStateLoss();

                    if (navigationFragmentStack.size() > 0) {
                        setBackIcon(R.drawable.ic_chevron_left_white_36dp);
                    }

                    navigationFragmentStack.push(fragment);
                    fragmentWillAppear(fragment);
                }

            } else {
                Log.d(getString(R.string.Home_Activity), "Could not push "+tag+" to stack");
            }
        }
    }

    public void setBackIcon(Integer icon) {
        toolbar.setNavigationIcon(icon);
    }

    @Override
    public void onResume() {
        Log.d(getString(R.string.Home_Activity), "Home Activity Entered");
        super.onResume();
    }
    @Override
    public void onPause() {
        Log.d(getString(R.string.Home_Activity), "Home Activity Paused");
        super.onPause();
    }
    @Override
    public void onStop() {
        Log.d(getString(R.string.Home_Activity), "Home Activity Stopped");
        super.onStop();
    }
    @Override
    public void onDestroy() {
        Log.d(getString(R.string.Home_Activity), "Home Activity Destroyed");
        activityActive = false;
        super.onDestroy();
    }
}
