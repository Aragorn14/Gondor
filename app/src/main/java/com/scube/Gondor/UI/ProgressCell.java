package com.scube.Gondor.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.scube.Gondor.R;

/**
 * Created by jschroeder on 10/31/13.
 */
public class ProgressCell extends RelativeLayout
{
    private ProgressBar progressBar;

    public ProgressCell(Context context)
    {
        super(context);

        init(context);
    }

    public ProgressCell(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init(context);
    }

    public ProgressCell(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.progress_cell, this, true);

        progressBar = (ProgressBar) findViewById(R.id.progressCellProgressBar);
    }

    public void hide()
    {
        progressBar.setVisibility(INVISIBLE);
    }

    public void show()
    {
        progressBar.setVisibility(VISIBLE);
    }
}
