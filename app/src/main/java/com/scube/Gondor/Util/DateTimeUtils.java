package com.scube.Gondor.Util;

import java.util.Date;

/**
 * Created by igorkhomenko on 1/13/15.
 */
public class DateTimeUtils {
    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    private DateTimeUtils() {
    }

    /**
     * converts time (in milliseconds) to human-readable format
     *  "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
        if(duration > 0) {
            duration = new Date().getTime() - duration;
        }
        if(duration < 0){
            duration = 0;
        }

        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" day").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
            }

            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" and ");
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            res.append(" ago");
            return res.toString();
        } else {
            return "0 second ago";
        }
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public static long[] dateDifferenceArray(Date startDate, Date endDate){

        //milliseconds
        long difference = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long days = difference / daysInMilli;
        difference = difference % daysInMilli;

        long hours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        long minutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        long seconds = difference / secondsInMilli;

        long[] diffDate = new long[4];
        diffDate[0] = days;
        diffDate[1] = hours;
        diffDate[2] = minutes;
        diffDate[3] = seconds;

        return diffDate;
    }

    public static long dateDifferenceMilli(Date startDate, Date endDate) {

        // milliseconds
        long difference = endDate.getTime() - startDate.getTime();

        return difference;
    }

    public static String getTimeString(long inputTime) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long days = inputTime / daysInMilli;
        inputTime = inputTime % daysInMilli;

        long hours = inputTime / hoursInMilli;
        inputTime = inputTime % hoursInMilli;

        long minutes = inputTime / minutesInMilli;
        inputTime = inputTime % minutesInMilli;

        String result = "";
        if(days > 0) {
            result += days+" days";
        }
        result += " "+hours+":";
        result += minutes;

        // Format : X days 2:25
        return result;
    }
}
