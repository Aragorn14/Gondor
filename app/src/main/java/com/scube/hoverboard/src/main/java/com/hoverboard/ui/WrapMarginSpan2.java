package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * Created by jschroeder on 11/11/13.
 */
public class WrapMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2
{
    private int margin;
    private int lines;

    public WrapMarginSpan2(int lines, int margin)
    {
        this.lines = lines;
        this.margin = margin;
    }

    @Override
    public int getLeadingMarginLineCount()
    {
        return lines;
    }

    @Override
    public int getLeadingMargin(boolean first)
    {
        return first ? margin : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout)
    {

    }
}
