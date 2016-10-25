package com.scube.Gondor.Home.controllers.fragments.scubitFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Helpers.api.ScubitApiHelper;
import com.scube.Gondor.R;
import com.scube.Gondor.Home.models.scubitModels.MyScubitModel;
import com.scube.Gondor.Home.views.scubitViews.MyScubitAdapter;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;
import com.scube.Gondor.Util.Interfaces.QbDialogsResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vashoka on 7/11/15.
 */
public class MyScubitsFragment extends NavigationFragment {

    private Context context;
    public Bundle bundle = new Bundle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_scubits, container, false);
        context = getActivity().getApplicationContext();
        // Save Activity
        GlobalUtils.addSharedPreference(context, getString(R.string.sp_nav), getString(R.string.sp_latest_activity), context.getClass().getSimpleName());

        bundle = this.getArguments();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        MyScubitsListFragment myScubitsListFragment = new MyScubitsListFragment();

        // Pass profile and profile object for list context
        myScubitsListFragment.setArguments(bundle);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.my_scubits_list_fragment_container, myScubitsListFragment);
        ft.commit();

        // Add tag for accessibility
        v.setTag("MyScubitsFragment");

        return v;
    }

    /**
     * Created by srikanthsridhara on 6/27/15.
     */
    public static class MyScubitsListFragment extends ListFragment {

        private MyScubitAdapter myScubitAdapter;
        List<MyScubitModel> myScubitModels = new ArrayList<MyScubitModel>();
        HashMap<String, List<String>> myDialogList = new HashMap<String, List<String>>();
        HashMap<String, List<String>> myOpponentList = new HashMap<String, List<String>>();
        Context context;

        /*
        *  myDialogList   is a hash of <Scubit ID> <Dialog Ids of that Scubit>
        *  myOpponentList is a hash of <Scubit ID> <Opponent names of that Scubit>
        */

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            context = getActivity().getApplicationContext();

            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_my_scubits_list, container, false);

            /* Building My Scubits
             1. Fetch all the scubits current user created from scube db
             2. Create My Scubit Models list
             3. Fetch all the dialogs (Created by current user along with the ones current user has interacted with)
             4. Loop through My Scubit Models, pull the dialogs from qbDialog array where scubitId matches and add them into scubit dialog list
            */
            ScubitApiHelper scubitApiHelper = new ScubitApiHelper(context, "MyScubitsListFragment");
            scubitApiHelper.getScubitsOfUser(new ApiResponse.Listener<List<MyScubitModel>>() {

                @Override
                public void onResponse(List<MyScubitModel> scubitModelList) {
                    // Here we have my scubit models list.
                    myScubitModels.clear();
                    myScubitModels.addAll(scubitModelList);

                    QbDialogsResponse.Listener<Boolean> qbDialogsResponse = new QbDialogsResponse.Listener<Boolean>() {

                        @Override
                        public void onResponse(Boolean response) {
                            if (response) {
                                Log.d(getString(R.string.FRAGMENT_MY_SCUBITS), "QbDialogsResponse : dialogs received successfully");
                                myScubitAdapter.notifyDataSetChanged();
                            }
                        }
                    };

                    // After populating all the "my scubits" with associated dialogs and participants. Notify the adapter to construct the list view
                    myScubitAdapter = new MyScubitAdapter(getActivity(), myScubitModels, getActivity().getApplicationContext(), qbDialogsResponse);
                    setListAdapter(myScubitAdapter);

                    Log.d(getString(R.string.FRAGMENT_MY_SCUBITS), "myScubit models populated and myScubitAdapter created");
                }
            });

            // Add tag for accessibility
            v.setTag("MyScubitsListFragment");

            return v;
        }
    }
}
