package com.scube.hoverboard.src.main.java.com.hoverboard.util;

import android.view.View;

/**
 * Created by jschroeder on 10/28/13.
 */
public class GestureHelper
{
    public static void addInterceptOnTouchListener(View v)
    {
        v.setOnTouchListener(new InterceptHorizontalOnTouchListener());
    }
}
