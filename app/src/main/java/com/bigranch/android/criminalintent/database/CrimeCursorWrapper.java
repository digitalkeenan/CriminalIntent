package com.bigranch.android.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bigranch.android.criminalintent.Crime;
import com.bigranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by obrien on 9/26/2017.
 */

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int requiresPolice = getInt(getColumnIndex(CrimeTable.Cols.REQUIRES_POLICE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String suspectID = getString(getColumnIndex(CrimeTable.Cols.SUSPECT_ID));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setRequiresPolice(requiresPolice != 0);
        crime.setSuspect(suspect);
        crime.setSuspectID(suspectID);
        return crime;
    }
}
