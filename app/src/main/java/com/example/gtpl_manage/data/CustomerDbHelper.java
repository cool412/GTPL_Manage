package com.example.gtpl_manage.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.gtpl_manage.data.CustomerContract.CustomerEntry;

public class CustomerDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CustomerDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "gtpl.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link CustomerDbHelper}.
     *
     * @param context of the app
     */

    public CustomerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_CUSTOMERS_TABLE =  "CREATE TABLE " + CustomerEntry.TABLE_NAME + " ("
                + CustomerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CustomerEntry.COLUMN_CUSTOMER_NAME + " TEXT NOT NULL, "
                + CustomerEntry.COLUMN_CUSTOMER_ACCOUNT + " TEXT , "
                + CustomerEntry.COLUMN_CUSTOMER_MOBILE + " TEXT , "
                + CustomerEntry.COLUMN_CUSTOMER_TOTAL + " INTEGER );";

        String SQL_CREATE_CUSTOMER_DEBIT = "CREATE TABLE " + CustomerEntry.TABLE_DEBIT + "("
                + CustomerEntry.D_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CustomerEntry.CUSTOMER_ID + " INTEGER , "
                + CustomerEntry.ADD_DEBIT + " INTEGER , "
                + CustomerEntry.DEBIT_DATE + " TEXT , "
                + CustomerEntry.DEBIT_RECEIPT + " TEXT );";

        String SQL_CREATE_CUSTOMER_CREDIT = "CREATE TABLE " + CustomerEntry.TABLE_CREDIT + "("
                + CustomerEntry.D_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CustomerEntry.CUSTOMER_ID + " INTEGER , "
                + CustomerEntry.ADD_CREDIT + " INTEGER , "
                + CustomerEntry.DEBIT_DATE + " TEXT , "
                + CustomerEntry.DEBIT_RECEIPT + " TEXT );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CUSTOMERS_TABLE);
        db.execSQL(SQL_CREATE_CUSTOMER_DEBIT);
        db.execSQL(SQL_CREATE_CUSTOMER_CREDIT);
    }
    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("DROP TABLE IF EXISTS "+ CustomerEntry.TABLE_NAME);
       // db.execSQL("DROP TABLE IF EXISTS "+ CustomerEntry.TABLE_DEBIT);
       // onCreate(db);
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
