package com.bigranch.android.criminalintent;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_CONFIRM = "confirm";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONFIRM = 1;

    //ToDo: remove this unused?
    UUID crimeId;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateTimeButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;

    //private OnFragmentInteractionListener mListener;

    public CrimeFragment() {
        // Required empty public constructor
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment fragment = new CrimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        fragment.setArguments(args);
        return fragment;
    }

    private void updateDateTime() {
        DateFormat mDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        mDateTimeButton.setText(mDateFormat.format(mCrime.getDate()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
            mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        if (NavUtils.getParentActivityName(getActivity()) != null) { // if no parent, don't display carrot
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setIcon(R.mipmap.ic_launcher);//ToDo: why doesn't this work?
            }
        }

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }
            public void afterTextChanged(Editable c) {
                // This one too
            }
        });

        mDateTimeButton = (Button)v.findViewById(R.id.crime_date_time);
        updateDateTime(); //calls mDateTimeButton.setText
        mDateTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                DateTimeButtonsFragment dialog = DateTimeButtonsFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivity(i);
            }
        });
        // If camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = (Camera.getNumberOfCameras() > 0);
        if (!hasACamera) {
            mPhotoButton.setEnabled(false);
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AppCompatActivity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            //Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            Date date = (Date)data.getSerializableExtra(DateTimeButtonsFragment.EXTRA_DTB_DATE);
            mCrime.setDate(date);
            updateDateTime();
        } else if (requestCode == REQUEST_CONFIRM) {
            CrimeLab.get(getActivity()).deleteCrime(mCrime);
            exitThisView();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    private void exitThisView() {
        if (NavUtils.getParentActivityName(getActivity()) != null) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                //Confirm with yes/no dialog
                FragmentManager fm = getActivity().getFragmentManager();
                OkCancelFragment dialog = OkCancelFragment.newInstance("Are you sure you want to delete this crime?", "Delete", "Cancel");
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CONFIRM);
                dialog.show(fm, DIALOG_CONFIRM);
                return true;
            case android.R.id.home:
                exitThisView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }


    /*        @Override
            public void onAttach(Context context) {
                super.onAttach(context);
                if (context instanceof OnFragmentInteractionListener) {
                    mListener = (OnFragmentInteractionListener) context;
                } else {
                    throw new RuntimeException(context.toString()
                            + " must implement OnFragmentInteractionListener");
                }
            }

            @Override
            public void onDetach() {
                super.onDetach();
                mListener = null;
            }*/

            /**
             * This interface must be implemented by activities that contain this
             * fragment to allow an interaction in this fragment to be communicated
             * to the activity and potentially other fragments contained in that
             * activity.
             * <p/>
             * See the Android Training lesson <a href=
             * "http://developer.android.com/training/basics/fragments/communicating.html"
             * >Communicating with Other Fragments</a> for more information.
             */
            /*public interface OnFragmentInteractionListener {
                // TODO: Update argument type and name
                void onFragmentInteraction(Uri uri);
            }*/

    }
