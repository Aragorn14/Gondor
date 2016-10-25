package com.scube.Gondor.Menu.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.scube.Gondor.Menu.model.FilterMenuListener;
import com.scube.Gondor.Menu.model.FilterMenuModel;
import com.scube.Gondor.R;
import com.scube.hoverboard.src.main.java.com.hoverboard.ui.ViewHelper;

import java.util.List;

/**
 * Created by vashoka on 05/30/15.
 */
public class FilterMenuView extends HorizontalScrollView
{
    /**
     * Themes that can be used for the filter menu
     * <li>{@link #LIGHT_THEME}</li>
     * <li>{@link #DARK_THEME}</li>
     * <li>{@link #TRAILERS_THEME}</li>
     */
    public enum FilterMenuTheme
    {
        /**
         * WHITE bg GRAY text
         * Used in trailers month filter
         * DinRound
         */
        LIGHT_THEME,
        /**
         * BLACK bg WHITE text
         * Used in Search filter and SFL
         * DinRound
         */
        DARK_THEME
    }

    private static final int DARK_THEME_VALUE = 0;
    private static final int LIGHT_THEME_VALUE = 1;

    private boolean noScrollEnabled = false;
    private FilterMenuListener filterMenuListener;
    private Context mContext;
    private LinearLayout filtersView;
    private View redHighlight;
    private FilterMenuCell highlightedFilter;
    private WidthAndXAnimation animation;
    private FilterMenuTheme menuTheme = FilterMenuTheme.DARK_THEME;

    public FilterMenuView(Context context)
    {
        super(context);

        init(context);
    }

    public FilterMenuView(Context context, FilterMenuTheme theme)
    {
        super(context);

        this.menuTheme = theme;
        init(context);
    }

    public FilterMenuView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, new int[] {android.R.attr.text}, 0, 0);
        try {
            int selectedThemeValue = 1;
            if (selectedThemeValue != -1) {
                switch(selectedThemeValue) {
                    case LIGHT_THEME_VALUE:
                        menuTheme = FilterMenuTheme.LIGHT_THEME;
                        break;
                    case DARK_THEME_VALUE:
                        menuTheme = FilterMenuTheme.DARK_THEME;
                        break;
                }
            }
        } finally {
            a.recycle();
        }
        init(context);
    }

    public FilterMenuView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init(context);
    }

    public FilterMenuTheme getMenuTheme()
    {
        return menuTheme;
    }

    public void setMenuTheme(FilterMenuTheme menuTheme)
    {
        this.menuTheme = menuTheme;
    }

    public boolean isNoScrollEnabled()
    {
        return noScrollEnabled;
    }

    public void setNoScrollEnabled(boolean noScrollEnabled)
    {
        this.noScrollEnabled = noScrollEnabled;
    }

    public void setFilterMenuListener(FilterMenuListener listener)
    {
        this.filterMenuListener = listener;
    }

    private void init(Context context)
    {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.filter_menu, this, true);
        filtersView = (LinearLayout) findViewById(R.id.filtersView);
        //redHighlight = findViewById(R.id.redHighlightView);
    }

    public void addMenuItems(List<FilterMenuModel> menuItems)
    {
        for (FilterMenuModel menuItem : menuItems) {
            addMenuItem(menuItem, menuItems.size(), null);
        }
    }

    public void addMenuItem(final FilterMenuModel menuItem, int itemCount, final FilterMenuListener itemMenuListener)
    {
        FilterMenuCell cell = new FilterMenuCell(mContext, menuTheme);
        LinearLayout.LayoutParams layout =
                new LinearLayout.LayoutParams(noScrollEnabled ?
                        ViewHelper.getScreenWidth(mContext) / itemCount :
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        cell.setLayoutParams(layout);
        cell.populate(menuItem);
        cell.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                itemTouched((FilterMenuCell) view);
                if (itemMenuListener != null) {
                    itemMenuListener.onItemSelected(menuItem);
                }
            }
        });
        filtersView.addView(cell);
    }

    public void selectCellAtPosition(int position)
    {
        FilterMenuCell cell = (FilterMenuCell) filtersView.getChildAt(position);
        selectCell(cell);
    }

    public void selectCell(FilterMenuCell cell)
    {
        if (highlightedFilter != cell) {
            highlightCell(cell);
            if (filterMenuListener != null) {
                filterMenuListener.onItemSelected(cell.getCurrentFilterMenuModel());
            }
        }
    }

    public void highlightCell(FilterMenuCell cell)
    {
        if (highlightedFilter != cell) {
            if (menuTheme != FilterMenuTheme.LIGHT_THEME) {
                if (highlightedFilter != null) {
                    highlightedFilter.highlight(false);
                }
                cell.highlight(true);
            }
            highlightedFilter = cell;

            //moveRedHighlight();
        }
    }

    public void highlightCellAtPosition(int position)
    {
        FilterMenuCell cell = (FilterMenuCell) filtersView.getChildAt(position);
        highlightCell(cell);
    }

    public void moveRedHighlight()
    {
        requestLayout();
        int width = highlightedFilter.getMeasuredWidth();
        animation = new WidthAndXAnimation(redHighlight, width, highlightedFilter.getX());
        animation.setDuration(200);
        redHighlight.startAnimation(animation);
    }

    public void itemTouched(FilterMenuCell cell)
    {
        selectCell(cell);
    }

    public FilterMenuModel currentFilter()
    {
        FilterMenuModel filter = null;
        if (highlightedFilter != null) {
            filter = highlightedFilter.getCurrentFilterMenuModel();
        }

        return filter;
    }
}

class WidthAndXAnimation extends Animation
{
    private int mWidth;
    private int mStartWidth;
    private float mStartX;
    private float mEndX;
    private View mView;

    public WidthAndXAnimation(View view, int width, float x)
    {
        mView = view;
        mWidth = width;
        mEndX = x;
        mStartWidth = view.getMeasuredWidth();
        mStartX = view.getX();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);
        float newX = mStartX + (mEndX - mStartX) * interpolatedTime;

        mView.getLayoutParams().width = newWidth;
        mView.setX(newX);
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}
