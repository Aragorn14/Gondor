package com.scube.Gondor.Home.controllers.fragments.scubitFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vashoka on 6/27/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public View targetView;
    public static TimePickerFragment newInstance(View target) {

        // Add tag for accessibility
        target.setTag("TimePickerFragment");

        TimePickerFragment timeFrag = new TimePickerFragment();
        timeFrag.setTargetView(target);

        return  timeFrag;
    }

    public void setTargetView(View target) {
        targetView = target;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        EditText editTarget = (EditText) targetView;
        editTarget.setText(convert24To12HourFormat(hourOfDay, minute));
    }

    public static String convert24To12HourFormat(int hour, int minute) {
        String _24HourTime = hour+":"+minute;

        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            return  _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return _24HourTime;
    }
}
