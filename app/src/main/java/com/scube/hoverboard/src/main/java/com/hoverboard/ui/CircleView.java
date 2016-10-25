package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jschroeder on 1/13/15.
 */
public class CircleView extends View
{
    public int radius = 20;
    public int color;
    private Paint paint = new Paint();

    public CircleView(int color, Context context)
    {
        super(context);

        this.color = color;
        paint.setStyle(Paint.Style.FILL);
    }

    public CircleView(Context context)
    {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        paint.setColor(color);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
    }
}
