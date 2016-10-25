package com.scube.Gondor.Core.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.scube.Gondor.R;

/**
 * Created by vashoka on 05/30/15.
 */
public class GhostView extends LinearLayout
{
    public GhostView(Context context)
    {
        super(context);
    }

    public GhostView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GhostView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ghost_view, this, true);
    }
}
