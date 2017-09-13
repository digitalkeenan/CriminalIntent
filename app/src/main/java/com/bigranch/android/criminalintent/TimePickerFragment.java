package com.bigranch.android.criminalintent;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_HOUROFDAY = "com.bignerdranch.android.criminalintent.hourofday";
    public static final String EXTRA_MINUTE = "com.bignerdranch.android.criminalintent.minute";
    private int mHourOfDay;
    private int mMinute;

    public static TimePickerFragment newInstance(int hourOfDay, int minute) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_HOUROFDAY, hourOfDay);
        args.putSerializable(EXTRA_MINUTE, minute);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mHourOfDay = (int)getArguments().getSerializable(EXTRA_HOUROFDAY);
        mMinute = (int)getArguments().getSerializable(EXTRA_MINUTE);

        // Create a new instance of TimePickerDialog and return it
        // ToDo: Why not "retain" TimePickerFragment instead? Is bug mentioned on BigNerdRanch page 220 fixed?
        TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(), this, mHourOfDay, mMinute, DateFormat.is24HourFormat(getActivity()));
        mTimePickerDialog.setTitle(R.string.time_picker_title);
        return mTimePickerDialog;
    }

    @Override
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
    }

}
