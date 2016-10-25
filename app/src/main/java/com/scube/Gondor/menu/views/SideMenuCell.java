package com.scube.Gondor.Menu.views;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scube.Gondor.Menu.model.SideMenuModel;
import com.scube.Gondor.R;
import com.scube.Gondor.UI.CircleTransform;
import com.scube.Gondor.Util.FontUtil;
import com.scube.hoverboard.src.main.java.com.hoverboard.ui.ViewHelper;
import com.squareup.picasso.Picasso;

/**
 * Created by vashoka on 05/30/15.
 */
public class SideMenuCell extends LinearLayout
{
    private TextView titleTextView;
    private ImageView supplementaryImageView;
    private TextView supplementaryTextView;
    private ImageView imageView;
    public CheckBox checkBox;

    public SideMenuCell(Context context)
    {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.side_menu_cell, this, true);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setTypeface(FontUtil.getDinRoundProBold());

        imageView = (ImageView) findViewById(R.id.iconImageView);
        supplementaryImageView = (ImageView) findViewById(R.id.supplementaryImageView);

        supplementaryTextView = (TextView) findViewById(R.id.supplementaryTextView);
        supplementaryTextView.setTypeface(FontUtil.getDinRoundProBold());

        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }

    public void populate(SideMenuModel model)
    {
        titleTextView.setText(model.title);
        titleTextView.setAlpha(model.isEnabled ? 1.0f : 0.3f);
        try {
            imageView.setImageDrawable(getResources().getDrawable(model.imageId > -1 ? model.imageId : R.color.DARK_GRAY));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (model.imageUrlString != null) {
            if (model.roundedCorners) {
                Picasso.with(getContext())
                        .load(model.imageUrlString)
                        .transform(new CircleTransform())
                        .into(imageView);
                imageView.getLayoutParams().height = ViewHelper.dpToPx(28, getContext());
            } else {
                Picasso.with(getContext())
                        .load(model.imageUrlString)
                        .into(imageView);
            }
        } else {
            imageView.getLayoutParams().height = ViewHelper.dpToPx(30, getContext());
        }
        imageView.setAlpha(model.isEnabled ? 1.0f : 0.3f);

        checkBox.setVisibility(model.headerModel != null && model.headerModel.isEditing ? VISIBLE : GONE);
        checkBox.setChecked(model.isChecked);
        if (model.supplementaryImageId > 0) {
            supplementaryImageView.setImageDrawable(getResources().getDrawable(model.supplementaryImageId));
            supplementaryImageView.setVisibility(VISIBLE);
        } else {
            supplementaryImageView.setVisibility(GONE);
        }

        if (model.supplementaryString != null) {
            supplementaryTextView.setVisibility(VISIBLE);
            supplementaryTextView.setText(model.supplementaryString);
        } else {
            supplementaryTextView.setVisibility(GONE);
        }

        highlight(model);
    }

    public void highlight(SideMenuModel model)
    {
        int color = model.isSelected ? R.color.WHITE : model.textColorId;
        titleTextView.setTextColor(getResources().getColor(color));

        int backgroundColor = model.isSelected ? R.color.WHITE : R.color.WHITE;
        setBackgroundColor(getResources().getColor(backgroundColor));
    }
}
