package com.scube.Gondor.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.scube.Gondor.R;

/**
 * Created by srikanthsridhara on 6/12/15.
 */
public class GlobalUtils {

    public static void addSharedPreference (Context context, String sp, String spName, String spValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(spName, spValue);
        editor.apply();
    }

    public static void addIntSharedPreference (Context context, String sp, String spName, Integer spValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(spName, spValue);
        editor.apply();
    }

    public static void removeSharedPreference (Context context, String sp, String spName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(spName);
        editor.apply();
    }

    public static String getSharedPreference (Context context, String sp, String spName) {
        return (context.getSharedPreferences(sp, Context.MODE_PRIVATE).getString(spName, ""));
    }

    public static Integer getIntSharedPreference (Context context, String sp, String spName) {
        return (context.getSharedPreferences(sp, Context.MODE_PRIVATE).getInt(spName, 0));
    }

    public static Boolean containsSharedPreference (Context context, String sp, String spName) {
        return (context.getSharedPreferences(sp, Context.MODE_PRIVATE).contains(spName));
    }

    public static String getDomain (Context context) {
        String domain = context.getString(R.string.openshift_prod_domain);
        if(containsSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_domain))) {
            domain = getSharedPreference(context, context.getString(R.string.sp_user), context.getString(R.string.user_domain));
        }

        return domain;
    }
}
