package com.bigranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment{ //Todo: remove this: implements TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_HOUROFDAY = "com.bignerdranch.android.criminalintent.hourofday";
    public static final String EXTRA_MINUTE = "com.bignerdranch.android.criminalintent.minute";
    public static final String ARG_HOUROFDAY = "hourofday";
    public static final String ARG_MINUTE = "minute";
    private TimePicker mTimePicker;
    private int mHourOfDay;
    private int mMinute;

    public static TimePickerFragment newInstance(int hourOfDay, int minute) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_HOUROFDAY, hourOfDay);
        args.putSerializable(ARG_MINUTE, minute);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mHourOfDay = (int)getArguments().getSerializable(ARG_HOUROFDAY);
        mMinute = (int)getArguments().getSerializable(ARG_MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker)v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentHour(mHourOfDay); //ToDo: for API 23 and above, change to setHour and setMinute
        mTimePicker.setCurrentMinute(mMinute);

        // Create a new instance of TimePickerDialog and return it
        // ToDo: Why not "retain" TimePickerFragment instead? Is bug mentioned on BigNerdRanch rev1 page 220 fixed?
        //TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(), this, mHourOfDay, mMinute, DateFormat.is24HourFormat(getActivity()));
        //mTimePickerDialog.setTitle(R.string.time_picker_title);
        //return mTimePickerDialog;

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void
                    onClick(DialogInterface dialog, int which) {
                        int hour = mTimePicker.getCurrentHour(); //ToDo: for API 23 and above, change to getHour and getMinute
                        int minute = mTimePicker.getCurrentMinute();
                        sendResult(Activity.RESULT_OK, hour, minute);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, int hour, int minute) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_HOUROFDAY, hour);
        intent.putExtra(EXTRA_MINUTE, minute);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    //ToDo: remove this old code
    /*@Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (view.isShown()) { //Unknown why OnDateSet gets called twice, but this only is true the first time
            mHourOfDay = hourOfDay;
            mMinute = minute;
            // Update arguments to preserve selected values on rotation
            getArguments().putSerializable(EXTRA_HOUROFDAY, mHourOfDay);
            getArguments().putSerializable(EXTRA_MINUTE, mMinute);

            Intent i = new Intent();
            i.putExtra(EXTRA_HOUROFDAY, mHourOfDay);
            i.putExtra(EXTRA_MINUTE, mMinute);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    }*/
}