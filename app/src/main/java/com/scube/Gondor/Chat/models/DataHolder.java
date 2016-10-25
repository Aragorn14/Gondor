package com.scube.Gondor.Chat.models;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.users.model.QBUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataHolder {

    private static DataHolder dataHolder;
    private List<QBUser> qbUsersList = new ArrayList<QBUser>();
    private QBUser signInQbUser;
    private Map<Integer, QBUser> dialogsUsers = new HashMap<Integer, QBUser>();
    private Map<String,QBDialog> myDialogs = new HashMap<String,QBDialog>();
    private JSONObject scubitFeed = new JSONObject();

    public static synchronized DataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new DataHolder();
        }
        return dataHolder;
    }

    public void setQbUsersList(List<QBUser> qbUsersList) {
        this.qbUsersList = qbUsersList;
    }

    public int getQBUserListSize() {
        return qbUsersList.size();
    }

    public String getQBUserName(int index) {
        return qbUsersList.get(index).getFullName();
    }

    public List<String> getQbUserTags(int index) {
        return qbUsersList.get(index).getTags();
    }

    public QBUser getQBUser(int index) {
        return qbUsersList.get(index);
    }

    public QBUser getLastQBUser() {
        return qbUsersList.get(qbUsersList.size() - 1);
    }

    public void addQbUserToList(QBUser qbUser) {
        qbUsersList.add(qbUser);
    }

    public QBUser getSignInQbUser() {
        return signInQbUser;
    }

    public void setSignInQbUser(QBUser singInQbUser) {
        this.signInQbUser = singInQbUser;
    }

    public String getSignInUserOldPassword() {
        return signInQbUser.getOldPassword();
    }

    public int getSignInUserId() {
        return signInQbUser.getId();
    }

    public void setSignInUserPassword(String singInUserPassword) {
        signInQbUser.setPassword(singInUserPassword);
    }

    public String getSignInUserLogin() {
        return signInQbUser.getLogin();
    }

    public String getSignInUserEmail() {
        return signInQbUser.getEmail();
    }

    public String getSignInUserFullName() {
        return signInQbUser.getFullName();
    }

    public String getSignInUserPhone() {
        return signInQbUser.getPhone();
    }

    public String getSignInUserWebSite() {
        return signInQbUser.getWebsite();
    }

    public String getSignInUserTags() {
        if (signInQbUser.getTags() != null) {
            return signInQbUser.getTags().getItemsAsString();
        } else {
            return "";
        }
    }

    // CHAT RELATED METHODS

    public Map<Integer, QBUser> getDialogsUsers() {
        return dialogsUsers;
    }

    public void setDialogsUsers(List<QBUser> setUsers) {
        dialogsUsers.clear();

        for (QBUser user : setUsers) {
            dialogsUsers.put(user.getId(), user);
        }
    }

    public void addDialogsUsers(List<QBUser> newUsers) {
        for (QBUser user : newUsers) {
            dialogsUsers.put(user.getId(), user);
        }
    }

    public Integer getOpponentIDForPrivateDialog(QBDialog dialog){
        Integer opponentID = -1;
        for(Integer userID : dialog.getOccupants()){
            if(!userID.equals(getSignInQbUser().getId())){
                opponentID = userID;
                break;
            }
        }
        return opponentID;
    }

    public Map<String, QBDialog> getMyDialogs () {return myDialogs;}
    public void addDialog(String dialogId, QBDialog dialog) {this.myDialogs.put(dialogId, dialog);}

    public JSONObject getScubits() { return scubitFeed;}
    public void setScubits(JSONObject scubitFeed) {this.scubitFeed = scubitFeed;}

}
