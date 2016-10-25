package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by jschroeder on 10/13/14.
 */
public class WidescreenView extends FrameLayout
{
    public WidescreenView(Context context)
    {
        super(context);
    }

    public WidescreenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WidescreenView(Context context, AttributeSet attrs, int defStyle)
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
