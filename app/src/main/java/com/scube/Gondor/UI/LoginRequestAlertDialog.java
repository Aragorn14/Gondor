package com.scube.Gondor.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.scube.Gondor.Home.controllers.HomeActivity;
import com.scube.Gondor.R;

/**
 * Created by vashoka on 7/11/15.
 */
public class LoginRequestAlertDialog extends DialogFragment {

    public static Activity thisActivity;
    public static LoginRequestAlertDialog newInstance(String message, Activity activity) {
        thisActivity = activity;
        LoginRequestAlertDialog frag = new LoginRequestAlertDialog();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(message)
                .setPositiveButton(R.string.login_alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                invokeActivityCallback(true);
                            }
                        }
                )
                .setNegativeButton(R.string.login_alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                invokeActivityCallback(false);
                            }
                        }
                )
                .create();
    }

    private void invokeActivityCallback(Boolean status) {
        if(thisActivity.getClass().getName().equals(HomeActivity.class.getName())) {
            HomeActivity requestingActivity = (HomeActivity) thisActivity;
            if(status) {
                requestingActivity.userAcceptedLoginRequest();
            } else {
                requestingActivity.userCancelledLoginRequest();
            }
//        } else if(thisActivity.getClass().getName().equals(ScubitsActivity.class.getName())) {
//            ScubitsActivity requestingActivity = (ScubitsActivity) thisActivity;
//            if(status) {
//                requestingActivity.userAcceptedLoginRequest();
//            } else {
//                requestingActivity.userCancelledLoginRequest();
//            }
        }
    }
}
