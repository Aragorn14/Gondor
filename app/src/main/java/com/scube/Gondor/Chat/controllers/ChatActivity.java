package com.scube.Gondor.Chat.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.scube.Gondor.Chat.models.ChatManager;
import com.scube.Gondor.Chat.models.DataHolder;
import com.scube.Gondor.Chat.models.GroupChatManagerImpl;
import com.scube.Gondor.Chat.models.PrivateChatManagerImpl;
import com.scube.Gondor.Chat.views.ChatAdapter;
import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.GlobalUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends Activity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_DIALOG = "dialog";
    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    private Context context;
    private EditText messageEditText;
    private ListView messagesContainer;
    private ImageView sendButton;

    private ProgressBar progressBar;

    private Mode mode = Mode.PRIVATE;
    private ChatManager chat;
    private ChatAdapter adapter;
    private QBDialog dialog;

    private ArrayList<QBChatMessage> history;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d("DEBUG", "Chat Activity Created");
        context = getApplicationContext();
        // Save Activity
        GlobalUtils.addSharedPreference(context, getString(R.string.sp_nav), getString(R.string.sp_latest_activity), context.getClass().getSimpleName());

        initViews();
    }

    @Override
    public void onBackPressed() {
        Log.d(getString(R.string.ACTIVITY_CHAT), "BACK PRESSED");
        try {
            chat.release();
        } catch (XMPPException e) {
            Log.e(TAG, "failed to release chat", e);
        }

        String cameFromNotification = GlobalUtils.getSharedPreference(this, this.getString(R.string.sp_nav), "cameFromNotification");
        if (cameFromNotification.equals("true")) {
            GlobalUtils.removeSharedPreference(this, this.getString(R.string.sp_nav), "cameFromNotification");
            Log.d(getString(R.string.ACTIVITY_SCUBITS), "Back pressed: Came from Notification, so going to Home Activity");
            Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        super.onBackPressed();
    }

    private void initViews() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageEditText = (EditText) findViewById(R.id.messageEdit);
        sendButton = (ImageView) findViewById(R.id.chatSendButton);

        final TextView meLabel = (TextView) findViewById(R.id.meLabel);
        final TextView companionLabel = (TextView) findViewById(R.id.companionLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();

        // Get chat dialog
        //
        dialog = (QBDialog) intent.getSerializableExtra(EXTRA_DIALOG);

        mode = (Mode) intent.getSerializableExtra(EXTRA_MODE);
        switch (mode) {
            case GROUP:
                chat = new GroupChatManagerImpl(this);
                //container.removeView(meLabel);
                //container.removeView(companionLabel);

                // Join group chat
                progressBar.setVisibility(View.VISIBLE);
                ((GroupChatManagerImpl) chat).joinGroupChat(dialog, new QBEntityCallbackImpl() {
                    @Override
                    public void onSuccess() {
                        companionLabel.setText(dialog.getOccupants().toString());
                        meLabel.setText(dialog.getUserId().toString());

                        // Load Chat history
                        loadChatHistory();
                    }

                    @Override
                    public void onError(List list) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                        dialog.setMessage("error when join group chat: " + list.toString()).create().show();
                    }
                });
                break;
            case PRIVATE:
                final Integer opponentID = DataHolder.getDataHolder().getOpponentIDForPrivateDialog(dialog);
                Log.w(getString(R.string.ACTIVITY_CHAT), "The Opponent ID is:" + opponentID);

                chat = new PrivateChatManagerImpl(this, opponentID);
                QBUsers.getUser(opponentID, new QBEntityCallbackImpl<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle params) {
                        //Log.d("ChatActivity", DataHolder.getDataHolder().getOpponentUser(opponentID).getLogin());
                        companionLabel.setText(qbUser.getLogin());
                        // Load Chat history
                        loadChatHistory();
                    }

                    @Override
                    public void onError(List list) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                        dialog.setMessage("Error when joining private chat: " + list.toString()).create().show();
                    }
                });

                break;
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                // Send chat message
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(messageText);
                chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                chatMessage.setDateSent(new Date().getTime() / 1000);

                try {
                    chat.sendMessage(chatMessage);
                } catch (XMPPException e) {
                    Log.e(getString(R.string.ACTIVITY_CHAT), "failed to send a message", e);
                } catch (SmackException sme) {
                    Log.e(getString(R.string.ACTIVITY_CHAT), "failed to send a message", sme);
                }

                messageEditText.setText("");

                if (mode == Mode.PRIVATE) {
                    showMessage(chatMessage);
                }
            }
        });
    }

    private void loadChatHistory() {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                history = messages;
                adapter = new ChatAdapter(ChatActivity.this, new ArrayList<QBChatMessage>());
                messagesContainer.setAdapter(adapter);
                for (int i = messages.size() - 1; i >= 0; --i) {
                    QBChatMessage msg = messages.get(i);
                    showMessage(msg);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                dialog.setMessage("load chat history errors: " + errors).create().show();
            }
        });
    }

    public void showMessage(QBChatMessage message) {
        adapter.add(message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                scrollDown();
            }
        });
    }

    private void scrollDown() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    public enum Mode {PRIVATE, GROUP}

}

