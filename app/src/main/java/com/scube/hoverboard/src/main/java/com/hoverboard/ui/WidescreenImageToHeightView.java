package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jschroeder on 2/26/14.
 */
public class WidescreenImageToHeightView extends ImageView
{
    public Integer maxHeight;

    public WidescreenImageToHeightView(Context context)
    {
        super(context);
    }

    public WidescreenImageToHeightView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WidescreenImageToHeightView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = maxHeight == null ? getMeasuredHeight() : Math.min(maxHeight, getMeasuredHeight());
        setMeasuredDimension(Math.round(height * 16 / 9), height);
    }
}
