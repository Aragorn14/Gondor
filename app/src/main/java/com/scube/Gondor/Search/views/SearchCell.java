package com.scube.Gondor.Search.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scube.Gondor.R;
import com.scube.Gondor.Search.models.SearchModel;
import com.scube.Gondor.Util.FontUtil;
import com.squareup.picasso.Picasso;
//import com.mobile.ign.search.model.SearchModel;

/**
 * Created by vashoka on 05/29/15.
 */
public class SearchCell extends LinearLayout
{
    private ImageView imageView;
    private TextView titleTextView;
    private TextView contextTextView;

    public SearchCell(Context context)
    {
        super(context);

        init(context);
    }

    public SearchCell(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init(context);
    }

    public SearchCell(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.search_cell, this, true);

        imageView = (ImageView) this.findViewById(R.id.searchCellImageView);

        titleTextView = (TextView) this.findViewById(R.id.searchCellTitleTextView);
        titleTextView.setTypeface(FontUtil.getARSMaquetteFont());

        contextTextView = (TextView) this.findViewById(R.id.searchCellContextTextView);
        contextTextView.setTypeface(FontUtil.getDinRoundPro());
    }

    public void populate(final SearchModel model)
    {
        titleTextView.setText(Html.fromHtml(model.title));
        contextTextView.setText(model.modelContext);
        if (model.imageUrl != null) {
            Picasso.with(this.getContext())
                    .load(model.imageUrl)
                    .placeholder(R.drawable.abc_btn_borderless_material)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.abc_btn_borderless_material);
        }
    }
}
