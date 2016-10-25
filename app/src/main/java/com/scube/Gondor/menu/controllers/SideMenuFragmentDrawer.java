package com.scube.Gondor.Menu.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.controllers.fragments.BrandsFragment;
import com.scube.Gondor.Home.controllers.fragments.ViewPagerFragment;
import com.scube.Gondor.Login.controllers.fragments.LoginFragment;
import com.scube.Gondor.Menu.model.SideMenuAdapter;
import com.scube.Gondor.Menu.model.SideMenuHeaderModel;
import com.scube.Gondor.Menu.model.SideMenuModel;
import com.scube.Gondor.R;
import com.scube.Gondor.Home.controllers.fragments.scubitFragments.MyScubitsFragment;
import com.scube.Gondor.Home.controllers.fragments.scubitFragments.ScubitAddFragment;
import com.scube.Gondor.Util.Auth;
import com.scube.Gondor.Util.ChatLogin;
import com.scube.Gondor.Util.GlobalUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SideMenuFragmentDrawer extends DrawerLayout implements SideMenuAdapter.SideMenuListener
{
    private int profileHeaderId;
    private int scubeHeaderId = 1;
    private int chatsHeaderId = 2;
    private int scubitsHeaderId = 3;

    private Context mContext;
    private String defaultFeedString;
    public ArrayList<SideMenuModel> menuModels;
    public HomeActivity navigationController;
    private ActionBarDrawerToggle drawerToggle;
    private SideMenuAdapter drawerAdapter;
    public StickyListHeadersListView mainMenuListView;
    private SideMenuModel currentNavItem;
    private int indexOfDefaultFeed = -1;
    private SharedPreferences sharedPreferences;
    private JSONObject loginDetails = null;

    public SideMenuFragmentDrawer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public SideMenuFragmentDrawer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public SideMenuFragmentDrawer(Context context)
    {
        super(context);
        init(context);
    }

    private void init(Context context)
    {
        mContext = context;
    }

    public void setupDrawerConfiguration(StickyListHeadersListView drawerListView, Toolbar toolbar)
    {
        drawerAdapter = new SideMenuAdapter(getContext(), this);

        mainMenuListView = drawerListView;
        mainMenuListView.setVerticalScrollBarEnabled(false);
        mainMenuListView.setAdapter(drawerAdapter);
        mainMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectDrawerItem(position);
            }
        });

        //navigationController.setBackIcon(R.drawable.ic_dehaze_white_24dp);

        drawerToggle = setupDrawerToggle(toolbar);
        setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        sharedPreferences = navigationController.getApplicationContext().getSharedPreferences(navigationController.getString(R.string.sp_user), Context.MODE_PRIVATE);
        createMenu();

        // Select an element on load
        selectOnLoadFeed();
    }

    public void selectOnLoadFeed()
    {
        // Add logic to decide which fragment to load on start of the home activity
        selectDrawerItem(0);
    }

    private void createMenu()
    {
        menuModels = new ArrayList<SideMenuModel>();

        createMainItems(menuModels);

        drawerAdapter.addItems(menuModels, 0);
        drawerAdapter.notifyDataSetChanged();
    }

    private void addMenuItem(SideMenuModel model)
    {
        menuModels.add(model);
    }

    private void createMainItems(ArrayList<SideMenuModel> menuModels)
    {
        SideMenuModel home = new SideMenuModel("HOME",
                ViewPagerFragment.class,
                R.drawable.sidebar_home);
        addMenuItem(home);

        loginDetails = Auth.getLoginDetails(mContext);
        try {
            if (loginDetails.getString("status") == mContext.getString(R.string.user_session_true)) {
                String emailId = loginDetails.getString("emailId");
                if(emailId != null) {
                    SideMenuModel menuProfile = new SideMenuModel(emailId,
                            BrandsFragment.class,
                            R.drawable.profile);
                    addMenuItem(menuProfile);
                }

                SideMenuModel logout = new SideMenuModel("LOGOUT",
                        LoginFragment.class,
                        R.drawable.logout);
                addMenuItem(logout);
            } else if(loginDetails.getString("status") == mContext.getString(R.string.user_session_false)) {
                SideMenuModel login = new SideMenuModel("LOGIN",
                        LoginFragment.class,
                        R.drawable.login);
                addMenuItem(login);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        // Scubits
        SideMenuHeaderModel scubits = new SideMenuHeaderModel(1, "SCUBITS");
        // <div>Icons made by <a href="http://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a>is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a></div>
        SideMenuModel addScubit = new SideMenuModel("ADD SCUBIT",
                ScubitAddFragment.class,
                R.drawable.my_scubit, scubits);
        addMenuItem(addScubit);

        try {
            if (loginDetails.getString("status") == mContext.getString(R.string.user_session_true)) {
                SideMenuModel myScubits = new SideMenuModel("MY SCUBITS",
                        MyScubitsFragment.class,
                        R.drawable.my_scubit, scubits);
                addMenuItem(myScubits);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public void addNavItem(String navTitle,
                           Class<? extends Fragment> fragmentClass,
                           int imageViewId,
                           SideMenuHeaderModel headerModel)
    {
        SideMenuModel model = new SideMenuModel(navTitle, fragmentClass, imageViewId);
        if (headerModel != null) {
            model.headerModel = headerModel;
        }
        addMenuItem(model);
    }

    public void selectDrawerItem(int position)
    {
        SideMenuModel navItem = drawerAdapter.getItem(position);
        NavigationFragment topFragment = navigationController.getTopFragment();
        if (topFragment == null ||
                !((Object) topFragment).getClass().equals(navItem.getFragmentClass()) ||
                topFragment.getTitleName() == null ||
                navItem.title == null) {

            unselectCurrentItem();

            if(navItem.title.equals("LOGOUT")) {
                GlobalUtils.removeSharedPreference(mContext, mContext.getString(R.string.sp_user), mContext.getString(R.string.user_session));
                GlobalUtils.addSharedPreference(mContext, mContext.getString(R.string.sp_user), mContext.getString(R.string.user_logout), mContext.getString(R.string.user_logout_true));
                Log.d(navigationController.getString(R.string.Side_Menu_Drawer_Fragment), "User session removed; proceed to logout");
                // Logout the user and Navigate to Main Activity
                navigationController.navigateToLogin("logout");
            } else if(navItem.title.equals("HOME")) {
                navigationController.showAsRoot(navItem.getFragmentClass(), "ViewPagerFragment");
                mainMenuListView.setItemChecked(position, true);
                currentNavItem = navItem;
                currentNavItem.isSelected = true;
                drawerAdapter.notifyDataSetChanged();
            } else if(navItem.title.equals("ADD SCUBIT")) {
                mainMenuListView.setItemChecked(position, true);
                currentNavItem = navItem;
                currentNavItem.isSelected = true;
                drawerAdapter.notifyDataSetChanged();
                navigationController.drawerItemClicked("ADD SCUBIT");
                loginScubeAndChat();
            } else if(navItem.title.equals("MY SCUBITS")) {
                mainMenuListView.setItemChecked(position, true);
                currentNavItem = navItem;
                currentNavItem.isSelected = true;
                drawerAdapter.notifyDataSetChanged();
                navigationController.drawerItemClicked("MY SCUBITS");
                loginScubeAndChat();
            } else if(navItem.title.equals("LOGIN")) {
                navigationController.navigateToLogin("false");
            }
        }
        closeDrawer(mainMenuListView);
    }

    public void unselectCurrentItem()
    {
        if (currentNavItem != null) {
            currentNavItem.isSelected = false;
        }
    }

    public ActionBarDrawerToggle getDrawerToggle()
    {
        return drawerToggle;
    }

    private ActionBar getActionBar()
    {
        return navigationController.getSupportActionBar();
    }

    private void setTitle(CharSequence title)
    {
        getActionBar().setTitle(title);
    }

    private ActionBarDrawerToggle setupDrawerToggle(Toolbar toolbar)
    {
        return new ActionBarDrawerToggle(navigationController, /* host Activity */
                this, /* DrawerLayout object */
                toolbar, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        )
        {
            public void onDrawerClosed(View view)
            {
                navigationController.invalidateOptionsMenu(); // call onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView)
            {
                navigationController.invalidateOptionsMenu(); // call onPrepareOptionsMenu()
            }
        };
    }

    @Override
    public void headerDeleteButtonClicked(SideMenuHeaderModel model)
    {
        ArrayList<SideMenuModel> leftOverModels = new ArrayList<SideMenuModel>();
        for (SideMenuModel sideMenuModel : drawerAdapter.getDataSource()) {
            if (sideMenuModel.headerModel != null && sideMenuModel.headerModel.equals(model) && sideMenuModel.isChecked) {
                //deleteModel(sideMenuModel);
            } else {
                leftOverModels.add(sideMenuModel);
            }
        }
        model.numChildrenChecked = 0;
        drawerAdapter.setDataSource(leftOverModels);
    }

    private void checkIfDefaultFeed(SideMenuModel model)
    {
        if (defaultFeedString != null && model.title.equals(defaultFeedString)) {
            indexOfDefaultFeed = menuModels.size() - 1;
        }
    }

    public boolean isDrawerOpen()
    {
        return isDrawerOpen(mainMenuListView);
    }

    public void refresh()
    {
        drawerAdapter.clear();
        createMenu();
    }

    public interface SideMenuBlock
    {
        public void onSideMenuItemSelected();
    }

    /*
        If scube logged in : Try logging into chat
        If neither logged in : display alert box to request user to login
     */
    private void loginScubeAndChat() {
        try {
            if (loginDetails.getString("status") == mContext.getString(R.string.user_session_true)) {
                Log.d(mContext.getString(R.string.Side_Menu_Drawer_Fragment), "Scube Already Logged in. Init Chat Login");
                // Scube chat logged in, just login to chat
                ChatLogin.loginToChat(navigationController.getApplicationContext(), null, navigationController);
            } else {
                Log.d(mContext.getString(R.string.Side_Menu_Drawer_Fragment), "Neither Scube nor chat logged in. request user to login using alert box");
                // Neither Scube nor chat logged in. Request user to login using alert dialog
                navigationController.showLoginDialog(mContext.getString(R.string.login_request_title_to_add_scubit));
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
