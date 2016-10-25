package com.scube.Gondor.Home.controllers.fragments.scubitFragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.scube.Gondor.Chat.controllers.ChatActivity;
import com.scube.Gondor.Chat.models.DataHolder;
import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Helpers.api.ScubitApiHelper;
import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.R;
import com.scube.Gondor.Home.models.scubitModels.ScubitModel;
import com.scube.Gondor.Home.views.scubitViews.ScubitsAdapter;
import com.scube.Gondor.Util.ChatLogin;
import com.scube.Gondor.Util.ChatUtil;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srikanthsridhara on 6/20/15.
 */
public class ScubitsListFragment extends NavigationFragment {

    private RecyclerView scubitsListView;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;
    private ShopProfile shopProfile;
    private ArrayList<QBDialog> dialogs;

    List<ScubitModel> scubitModels = new ArrayList<ScubitModel>();
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "On Create View Entered");

        context = getActivity().getApplicationContext();

        View v = inflater.inflate(R.layout.fragment_scubits_list, container, false);
        scubitsListView = (RecyclerView) v.findViewById(R.id.scubitsList);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        // Set Layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        scubitsListView.setLayoutManager(layoutManager);

        // Get Bundle arguments
        String dialogId = getArguments().getString("dialogId");
        shopProfile = getArguments().getParcelable("shopProfile");
        if(dialogId != null) {
            Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "ENTERED FROM NOTIFICATION BAR");
            Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "Dialog Id: " + dialogId);
            ChatUtil chatUtil = new ChatUtil(context, this.getActivity());
            chatUtil.goToParticularChat(dialogId);
            return v;
        } else {
            Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "ENTERED NORMALLY");
        }
        if(shopProfile != null) {
            Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "Shop Profile Id:" + shopProfile.getShopProfileId());
        } else {
            Log.e(getString(R.string.FRAGMENT_SCUBITS_LIST), "Shop Profile Id not found");
        }

        // get dialogs
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);

        // populate my dialogs list
        QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogsPassed, Bundle args) {

                dialogs = dialogsPassed;
                ScubitApiHelper scubitApiHelper = new ScubitApiHelper(context, "ScubitsListFragment");
                scubitApiHelper.getScubitsFromShopProfile(shopProfile, dialogs, new ApiResponse.Listener<List<ScubitModel>>() {

                    @Override
                    public void onResponse(List<ScubitModel> scubitModelList) {
                        // Here we have the scubit models list.
                        scubitModels.clear();
                        scubitModels.addAll(scubitModelList);

                        ScubitsAdapter adapter = new ScubitsAdapter(scubitModels, getActivity());
                        scubitsListView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);

                        adapter.SetOnItemClickListener(new ScubitsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                onScubitClick(position);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {
                Log.e(getString(R.string.FRAGMENT_SCUBITS_LIST), "getChatDialogs failure");
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("get dialogs errors: " + errors).create().show();
                // TODO
//                ChatLogin.signUpUser();
            }
        });

        // Add tag for accessibility
        v.setTag("ScubitsListFragment");

        return v;
    }


    public QBDialog constructDialog(ScubitModel scubit) {

        String selectedScubitId = scubit.getScubitId();
        String scubitsOriginUserQBId = scubit.getOriginUserQBId();
        String scubitsOriginUserScubeId = scubit.getOriginUserScubeId();
        String whoAmI = DataHolder.getDataHolder().getSignInUserLogin();
        String myId =  String.valueOf(DataHolder.getDataHolder().getSignInUserId());

        Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "I am               : " + myId);
        Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "selected originator: " + scubitsOriginUserQBId);
        Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "selected scubit id : " + selectedScubitId);
        if (myId.equals(scubitsOriginUserQBId)) {
            Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "I can't have a dialog with myself!");
            return null;
        }

        // Create a new dialog
        QBDialog dialogToCreate = new QBDialog();
        dialogToCreate.setType(QBDialogType.GROUP);
        dialogToCreate.setName(selectedScubitId + "~" + scubitsOriginUserQBId + "~" + scubitsOriginUserScubeId + "~" + whoAmI + "~" + myId);
        ArrayList<Integer> occupantIds = new ArrayList<Integer>();
        // First add the origin id, then add my id.
        occupantIds.add(Integer.parseInt(scubitsOriginUserQBId));
        occupantIds.add(DataHolder.getDataHolder().getSignInUserId());
        dialogToCreate.setOccupantsIds(occupantIds);

        Map<String, String> data = new HashMap<String, String>();
        data.put("data[class_name]", "Scubit");
        data.put("data[scubit_id]", selectedScubitId);
        data.put("data[qb_user_id]", scubitsOriginUserQBId);
        dialogToCreate.setData(data);
        Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "Dialog to create:"+dialogToCreate);
        return dialogToCreate;
    }

    public void onScubitClick(final int position) {
        ScubitModel scubit = scubitModels.get(position);

        // If I already have an existing chat with the opponent,
        // reuse the dialog, else create a new dialog
        if (scubit.getDialog() != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);
            bundle.putSerializable(ChatActivity.EXTRA_DIALOG, scubit.getDialog());
            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else {
            QBDialog dialogToCreate = constructDialog(scubit);
            if (dialogToCreate == null) {
                return;
            }

            QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
                @Override
                public void onSuccess(QBDialog dialog, Bundle args) {
                    scubitModels.get(position).setDialog(dialog);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);
                    bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);
                    Log.d(getString(R.string.FRAGMENT_SCUBITS_LIST), "New Dialog: " + dialog);
                    // Save the dialog Id in dataholder
                    DataHolder.getDataHolder().addDialog(dialog.getDialogId(), dialog);

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }

                @Override
                public void onError(List<String> errors) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage("dialog creation errors: " + errors).create().show();
                    Log.e(getString(R.string.FRAGMENT_SCUBITS_LIST), "Error creating a new dialog");
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
