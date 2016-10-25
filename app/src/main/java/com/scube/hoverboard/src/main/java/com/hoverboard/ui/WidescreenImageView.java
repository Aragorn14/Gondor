package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jschroeder on 1/30/14.
 */
public class WidescreenImageView extends ImageView
{
    public WidescreenImageView(Context context)
    {
        super(context);
    }

    public WidescreenImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WidescreenImageView(Context context, AttributeSet attrs, int defStyle)
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
