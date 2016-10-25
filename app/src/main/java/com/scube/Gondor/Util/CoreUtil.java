package com.scube.Gondor.Util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by srikanthsridhara on 7/25/15.
 */
public class CoreUtil {

    public static float convertDpToPixel(int dpValue, DisplayMetrics displayMetrics) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, displayMetrics);
    }

}
