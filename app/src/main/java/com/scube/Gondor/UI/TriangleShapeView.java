package com.scube.Gondor.UI;

/**
 * Created by vashoka on 8/1/15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class TriangleShapeView extends View {

    private Integer bgColor = Color.GRAY;

    public TriangleShapeView(Context context) {
        super(context);
    }

    public TriangleShapeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TriangleShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBgColor(Integer bgColor) {
        this.bgColor = bgColor;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();

        Path path = new Path();
        path.moveTo( 0, 0);
        path.lineTo( w , 0);
        path.lineTo( w , w);
        path.lineTo( 0 , 0);
        path.close();

        Paint p = new Paint();
        p.setColor( this.bgColor );

        canvas.drawPath(path, p);
    }
}
