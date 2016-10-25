package com.scube.hoverboard.src.main.java.com.hoverboard.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by jschroeder on 11/11/13.
 */
public class ViewHelper
{
    public static void flowText(String text, View thumbnailView, TextView messageView, int margin)
    {
        // Get height and width of the image and height of the text line
        //TODO: This is designed for an image in the top left.  Add params to make more robust.
        if (messageView != null && thumbnailView != null) {
            int width = thumbnailView.getWidth() + margin;
            int height = thumbnailView.getHeight() + margin;

            int padding = messageView.getTotalPaddingTop();
            float textLineHeight = messageView.getPaint().getTextSize();

            try {
                // Set the span according to the number of lines and width of the image
                int lines = (int) Math.round((height - padding) / textLineHeight) - 2;
                SpannableStringBuilder ss = (SpannableStringBuilder) Html.fromHtml(text);
                ss.setSpan(new WrapMarginSpan2(lines, width), 0, lines, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                messageView.setText(ss);

                // Align the text with the image by removing the rule that the text is to the right of the image
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageView.getLayoutParams();
                int[] rules = params.getRules();
                rules[RelativeLayout.RIGHT_OF] = 0;
            } catch (Exception e) {
                e.printStackTrace();
                messageView.setText(text);
            }
        }
    }

    public static int dpToPx(int dp, Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return px;
    }

    public static int pxToDp(int px, Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return dp;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    public static Point getScreenSize(FragmentActivity fragmentActivity)
    {
        Display display = fragmentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    public static int getScreenWidth(Context context)
    {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context)
    {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void animateAlpha(final View view, long duration, final float startAlpha, final float endAlpha)
    {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 1.0f);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    }

    public static float middleX(View view)
    {
        return view.getX() + view.getMeasuredWidth() / 2;
    }

    public static void removeFromParentView(View view)
    {
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static void disableClipOnParents(View v)
    {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(false);
            ((ViewGroup) v).setClipToPadding(false);
        }

        if (v.getParent() instanceof View) {
            disableClipOnParents((View) v.getParent());
        }
    }

    public static boolean isLandscape(int orientation)
    {
        return orientation == ActivityInfo.SCREEN_ORIENTATION_USER ||
                orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE ||
                orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    public static boolean isPortrait(int orientation)
    {
        return orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener)
    {
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}
