package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scube.Gondor.R;
import com.scube.Gondor.Util.FontUtil;

/**
 * Created by vashoka on 6/6/15.
 */
public class TooltipNotificationView extends RelativeLayout
{
    protected TextView textView;
    protected ImageView imageView;

    public TooltipNotificationView(Context context)
    {
        super(context);

        init(context);
    }

    public TooltipNotificationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init(context);
    }

    public TooltipNotificationView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notification_tooltip, this, true);

        textView = ((TextView) findViewById(R.id.textView));
        textView.setTypeface(FontUtil.getARSMaquetteFont());

        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setText(String text)
    {
        textView.setText(Html.fromHtml(text));
    }

    public void setFontSize(float size)
    {
        textView.setTextSize(size);
    }

    public void setImageResource(int id)
    {
        imageView.setImageResource(id);
    }

    public void setImageAlpha(float alpha)
    {
        imageView.setAlpha(alpha);
    }
}
