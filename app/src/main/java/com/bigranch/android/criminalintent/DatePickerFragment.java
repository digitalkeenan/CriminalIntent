package com.bigranch.android.criminalintent;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
    private Date mDate;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        // ToDo: Why not "retain" DatePickerFragment instead? Is bug mentioned on BigNerdRanch page 220 fixed?
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        // ToDo: Debug: if I try to set the view onDateSet gets called with unchanged date
        // ToDo:  Clue: with the view.isShown added on onDateSet, it isn't true either time onDateSet is called
        //mDatePickerDialog.setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null));
        mDatePickerDialog.setTitle(R.string.date_picker_title);
        return mDatePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (view.isShown()) { //Unknown why OnDateSet gets called twice, but this only is true the first time
            // Translate year, month, day into a Date object using a calendar
            mDate = new GregorianCalendar(year, month, day).getTime();
            // Update argument to preserve selected value on rotation
            getArguments().putSerializable(EXTRA_DATE, mDate);

            Intent i = new Intent();
            i.putExtra(EXTRA_DATE, mDate);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    }
}
