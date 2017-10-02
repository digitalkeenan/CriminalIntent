package com.bigranch.android.criminalintent;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_CONFIRM = "confirm";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONFIRM = 1;
    private static final int REQUEST_CONTACT = 2;

    //ToDo: remove this unused?
    UUID crimeId;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateTimeButton;
    private CheckBox mRequiresPoliceCheckBox;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private Button mFirstCrimeButton;
    private Button mLastCrimeButton;
    private Button mSuspectButton;
    private ImageButton mCallSuspectButton;
    private Button mReportButton;

    public CrimeFragment() {
        // Required empty public constructor
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment fragment = new CrimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        fragment.setArguments(args);
        return fragment;
    }

    private void updateDateTime() {
        mDateTimeButton.setText(DateFormat.format("EEE, yyyy-MM-dd h:mm a", mCrime.getDate()));
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
            mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        mTitleField = (EditText)v.findViewById(R.id.fcrime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void afterTextChanged(Editable c) {
                // This one too
            }
        });

        mDateTimeButton = (Button)v.findViewById(R.id.crime_date_time);
        updateDateTime(); //calls mDateTimeButton.setText
        mDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager(); //ToDo: book uses regular getFragmentManager
                DateTimeButtonsFragment dialog = DateTimeButtonsFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mRequiresPoliceCheckBox = (CheckBox) v.findViewById(R.id.crime_requires_police);
        mRequiresPoliceCheckBox.setChecked(mCrime.requiresPolice());
        mRequiresPoliceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the crime's requires police property
                mCrime.setRequiresPolice(isChecked);
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

        mFirstCrimeButton = (Button)v.findViewById(R.id.first_crime_button);
        if (((CrimePagerActivity)getActivity()).currentCrimeNum(mCrime.getId()) == 0) {
            mFirstCrimeButton.setVisibility(View.GONE);
        } else {
            mFirstCrimeButton.setVisibility(View.VISIBLE);
        }
        mFirstCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ((CrimePagerActivity)getActivity()).gotoFirstPage();
            }
        });

        mLastCrimeButton = (Button)v.findViewById(R.id.last_crime_button);
        if (((CrimePagerActivity)getActivity()).currentCrimeIsLast(mCrime.getId())) {
            mLastCrimeButton.setVisibility(View.GONE);
        } else {
            mLastCrimeButton.setVisibility(View.VISIBLE);
        }
        mLastCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ((CrimePagerActivity)getActivity()).gotoLastPage();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        // the following code breaks the contact search to test that the button will be disabled
        //pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mCallSuspectButton = (ImageButton) v.findViewById(R.id.call_suspect_button);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivityForResult(pickContact, REQUEST_CONTACT);
                String number = "";
                ContentResolver contentResolver = getActivity().getContentResolver();
                String contactFinder = ContactsContract.CommonDataKinds.Phone._ID + " = " + mCrime.getSuspectID();
                Cursor c = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, contactFinder, null, null);

                if(c.getCount() > 0)
                {
                    while(c.moveToNext())
                    {
                        number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    Uri uriNumber = Uri.parse("tel:" + number);
                    final Intent i = new Intent(Intent.ACTION_DIAL, uriNumber);
                    startActivity(i);
                }

                c.close();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));*/
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setChooserTitle(R.string.send_report)
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .getIntent();
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AppCompatActivity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DateTimeButtonsFragment.EXTRA_DTB_DATE);
            mCrime.setDate(date);
            updateDateTime();
        } else if (requestCode == REQUEST_CONFIRM) {
            CrimeLab.get(getActivity()).deleteCrime(mCrime);
            exitThisView();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify for which fields you want your query to return values
            String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where" clause here
            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor c = contentResolver.query(contactUri, queryFields, null, null, null);
            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }
                // Pull out the first column of the first row of data - that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
            String[] projection1 = {ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER};
            c = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection1, null, null, null);//ContactsContract.Contacts.DISPLAY_NAME);
            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }
                // Pull out the first column of the first row of data - that is your suspect's name
                c.moveToFirst();
                String contactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                mCrime.setSuspectID(contactID);
            } finally {
                c.close();
            }
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
                FragmentManager fm = getActivity().getSupportFragmentManager();
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
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
}
