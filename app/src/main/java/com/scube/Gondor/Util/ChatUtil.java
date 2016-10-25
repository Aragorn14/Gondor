package com.scube.Gondor.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.scube.Gondor.Chat.controllers.ChatActivity;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikanthsridhara on 7/24/15.
 */
public class ChatUtil {

    private Context context;
    private Activity activity;

    public ChatUtil(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void goToParticularChat(String dialogId) {

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setPagesLimit(100);
        requestBuilder.addRule("_id", "eq", dialogId);

        QBChatService.getChatDialogs(null, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args) {
                Log.d(context.getString(R.string.Chat_Util), "QBChatService: Getting particular chat: " + dialogs);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialogs.get(0));

                Intent intent = new Intent(context, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                context.startActivity(intent);
                //activity.finish();
            }

            @Override
            public void onError(List<String> errors) {
                Log.e(context.getString(R.string.Chat_Util), "QBChatService: getChatDialogs failure");
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("QBChatService: get dialogs errors: " + errors).create().show();

                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //activity.finish();
            }
        });
    }
}
