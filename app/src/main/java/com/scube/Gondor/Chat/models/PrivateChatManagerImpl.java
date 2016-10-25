package com.scube.Gondor.Chat.models;

import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.scube.Gondor.Chat.controllers.ChatActivity;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

public class PrivateChatManagerImpl extends QBMessageListenerImpl<QBPrivateChat> implements ChatManager, QBPrivateChatManagerListener {

    private static final String TAG = "PrivateChatManagerImpl";

    private ChatActivity chatActivity;

    private QBPrivateChatManager privateChatManager;
    private QBPrivateChat privateChat;

    public PrivateChatManagerImpl(ChatActivity chatActivity, Integer opponentID) {
        this.chatActivity = chatActivity;

        privateChatManager = QBChatService.getInstance().getPrivateChatManager();

        privateChatManager.addPrivateChatManagerListener(this);

        // init private chat
        //
        privateChat = privateChatManager.getChat(opponentID);
        Log.w(TAG, "Created pvt chat manager");

        if (privateChat == null) {
            Log.w(TAG, "pvt chat is NULL");
            privateChat = privateChatManager.createChat(opponentID, this);
            //privateChat.addMessageListener(this);
        } else {
            privateChat.addMessageListener(this);
        }
    }

        @Override
        public void sendMessage (QBChatMessage message)throws
                XMPPException, SmackException.NotConnectedException {
            Log.w(TAG,"Sending Message:"+message.toString());
            privateChat.sendMessage(message);
        }

        @Override
        public void release () {
            Log.w(TAG, "release private chat");
            privateChat.removeMessageListener(this);
            privateChatManager.removePrivateChatManagerListener(this);
        }

        @Override
        public void processMessage (QBPrivateChat chat, QBChatMessage message){
            Log.w(TAG, "new incoming message: " + message);
            chatActivity.showMessage(message);
        }

        @Override
        public void processError (QBPrivateChat chat, QBChatException error, QBChatMessage
        originChatMessage){

        }

        @Override
        public void chatCreated (QBPrivateChat incomingPrivateChat,boolean createdLocally){
            if (!createdLocally) {
                privateChat = incomingPrivateChat;
                privateChat.addMessageListener(PrivateChatManagerImpl.this);
            }

            Log.w(TAG, "private chat created: " + incomingPrivateChat.getParticipant() + ", createdLocally:" + createdLocally);
        }
}
