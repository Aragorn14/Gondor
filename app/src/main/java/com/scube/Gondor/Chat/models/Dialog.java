package com.scube.Gondor.Chat.models;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.scube.Gondor.Helpers.api.UserApiHelper;
import com.scube.Gondor.Login.models.UserProfile;
import com.scube.Gondor.R;
import com.scube.Gondor.UI.CircleTransform;
import com.scube.Gondor.Util.Interfaces.ApiResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikanthsridhara on 7/18/15.
 */
public class Dialog {
    private String dialogId;
    private Integer creatorQBUserId;
    private List<UserProfile> participantProfiles = new ArrayList<UserProfile>();
    private QBDialog qbDialog;

    public Dialog(String dialogId, Integer creatorQBUserId, QBDialog qbDialog) {
        this.dialogId = dialogId;
        this.creatorQBUserId = creatorQBUserId;
        this.qbDialog = qbDialog;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setCreatorQBUserId(Integer qbUserId) {
        this.creatorQBUserId = qbUserId;
    }

    public Integer getCreatorQBUserId() {
        return creatorQBUserId;
    }

    public List<UserProfile> getParticipantProfiles() {
        return participantProfiles;
    }

    // ParticipantId = QB User ID
    public void addParticipantAndFetchProfile(Integer participantQBUserId, Context context, String fragmentTag, View interestedMember) {
        getUserProfile(participantQBUserId, context, fragmentTag, interestedMember);
    }

    public UserProfile getParticipantProfile(Integer position) {
        return participantProfiles.get(position);
    }

    public void getUserProfile(Integer qbUserId, final Context context, String fragmentTag, final View interestedMember) {
        UserApiHelper userApiHelper = new UserApiHelper(context, fragmentTag);
        userApiHelper.getProfileByQBUserId(qbUserId, new ApiResponse.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject userProfileObj) {
                /*
                    Response For success:
                    {
                       username: "maithu31@gmail.com",
                       firstname: null,
                       lastname: null,
                       gender: "Male",
                       dob: "1988-12-12T08:00:00.000Z",
                       location: "milpitas",
                       phone: null,
                       imageurl: null,
                       qb_user_id: 1000
                    }
                    For failure:
                    {
                       "user_status" : -1,
                       "user_reason" : "failure
                    }
                 */
                UserProfile userProfile = new UserProfile(userProfileObj, context);

                ImageView profileImage = (ImageView) interestedMember.findViewById(R.id.profileImage);
                TextView memberName = (TextView) interestedMember.findViewById(R.id.memberName);

                Picasso.with(context)
                        .load(userProfile.getImageUrl())
                        .transform(new CircleTransform())
                        .into(profileImage);

                memberName.setText(userProfile.getUserName());

                // Save userprofile
                participantProfiles.add(userProfile);
            }
        });
    }

    public QBDialog getQbDialog() {
        return this.qbDialog;
    }

    public void setQBDialog(QBDialog qbDialog) {
        this.qbDialog = qbDialog;
    }
}

