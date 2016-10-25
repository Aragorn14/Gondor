package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by jschroeder on 12/16/14.
 */
public class WidescreenFrameLayout extends FrameLayout
{
    public WidescreenFrameLayout(Context context)
    {
        super(context);
    }

    public WidescreenFrameLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WidescreenFrameLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * 9 / 16);
    }
}
