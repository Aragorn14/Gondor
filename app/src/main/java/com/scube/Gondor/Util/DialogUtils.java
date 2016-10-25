package com.scube.Gondor.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;


public class DialogUtils {

    private static Toast toast;

    public static void show(Context context, String message) {
        if (message == null) {
            return;
        }
        if (toast == null && context != null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }
        if (toast != null) {
            toast.setText(message);
            toast.show();
        }
    }

    public static ProgressDialog getProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        return progressDialog;
    }

    public static void showLong(Context context, String message) {
        if (message == null) {
            return;
        }
        if (toast == null && context != null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        }
        if (toast != null) {
            toast.setText(message);
            toast.show();
        }
    }

    public static Dialog createDialog(Context context, int titleId, int messageId,
            DialogInterface.OnClickListener positiveButtonListener,
            DialogInterface.OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId);
        builder.setMessage(messageId);
        builder.setPositiveButton("OK", positiveButtonListener);
        builder.setNegativeButton("Cancel", negativeButtonListener);

        return builder.create();
    }

    public static Dialog createDialog(Context context, int titleId, int messageId, View view,
            DialogInterface.OnClickListener positiveClickListener,
            DialogInterface.OnClickListener negativeClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId);
        builder.setMessage(messageId);
        builder.setView(view);
        builder.setPositiveButton("OK", positiveClickListener);
        builder.setNegativeButton("Cancel", negativeClickListener);

        return builder.create();
    }
}