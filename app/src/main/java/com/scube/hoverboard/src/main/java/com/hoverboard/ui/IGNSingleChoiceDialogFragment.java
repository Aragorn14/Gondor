package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by fvelasquez on 27/03/2014.
 */
public class IGNSingleChoiceDialogFragment extends DialogFragment
{
    private String title = "Select a choice";
    private CharSequence[] choices;
    private int selectedItem;
    private IGNSingleChoiceDialogFragmentListener mListener;

    public IGNSingleChoiceDialogFragment(CharSequence[] choices, int selectedItem, IGNSingleChoiceDialogFragmentListener listener)
    {
        super();

        this.choices = choices;
        this.selectedItem = selectedItem;
        this.mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        // Specify the list array, the items to be selected by default (null for none),
        // and the listener thourgh witch to receive callbacks when items are selected
        builder.setSingleChoiceItems(choices, selectedItem, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mListener.selectedItemChanged(which);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        return alertDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface)
    {
        mListener.dialogDismissed();
    }

    public interface IGNSingleChoiceDialogFragmentListener
    {
        public void selectedItemChanged(int selectedItem);

        public void dialogDismissed();
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
