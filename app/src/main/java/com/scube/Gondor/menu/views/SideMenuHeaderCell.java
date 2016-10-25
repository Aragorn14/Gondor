package com.scube.Gondor.Menu.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scube.Gondor.Menu.model.SideMenuHeaderModel;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.FontUtil;

/**
 * Created by vashoka on 05/30/15.
 */
public class SideMenuHeaderCell extends LinearLayout
{
    private TextView titleTextView;
    private Context mContext;
    public LinearLayout editBar;
    public LinearLayout defaultBar;
    public LinearLayout editButton;
    public TextView editTextView;
    private ImageView editImageView;
    public ImageButton doneButton;
    public Button deleteButton;

    public SideMenuHeaderCell(Context context)
    {
        super(context);

        init(context);
    }

    public SideMenuHeaderCell(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init(context);
    }

    public SideMenuHeaderCell(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context)
    {
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.side_menu_header_cell, this, true);

        defaultBar = (LinearLayout) findViewById(R.id.defaultBar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setTypeface(FontUtil.getARSMaquetteBold());

        editButton = (LinearLayout) findViewById(R.id.editButton);

        editTextView = (TextView) findViewById(R.id.editTextView);
        editTextView.setTypeface(FontUtil.getARSMaquetteBold());

        editImageView = (ImageView) findViewById(R.id.editImage);

        editBar = (LinearLayout) findViewById(R.id.editBar);
        doneButton = (ImageButton) findViewById(R.id.doneButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
    }

    public void populate(SideMenuHeaderModel model)
    {
        if (model != null) {
            titleTextView.setText(model.title);
            editButton.setVisibility(model.isEditable ? VISIBLE : GONE);
            editBar.setVisibility(model.isEditing ? VISIBLE : GONE);
            defaultBar.setVisibility(model.isEditing ? GONE : VISIBLE);
            deleteButton.setAlpha(model.numChildrenChecked > 0 ? 1.0f : 0.3f);
            deleteButton.setEnabled(model.numChildrenChecked > 0);
        }
    }

    public void populateWithText(String text)
    {
        if (text != null) {
            titleTextView.setText(text);
            editButton.setVisibility(GONE);
            editBar.setVisibility(GONE);
            defaultBar.setVisibility(VISIBLE);
            deleteButton.setAlpha(1.0f);
            deleteButton.setEnabled(true);
        }
    }
}
