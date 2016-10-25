package com.scube.Gondor.Home.controllers.fragments.scubitFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by vashoka on 6/27/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public View targetView;
    public static DatePickerFragment newInstance(View target) {

        // Add tag for accessibility
        target.setTag("DatePickerFragment");

        DatePickerFragment dateFrag = new DatePickerFragment();
        dateFrag.setTargetView(target);

        return  dateFrag;
    }

    public void setTargetView(View target) {
        targetView = target;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        EditText editTarget = (EditText) targetView;
        int monthIndex = (month > 0) ? month-1 : month;
        String monthName = new DateFormatSymbols().getMonths()[monthIndex];
        editTarget.setText(monthName+" "+day+" "+year);
    }

    public static Integer convertMonthToInt(String month) {
        int monthInt = 0;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("MMM").parse("July"));
            monthInt = cal.get(Calendar.MONTH) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return monthInt;
    }
}
