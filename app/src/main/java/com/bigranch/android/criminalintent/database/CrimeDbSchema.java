package com.bigranch.android.criminalintent.database;

/**
 * Created by obrien on 9/25/2017.
 */

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String REQUIRES_POLICE = "requiresPolice";
            public static final String SUSPECT = "suspect";
            public static final String SUSPECT_ID = "suspectID";
        }
    }
}
