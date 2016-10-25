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
 * Created by srikanthsridhara on 9/12/15.
 */
public class EmailValidationAlertDialog extends DialogFragment {
    public static Activity thisActivity;
    public static EmailValidationAlertDialog newInstance(String message, Activity activity) {
        thisActivity = activity;
        EmailValidationAlertDialog frag = new EmailValidationAlertDialog();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(message)
                .setPositiveButton(R.string.email_validation_dialog_resend,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                invokeActivityCallback(true);
                            }
                        }
                )
                .setNegativeButton(R.string.email_validation_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                invokeActivityCallback(false);
                            }
                        }
                )
                .create();

        alertDialog.setCanceledOnTouchOutside(false);

        return  alertDialog;
    }

    private void invokeActivityCallback(Boolean status) {
        if(thisActivity.getClass().getName().equals(HomeActivity.class.getName())) {
            HomeActivity requestingActivity = (HomeActivity) thisActivity;
            if(status) {
                requestingActivity.userAcceptedResendEmailValidation();
            } else {
                requestingActivity.userCancelledResendEmailValidation();
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
