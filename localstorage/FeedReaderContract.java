package kevintn.uw.tacoma.edu.webserviceslab.localstorage;

import android.provider.BaseColumns;

/**
 * Class holds values for table name and column names.
 *
 * This class originally came from Android studio online here:
 * https://developer.android.com/training/basics/data-storage/databases.html
 *
 */
public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Likes";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_TITLE = "title";
    }
}
