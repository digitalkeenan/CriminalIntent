package com.bigranch.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by obrien on 2/6/2016.
 */
public class Crime {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_REQUIRES_POLICE = "requiresPolice";

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public Crime() {
        // Generate unique identifier
        mId = UUID.randomUUID();
        mDate = new Date();
        mSolved = false;
        mRequiresPolice = false;
    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) {
            mTitle = json.getString(JSON_TITLE);
        }
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
        mRequiresPolice = json.getBoolean(JSON_REQUIRES_POLICE);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_DATE, mDate.getTime());
        json.put(JSON_REQUIRES_POLICE, mRequiresPolice);
        return json;
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
