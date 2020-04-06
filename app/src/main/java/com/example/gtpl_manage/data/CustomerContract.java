package com.example.gtpl_manage.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class CustomerContract {

    // To avoid any additional instantiation crate an empty constructor
    private CustomerContract(){}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.gtpl_manage";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.gtpl_customer/customers/ is a valid path for
     * looking at pet data. content://com.example.android.gtpl_customer/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_CUSTOMER = "gtpl_manage";


    // test record path.
    public static final String PATH_RECORD = "gtpl_record";

    /**
     * Inner class that defines constant values for the customer database table.
     * Each entry in the table represents a single customer.
     */
    public static final class CustomerEntry implements BaseColumns{
        /** The content URI to access the customer data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CUSTOMER);

        // The contect URI to access the record data in the provider.
        public static final  Uri RECORD_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECORD);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUSTOMER;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUSTOMER;

        /** Name of database table for customer */
        public final static String TABLE_NAME = "customers";

        /* Name of the database table for debit */
        public final static String TABLE_DEBIT = "debit_record";

        /* Name of the database table for credit */
        public final static String TABLE_CREDIT = "credit_record";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Customer.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CUSTOMER_NAME ="name";

        public final static String COLUMN_CUSTOMER_ACCOUNT = "account";

        public final static String COLUMN_CUSTOMER_MOBILE = "mobile";

        public final static String COLUMN_CUSTOMER_TOTAL = "total";

        public final static String DESCREPTION = "description";

        public final static int DEFAULT_TOTAL = 0;

        /* Debit table variables*/
        public final static String D_ID = "id";
        public final static String CUSTOMER_ID = "customer_id";
        public final static String ADD_DEBIT = "debit";
        public final static String ADD_CREDIT = "credit";
        public final static String DEBIT_RECEIPT = "receipt";
        public final static String DEBIT_DATE = "date";

        // Declare value for credit and debit.
        public final static int SELECT_UNKNOWN = 0;
        public final static int SELECT_DEBIT = 1;
        public final static int SELECT_CREDIT = 2;



        public static boolean isValidaccount(String account_number){
            for (int i = 0; i < account_number.length(); i++)
                if (Character.isDigit(account_number.charAt(i))
                        == false)
                    return false;
            return true;
        }
        public static boolean isValidMobile(String mobileNumber){
            if (isValidaccount(mobileNumber) && mobileNumber.length() == 10)
                return true;
            return false;

        }
    }
}
