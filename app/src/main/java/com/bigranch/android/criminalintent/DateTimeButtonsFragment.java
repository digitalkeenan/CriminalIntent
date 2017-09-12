package com.bigranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeButtonsFragment extends DialogFragment {
    public static final String EXTRA_DTB_DATE = "com.bignerdranch.android.criminalintent.DTButtons.date";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Date mDate;
    private Button mDateButton;
    private Button mTimeButton;

    public static DateTimeButtonsFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DTB_DATE, date);
        DateTimeButtonsFragment fragment = new DateTimeButtonsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateDate() {
        DateFormat mDateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        mDateButton.setText(mDateFormat.format(mDate));
    }

    private void updateTime() {
        DateFormat mDateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        mTimeButton.setText(mDateFormat.format(mDate));
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DTB_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_time_buttons, null);
        mDate = (Date)getArguments().getSerializable(EXTRA_DTB_DATE);

        mDateButton = (Button)v.findViewById(R.id.date_time_buttons_date);
        updateDate(); //calls mDateButton.setText
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mDate);
                dialog.setTargetFragment(DateTimeButtonsFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton = (Button)v.findViewById(R.id.date_time_buttons_time);
        updateTime(); //calls mTimeButton.setText
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mDate);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerFragment dialog = TimePickerFragment.newInstance(hour, minute);
                dialog.setTargetFragment(DateTimeButtonsFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_time_buttons_title) //ToDo: change to use input string to keep fragment for general use
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        Calendar calendarOld = Calendar.getInstance();
        calendarOld.setTime(mDate);
        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Calendar calendarNew = Calendar.getInstance();
                calendarNew.setTime(date);
                mDate = new GregorianCalendar(
                        calendarNew.get(Calendar.YEAR),
                        calendarNew.get(Calendar.MONTH),
                        calendarNew.get(Calendar.DAY_OF_MONTH),
                        calendarOld.get(Calendar.HOUR_OF_DAY),
                        calendarOld.get(Calendar.MINUTE)
                ).getTime();
                updateDate();
                break;
            case REQUEST_TIME:
                mDate = new GregorianCalendar(
                        calendarOld.get(Calendar.YEAR),
                        calendarOld.get(Calendar.MONTH),
                        calendarOld.get(Calendar.DAY_OF_MONTH),
                        (int)data.getSerializableExtra(TimePickerFragment.EXTRA_HOUROFDAY),
                        (int)data.getSerializableExtra(TimePickerFragment.EXTRA_MINUTE)
                ).getTime();
                updateTime();
                break;
        }
    }
}
