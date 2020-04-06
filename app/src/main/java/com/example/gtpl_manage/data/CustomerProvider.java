package com.example.gtpl_manage.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.gtpl_manage.data.CustomerContract.CustomerEntry;


/**
 * {@link ContentProvider} for Customer app.
 */
public class CustomerProvider extends ContentProvider{

    /** Tag for the log messages */
    public static final String LOG_TAG = ContentProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the customers table */
    private static final int CUSTOMERS = 100;

    /** URI matcher code for the content URI for a single pet in the customers table */
    private static final int CUSTOMER_ID = 101;

    // URI mather code fro the record URI.
    private static final int RECORD = 111;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(CustomerContract.CONTENT_AUTHORITY, CustomerContract.PATH_CUSTOMER, CUSTOMERS);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(CustomerContract.CONTENT_AUTHORITY, CustomerContract.PATH_CUSTOMER + "/#", CUSTOMER_ID);

        //
        sUriMatcher.addURI(CustomerContract.CONTENT_AUTHORITY, CustomerContract.PATH_RECORD, RECORD);
    }

    /** Database helper object */
    private CustomerDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new CustomerDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CUSTOMERS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(CustomerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CUSTOMER_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = CustomerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(CustomerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case RECORD:
                cursor = database.query(CustomerEntry.TABLE_DEBIT,projection,selection,selectionArgs,null,null,sortOrder);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Set Notification URI on Cursor.
        //So we know what content URI the cursor was created so far.
        // If the data in URi changes then we know we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the cursor.
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CUSTOMERS:
                return CustomerEntry.CONTENT_LIST_TYPE;
            case CUSTOMER_ID:
                return CustomerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CUSTOMERS:
                return insertCustomer(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertCustomer(Uri uri, ContentValues values){
        // Check that the name is not null
        String name = values.getAsString(CustomerEntry.COLUMN_CUSTOMER_NAME);
        if (name == null){
            throw new IllegalArgumentException("Customer Name requires a name");
        }
        // Check that the gender is valid
        String account = values.getAsString(CustomerEntry.COLUMN_CUSTOMER_ACCOUNT);
        if (account == null || !CustomerEntry.isValidaccount(account)){
            throw new IllegalArgumentException("Customer requires valid account number");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        String mobile = values.getAsString(CustomerEntry.COLUMN_CUSTOMER_MOBILE);
        if (mobile == null || !CustomerEntry.isValidMobile(mobile)){
            throw new IllegalArgumentException("Customer requires valid mobile number");
        }

        int total = values.getAsInteger(CustomerEntry.COLUMN_CUSTOMER_TOTAL);
        // No need to check the breed, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(CustomerEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all the listeners that data in pet has changed.
        getContext().getContentResolver().notifyChange( uri,null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CUSTOMERS:
                // Delete all rows that match the selection and selection args
                int rowsdeleted = database.delete(CustomerEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsdeleted != 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                // returns the number of rows deleted.
                return rowsdeleted;
            case CUSTOMER_ID:
                // Delete a single row given by the ID in the URI
                selection = CustomerEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                int rowsDeleted = database.delete(CustomerEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                // returns the number of rows deleted.
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CUSTOMERS:
                return updateCustomer(uri, contentValues, selection, selectionArgs);
            case CUSTOMER_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CustomerEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCustomer(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateCustomer(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link CustomerEntry#COLUMN_CUSTOMER_NAME} key is present,
        // check that the name value is not null.

        if (values.containsKey(CustomerEntry.COLUMN_CUSTOMER_NAME)) {
            String name = values.getAsString(CustomerEntry.COLUMN_CUSTOMER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Customer Name requires a name");
            }
        }

        // If the {@link CustomerEntry#COLUMN_CUSTOMER_ACCOUNT} key is present,
        // check that the account number value is valid.
        if (values.containsKey(CustomerEntry.COLUMN_CUSTOMER_ACCOUNT)) {
            String account = values.getAsString(CustomerEntry.COLUMN_CUSTOMER_ACCOUNT);
            if (account == null || !CustomerEntry.isValidaccount(account)) {
                throw new IllegalArgumentException("Customer requires valid account number");
            }
        }

        // If the {@link CustomerEntry#COLUMN_CUSTOMER_MOBILE} key is present,
        // check that the weight value is valid.
        if (values.containsKey(CustomerEntry.COLUMN_CUSTOMER_MOBILE)) {
            // Check that the weight is greater than or equal to 0 kg
            String mobile = values.getAsString(CustomerEntry.COLUMN_CUSTOMER_MOBILE);
            if (mobile == null || !CustomerEntry.isValidMobile(mobile)) {
                throw new IllegalArgumentException("Customer requires valid weight");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0 ) {
            return 0;
        }


            // Otherwise, get writeable database to update the data
            SQLiteDatabase database = mDbHelper.getWritableDatabase();


            // Get all the umber of rows updated.
            int rowupdated = database.update(CustomerEntry.TABLE_NAME, values, selection, selectionArgs);

            if (rowupdated != 0) {
                // If 1 or more rows were updated, then notify all listeners that the data at the
                // given URI has changed.
                getContext().getContentResolver().notifyChange(uri, null);
        }


        // Returns the number of database rows affected by the update statement
        return rowupdated;
        // return database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
