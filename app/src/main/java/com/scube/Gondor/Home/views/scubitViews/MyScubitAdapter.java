package com.scube.Gondor.Home.views.scubitViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.scube.Gondor.Chat.models.Dialog;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.R;
import com.scube.Gondor.Home.models.scubitModels.MyScubitModel;
import com.scube.Gondor.UI.CircleTransform;
import com.scube.Gondor.UI.TriangleShapeView;
import com.scube.Gondor.Util.ChatUtil;
import com.scube.Gondor.Util.Interfaces.QbDialogsResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by srikanthsridhara on 6/27/15.
 */
public class MyScubitAdapter extends BaseAdapter {
    private Activity activity;
    private List<MyScubitModel> myScubitModelList;
    private QbDialogsResponse.Listener<Boolean> qbDialogsResponse;
    private ArrayList<QBDialog> qbDialogsList;

    LayoutInflater inflater;
    Context context;

    public MyScubitAdapter(Activity activity, List<MyScubitModel> myScubits, Context context, QbDialogsResponse.Listener<Boolean> qbDialogsResponse) {
        this.activity = activity;
        this.myScubitModelList = myScubits;
        this.context = context;
        this.qbDialogsResponse = qbDialogsResponse;

        // Fetch all the dialogs (Created and interacted) of "self" from QB
        Log.d(context.getString(R.string.ADAPTER_MY_SCUBITS), "myScubit adapter instantiated");
        getQBUserDialogs();
    }

    @Override
    public int getCount() {
        return myScubitModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return myScubitModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        TextView offerName, mallName, shopName, brandName, timerHeading;
        ImageView brandWall, ownerProfileImage;
        TriangleShapeView triangleShapeView;
        HorizontalScrollView interestedMembersHorizontalScrollView;

        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.card_view_my_scubit, null);
        }

        MyScubitModel myScubitModel = myScubitModelList.get(position);
        AddDialogsForModel(myScubitModel);

        offerName = (TextView) convertView.findViewById(R.id.offerName);
        brandName = (TextView) convertView.findViewById(R.id.brandName);
        shopName = (TextView) convertView.findViewById(R.id.shopName);
        mallName = (TextView) convertView.findViewById(R.id.mallName);
        timerHeading = (TextView) convertView.findViewById(R.id.timerHeading);

        ownerProfileImage = (ImageView) convertView.findViewById(R.id.ownerProfileImage);
        brandWall = (ImageView) convertView.findViewById(R.id.brandWall);

        // Fill Text Data
        if(myScubitModel.getOfferName() != null) {
            offerName.setText(myScubitModel.getOfferName());
        }
        if(myScubitModel.getMallName() != null) {
            mallName.setText(myScubitModel.getMallName());
        }
        if(myScubitModel.getShopName() != null) {
            shopName.setText(myScubitModel.getShopName());
        }
        if(myScubitModel.getBrandName() != null) {
            //    holder.brandName.setText(myScubitModel.getBrandName());
        }

        addMemberViewToHorizontalScrollView(myScubitModel, convertView);

        return convertView;
    }

    public void addMemberViewToHorizontalScrollView(MyScubitModel myScubitModel, View convertView) {
        for(int i=0; i<myScubitModel.getDialogs().size(); i++) {
            Dialog thisDialog = myScubitModel.getDialogs().get(i);

            // V2
            HorizontalScrollView interestedMembersScrollView = (HorizontalScrollView) convertView.findViewById(R.id.interestedMembersHorizontalScrollView);
            LinearLayout childMemberLinearlayout = (LinearLayout) interestedMembersScrollView.findViewById(R.id.interestedMembersHorizontalLinearLayout);

            childMemberLinearlayout.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View interestedMember = inflater.inflate(R.layout.my_scubit_interested_member, null);

            ImageView profileImage = (ImageView) interestedMember.findViewById(R.id.profileImage);

            // Set image and member name data. Backup profile image will be loaded as a placeholder
            // before user profile API response kicks in
            Picasso.with(context)
                    .load("https://www.myzydeco.com/assets/blank_user_icon.png")
                    .transform(new CircleTransform())
                    .into(profileImage);

            // Set and id as an index & save the user dialog to member view for use upon click
            interestedMember.setId(i);
            interestedMember.setTag(R.integer.interested_user_profile_image_tag, myScubitModel.getDialogs().get(i));

            // Onclick action or member view : Start that particular chat
            interestedMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatUtil chatUtil = new ChatUtil(context, activity);
                    Dialog dialog = (Dialog) v.getTag(R.integer.interested_user_profile_image_tag);
                    chatUtil.goToParticularChat(dialog.getDialogId());
                }
            });

            // Add all the occupants other than "self" into participants list in the dialog
            thisDialog.getParticipantProfiles().clear();
            ArrayList<Integer> dialogOccupants = thisDialog.getQbDialog().getOccupants();
            for (int j = 0; j < dialogOccupants.size(); j++) {
                Integer occupantId = dialogOccupants.get(j);
                if (!occupantId.equals(thisDialog.getCreatorQBUserId())) {
                    thisDialog.addParticipantAndFetchProfile(occupantId, context, "MyScubitsListFragment", interestedMember);
                }
            }

            // Add member to the horizontal linear view inside the horizontal scroll view
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            childMemberLinearlayout.addView(interestedMember, layoutParams);

            Log.d(context.getString(R.string.ADAPTER_MY_SCUBITS), "Adding New interested member with id = " + i + "to scroll view");
        }
    }

    public void getQBUserDialogs() {
        // Here, we need to get All scubits that the current user has created.
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);

        Log.d(context.getString(R.string.ADAPTER_MY_SCUBITS), "Requesting Qb user dialogs");
        // Populate my qbdialogs list
        QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> qbDialogs, Bundle args) {

                Log.d(context.getString(R.string.ADAPTER_MY_SCUBITS), "Successfully received qb dialogs "+qbDialogs);
                // Notify MyScubitAdapter that chat dislogs are received from QB
                qbDialogsResponse.onResponse(true);
                qbDialogsList = qbDialogs;
            }

            @Override
            public void onError(List<String> errors) {
                Log.e(context.getString(R.string.ADAPTER_MY_SCUBITS), "getChatDialogs failure");
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("get qbdialogs errors: " + errors).create().show();

                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public void AddDialogsForModel(MyScubitModel myScubitModel) {
        if(qbDialogsList == null) return;

        // Temp list to save the scubit models with inflated dialogs
        List<MyScubitModel> myScubitModelsWithDialogs = new ArrayList<MyScubitModel>();

        // Loop through all the dialogs of the current user and populate the ones which he has created into the model
        for (QBDialog qbDialog : qbDialogsList) {
            // Fetch dialog info
            Map<String, String> dialogData = qbDialog.getData();
            Integer dialogScubitId = Integer.parseInt(dialogData.get("scubit_id")),
                    dialogOwnerQBUserId = Integer.parseInt(dialogData.get("qb_user_id"));
            String dialogId = qbDialog.getDialogId();

            // If the creator of the scubit is "self" then add the dialog to the myscubit model dialog list
            if (myScubitModel.getScubitId().equals(dialogScubitId)) {
                Dialog dialog = new Dialog(dialogId, dialogOwnerQBUserId, qbDialog);

                // After populating all the occupants into the dialog, save the dialog to the "my scubit" model
                myScubitModel.addDialog(dialog);
                Log.d(context.getString(R.string.FRAGMENT_MY_SCUBITS), "Adding dialog" + dialog + "to scubit id " + dialogScubitId);
            }
        }
    }
}
