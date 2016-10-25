package com.scube.Gondor.Menu.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scube.Gondor.Menu.model.FilterMenuModel;
import com.scube.Gondor.R;
import com.scube.Gondor.Util.FontUtil;

/**
 * Created by jschroeder on 10/31/13.
 */
public class FilterMenuCell extends LinearLayout
{
    private TextView textView;
    private FilterMenuView.FilterMenuTheme theme = null;
    private FilterMenuModel currentFilterMenuModel = null;

    public FilterMenuCell(Context context)
    {
        super(context);

        init(context);
    }

    public FilterMenuCell(Context context, FilterMenuView.FilterMenuTheme theme)
    {
        super(context);

        this.theme = theme;
        init(context);
    }

    public void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.filter_menu_cell, this, true);
        textView = (TextView)findViewById(R.id.textView);
        textView.setTypeface(FontUtil.getDinRoundProBold());
    }

    public void populate(FilterMenuModel filterMenuModel)
    {
        currentFilterMenuModel = filterMenuModel;
        textView.setText(filterMenuModel.getDisplayTitle());
    }

    public void highlight(boolean highlight)
    {
        int color = highlight ? R.color.WHITE : R.color.GRAY_BLUE;
        textView.setTextColor(getResources().getColor(color));
    }

    public FilterMenuModel getCurrentFilterMenuModel()
    {
        return currentFilterMenuModel;
    }
}
