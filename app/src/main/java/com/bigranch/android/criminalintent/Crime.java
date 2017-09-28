package com.bigranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by obrien on 2/6/2016.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
        mSolved = false; //ToDo: this is not in the book?
        mRequiresPolice = false;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() { return mTitle; }
    /*@Override
    public String toString() { return mTitle; }*/

    public void setTitle(String title) { mTitle = title; }

    public Date getDate() { return mDate; }

    public void setDate(Date date) { mDate = date; }

    public boolean isSolved() { return mSolved; }

    public void setSolved(boolean solved) { mSolved = solved; }

    public boolean requiresPolice() { return mRequiresPolice; }

    public void setRequiresPolice(boolean doesRequirePolice) { mRequiresPolice = doesRequirePolice; }
}
