package com.bigranch.android.criminalintent;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class OkCancelFragment extends DialogFragment {
    public static final String EXTRA_OKC_QUESTION = "com.bignerdranch.android.criminalintent.OkCancel.question";
    public static final String EXTRA_OKC_OK = "com.bignerdranch.android.criminalintent.OkCancel.ok";
    public static final String EXTRA_OKC_CANCEL = "com.bignerdranch.android.criminalintent.OkCancel.cancel";

    private String mQuestion;
    private String mOK;
    private String mCancel;

    public OkCancelFragment() {
        // Required empty public constructor
    }

    public static OkCancelFragment newInstance(String question, String ok, String cancel) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_OKC_QUESTION, question);
        args.putSerializable(EXTRA_OKC_OK, ok);
        args.putSerializable(EXTRA_OKC_CANCEL, cancel);
        OkCancelFragment fragment = new OkCancelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        //i.putExtra(EXTRA_DTB_DATE, mDate); //ToDo: remove unneeded intent?
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_ok_cancel, null);
        mQuestion = (String)getArguments().getSerializable(EXTRA_OKC_QUESTION);
        mOK = (String)getArguments().getSerializable(EXTRA_OKC_OK);
        mCancel = (String)getArguments().getSerializable(EXTRA_OKC_CANCEL);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(mQuestion)
                .setPositiveButton(mOK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .setNegativeButton(mCancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_CANCELED);
                            }
                        })
                .create();
    }

}
