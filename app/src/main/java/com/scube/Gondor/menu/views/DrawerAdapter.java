package com.scube.Gondor.Menu.views;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scube.Gondor.Helpers.api.UserApiHelper;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.Home.controllers.fragments.ViewPagerFragment;
import com.scube.Gondor.R;
import com.scube.Gondor.UI.CircleTransform;
import com.scube.Gondor.Util.Auth;
import com.scube.Gondor.Util.ChatLogin;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vashoka on 8/22/15.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String titles[];
    private Integer icons[];
    private String userName;
    private int profilePhoto;
    private String userEmail;
    private Context context;
    private JSONObject loginDetails = null;
    private HomeActivity homeActivity;
    private DrawerLayout drawerLayout;
    private String userFirstName = "";
    private String userImageUrl = "";
    private String loggedIn = "false";

    public DrawerAdapter(Context context, int profilePhoto, HomeActivity homeActivity, DrawerLayout drawerLayout) {
        this.profilePhoto = profilePhoto;
        this.context = context;
        this.homeActivity = homeActivity;
        this.drawerLayout = drawerLayout;

        loginDetails = Auth.getLoginDetails(context);
        try {
            if (loginDetails.getString("status").equals(context.getString(R.string.user_session_true))) {
                this.loggedIn = "true";
                String emailId = loginDetails.getString("emailId");
                String userFirstName = loginDetails.getString("userFirstName");
                String userImageUrl = loginDetails.getString("userImageUrl");

                this.titles = context.getResources().getStringArray(R.array.drawerItems);
                this.icons = new Integer[]{R.drawable.home, R.drawable.my_scubit, R.drawable.add_scubit, R.drawable.settings, R.drawable.logout};
                if (emailId != null) { this.userEmail = emailId; }
                if (userFirstName != null) { this.userFirstName = userFirstName; }
                if (userImageUrl != null) { this.userImageUrl = userImageUrl; }

                // Add logout
            } else if(loginDetails.getString("status") == context.getString(R.string.user_session_false)) {
                // Add login
                this.titles = context.getResources().getStringArray(R.array.drawerItemsNotLoggedIn);
                this.icons = new Integer[]{R.drawable.home, R.drawable.add_scubit, R.drawable.settings, R.drawable.login};

            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        // Init Frame content with Pager Fragment
        homeActivity.showAsRoot(ViewPagerFragment.class, "ViewPagerFragment");
    }

    // viewType is Type_ITEM : inflate drawer_list_item.xml
    // viewType is Type_HEADER : inflate drawer_header.xml
    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType, parent.getContext());
            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType, parent.getContext());
            return vhHeader;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
        if(holder.holderId ==1) {
            holder.itemTextView.setText(titles[position - 1]);
            holder.itemIconView.setImageResource(icons[position -1]);
        }
        else{
            loginDetails = Auth.getLoginDetails(context);
            try {
                if(!userImageUrl.equals("")){
                    Picasso.with(context)
                            .load(userImageUrl)
                            .placeholder(R.drawable.profile_placeholder)
                            .transform(new CircleTransform())
                            .into(holder.userProfilePhoto);
                } else {
                    holder.userProfilePhoto.setVisibility(View.INVISIBLE);
                }

                if (loginDetails.getString("status").equals(context.getString(R.string.user_session_true))) {
                    String emailId = loginDetails.getString("emailId");
                    if(emailId != null) {
                        holder.userEmailTextView.setText(userEmail);
                    }

                    holder.userNameTextView.setText(userFirstName);
                } else if(loginDetails.getString("status").equals(context.getString(R.string.user_session_false))) {
                    Log.d(context.getString(R.string.Drawer_Adapter), "No logged in user found. BindViewHolder terminated");
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return titles.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int holderId;
        TextView itemTextView;
        ImageView itemIconView;
        ImageView userProfilePhoto;
        TextView userNameTextView;
        TextView userEmailTextView;
        View view;
        Context context;

        public ViewHolder(View itemView, int ViewType, Context context) {
            super(itemView);

            view = itemView;
            this.context = context;

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            if(ViewType == TYPE_ITEM) {
                itemTextView = (TextView) itemView.findViewById(R.id.itemName);
                itemIconView = (ImageView) itemView.findViewById(R.id.itemIcon);
                holderId = 1;
            } else {
                userNameTextView = (TextView) itemView.findViewById(R.id.name);
                userEmailTextView = (TextView) itemView.findViewById(R.id.email);
                userProfilePhoto = (ImageView) itemView.findViewById(R.id.profileImage);
                holderId = 0;
            }
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(context, "The Item Clicked is: " + getLayoutPosition(), Toast.LENGTH_SHORT).show();

            if(loggedIn.equals("true")) {
                switch (getLayoutPosition()) {
                    // HOME
                    case 1:
                        homeActivity.showAsRoot(ViewPagerFragment.class, "ViewPagerFragment");
                        break;
                    // MY SCUBITS
                    case 2:
                        homeActivity.drawerItemClicked("MY SCUBITS");
                        loginScubeAndChat();
                        break;
                    // ADD SCUBIT
                    case 3:
                        homeActivity.drawerItemClicked("ADD SCUBIT");
                        loginScubeAndChat();
                        break;
                    // SETTINGS
                    case 4:
                        homeActivity.drawerItemClicked("SETTINGS");
                        // Settings view fragment push
                        break;
                    // LOGOUT
                    case 5:
                        GlobalUtils.removeSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_session));
                        GlobalUtils.addSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_logout), context.getString(R.string.user_logout_true));
                        Log.d(homeActivity.getString(R.string.Side_Menu_Drawer_Fragment), "User session removed; proceed to logout");
                        // Logout the user and Navigate to Main Activity
                        homeActivity.navigateToLogin("logout");
                        break;
                }
            } else {
                switch (getLayoutPosition()) {
                    // HOME
                    case 1:
                        homeActivity.showAsRoot(ViewPagerFragment.class, "ViewPagerFragment");
                        break;
                    // ADD SCUBIT
                    case 2:
                        homeActivity.drawerItemClicked("ADD SCUBIT");
                        loginScubeAndChat();
                        break;
                    // SETTINGS
                    case 3:
                        homeActivity.drawerItemClicked("SETTINGS");
                        // Settings view fragment push
                        break;
                    // LOGIN
                    case 4:
                        // Show the login screen to the user
                        homeActivity.navigateToLogin("false");
                        break;
                }
            }
            // Upon clicking anything, close the drawer
            drawerLayout.closeDrawers();
        }
    }

    /*
        If scube logged in : Try logging into chat
        If neither logged in : display alert box to request user to login
    */
    private void loginScubeAndChat() {
        try {
            if (loginDetails.getString("status") == context.getString(R.string.user_session_true)) {
                Log.d(context.getString(R.string.Side_Menu_Drawer_Fragment), "Scube Already Logged in. Init Chat Login");
                // Scube chat logged in, just login to chat
                ChatLogin.loginToChat(context, null, homeActivity);

                // Check if the user is validated in shared preferences first.
                if(!GlobalUtils.getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.email_validation_state)).equals("true")) {
                    // Get to know if Email Validation is over through API call.
                    // If not validated, we show this dialog box.

                    UserApiHelper userApiHelper = new UserApiHelper(context, "ViewPagerFragment");
                    userApiHelper.getEmailValidationStatus(loginDetails.getInt("scubeId"), new ApiResponse.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject userStatus) {
                            try {
                                if (!userStatus.getString("status_description").equals("Active")) {
                                    // User has not yet validated login
                                    homeActivity.showEmailValidationDialog(context.getString(R.string.email_validation_dialog_title));
                                } else {
                                    GlobalUtils.addSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.email_validation_state), "true");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                Log.d(context.getString(R.string.Side_Menu_Drawer_Fragment), "Neither Scube nor chat logged in. request user to login using alert box");
                // Neither Scube nor chat logged in. Request user to login using alert dialog
                homeActivity.showLoginDialog(context.getString(R.string.login_request_title_to_add_scubit));
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
