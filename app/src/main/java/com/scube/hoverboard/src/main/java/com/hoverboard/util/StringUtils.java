package com.scube.hoverboard.src.main.java.com.hoverboard.util;

/**
 * Created by fvelazquez on 4/16/15.
 */
public class StringUtils {

    public static String itemCountWithSufix(double count)
    {
        String totalDisplayString = "";
        if (count > 0 && count < 1000) {
            totalDisplayString = "" + count;
        } else if (count > 1000) {
            int exp = (int) (Math.log(count) / Math.log(1000));
            String formatString = count > 1000000 ? "%.1f %c" : "%.0f %c";
            totalDisplayString = String.format(formatString,
                    count / Math.pow(1000, exp),
                    "KMBTQ".charAt(exp - 1));
        }

        return totalDisplayString;
    }
}
