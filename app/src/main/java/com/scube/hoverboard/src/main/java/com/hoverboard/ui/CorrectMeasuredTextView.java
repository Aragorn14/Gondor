package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jschroeder on 12/16/14.
 */
public class CorrectMeasuredTextView extends TextView
{
    private int measuredOffset = 0;

    public int getMeasuredOffset()
    {
        return measuredOffset;
    }

    public void setMeasuredOffset(int measuredOffset)
    {
        this.measuredOffset = measuredOffset;
    }

    public CorrectMeasuredTextView(Context context)
    {
        super(context);
    }

    public CorrectMeasuredTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CorrectMeasuredTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + measuredOffset);
    }
}
