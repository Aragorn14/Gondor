package com.scube.hoverboard.src.main.java.com.hoverboard.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jschroeder on 10/28/13.
 */
public class InterceptHorizontalOnTouchListener implements View.OnTouchListener
{
    private float downX, downY;

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
            }
            case MotionEvent.ACTION_MOVE: {
                checkIntercept(view, event);
            }
            case MotionEvent.ACTION_UP: {
                checkIntercept(view, event);
            }
        }

        return false;
    }

    private void checkIntercept(View view, MotionEvent event)
    {
        float deltaX = downX - event.getX();
        float deltaY = downY - event.getY();

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            ((ViewGroup) view).requestDisallowInterceptTouchEvent(true);
        }
    }
}
