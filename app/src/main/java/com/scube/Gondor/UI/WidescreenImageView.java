package com.scube.Gondor.UI;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by srikanthsridhara on 8/1/15.
 */
public class WidescreenImageView extends ImageView {


    public WidescreenImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if(d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (9 * width) / 16;

            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
