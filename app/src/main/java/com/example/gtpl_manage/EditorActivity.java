package com.example.gtpl_manage;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.gtpl_manage.data.CustomerContract.CustomerEntry;
import com.example.gtpl_manage.data.CustomerDbHelper;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the customer data loader
     */
    private static final int EXISTING_CUSTOMER_LOADER = 0;

    /**
     * Content URI for the existing customer (null if it's a new customer)
     */
    private Uri mCurrentCustomerUri;

    /**
     * EditText field to enter the customer's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the customer's account number
     */
    private EditText mAccountEditText;

    /**
     * EditText field to enter the customer's mobile number
     */
    private EditText mMobileEditText;

    // define total amount
    private int mCurrentTotal = 0;

    /**
     * Boolean flag that keeps track of whether the customer has been edited (true) or not (false)
     */
    private boolean mCustomerHasChanged = false;

    // Set up spinner for debit and credit.
    private Spinner mDebitCreditSpinner;

    //Declare credit debit variable.
    private int mCreditDebit = CustomerEntry.SELECT_UNKNOWN;

    //Declare Layout variables.
    private LinearLayout mSpinnerLayout;
    private LinearLayout mDateLayout;
    private LinearLayout mReceiptLayout;

    //Declare activity  is in edit mode.

    private boolean mActivityEditMode = false;

    // ID of cursor passed in its table

    private int mCurrentId;

    //declare

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mCustomerHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new customer or editing an existing one.
        Intent intent = getIntent();
        mCurrentCustomerUri = intent.getData();

        // If the intent DOES NOT contain a customer content URI, then we know that we are
        // creating a new customer.
        if (mCurrentCustomerUri == null) {
            // This is a new customer, so change the app bar to say "Add a customer"
            setTitle(getString(R.string.editor_activity_title_new_pet));
            mSpinnerLayout = (LinearLayout) findViewById(R.id.spinne_layout);
            mDateLayout = (LinearLayout) findViewById(R.id.date_layout);
            mReceiptLayout = (LinearLayout) findViewById(R.id.receipt_layout);
            mSpinnerLayout.setVisibility(View.GONE);
            mDateLayout.setVisibility(View.GONE);
            mReceiptLayout.setVisibility(View.GONE);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();

        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit customer"
            setTitle(getString(R.string.editor_activity_title_edit_pet));

            // Initialize a loader to read the customer data from the database
            // and display the current values in the editor
            // Kick off the loader.
            final LoaderManager supportLoaderManager = getSupportLoaderManager();
            supportLoaderManager.initLoader(EXISTING_CUSTOMER_LOADER, null, this);

            // getLoaderManager().initLoader(EXISTING_CUSTOMER_LOADER, null,  this);
            //  getLoaderManager().initLoader(EXISTING_CUSTOMER_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_customername);
        mAccountEditText = (EditText) findViewById(R.id.edit_account);
        mMobileEditText = (EditText) findViewById(R.id.edit_mobile);
        mDebitCreditSpinner = (Spinner) findViewById(R.id.select_entry);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mAccountEditText.setOnTouchListener(mTouchListener);
        mMobileEditText.setOnTouchListener(mTouchListener);

        setupSpinner();

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter creditdebitSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_credit_debit_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        creditdebitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mDebitCreditSpinner.setAdapter(creditdebitSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mDebitCreditSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.entry_credit))) {
                        mCreditDebit = CustomerEntry.SELECT_CREDIT;
                    } else if (selection.equals(getString(R.string.entry_debit))) {
                        mCreditDebit = CustomerEntry.SELECT_DEBIT;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCreditDebit = CustomerEntry.SELECT_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save pet into database.
     */
    private void saveCustomer() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String accountString = mAccountEditText.getText().toString().trim();
        String mobileString = mMobileEditText.getText().toString().trim();

        // Check if this is supposed to be a new customer.
        // and check if all the fields in the editor are blank
        if (mCurrentCustomerUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(accountString) &&
                TextUtils.isEmpty(mobileString)) {
            // Since no fields were modified, we can return early without creating a new customer.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and customer attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_CUSTOMER_NAME, nameString);
        values.put(CustomerEntry.COLUMN_CUSTOMER_ACCOUNT, accountString);
        values.put(CustomerEntry.COLUMN_CUSTOMER_MOBILE, mobileString);
        values.put(CustomerEntry.COLUMN_CUSTOMER_TOTAL, 0);

        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentCustomerUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new customer.

            Uri newUri = getContentResolver().insert(CustomerEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!mActivityEditMode) {
                // Take the inputs from user about debit and credit.
                EditText amount_edit = (EditText) findViewById(R.id.amount_edit);
                EditText date = (EditText) findViewById(R.id.date_edit);
                EditText receipt = (EditText) findViewById(R.id.receipt_edit);

                int debit_credit = Integer.parseInt(amount_edit.getText().toString().trim());
                String mDate = date.getText().toString().trim();
                String mReceipt = receipt.getText().toString().trim();




                ContentValues debit_credit_values = new ContentValues();
                debit_credit_values.put(CustomerEntry.DEBIT_DATE, mDate);
                debit_credit_values.put(CustomerEntry.DEBIT_RECEIPT, mReceipt);
                debit_credit_values.put(CustomerEntry.CUSTOMER_ID, mCurrentId);
                CustomerDbHelper mAddDbHelper = new CustomerDbHelper(this);
                SQLiteDatabase database = mAddDbHelper.getWritableDatabase();

                if (mCreditDebit == CustomerEntry.SELECT_DEBIT) {
                    mCurrentTotal = mCurrentTotal - debit_credit;
                    debit_credit_values.put(CustomerEntry.ADD_DEBIT, debit_credit);
                    long i = database.insert(CustomerEntry.TABLE_DEBIT, null, debit_credit_values);

                } else if (mCreditDebit == CustomerEntry.SELECT_CREDIT) {
                    mCurrentTotal = mCurrentTotal + debit_credit;
                    debit_credit_values.put(CustomerEntry.ADD_CREDIT, debit_credit);
                    long i = database.insert(CustomerEntry.TABLE_CREDIT, null, debit_credit_values);
                } else {
                    mCurrentTotal = mCurrentTotal;
                }
                database.close();
            }

            values.put(CustomerEntry.COLUMN_CUSTOMER_TOTAL, mCurrentTotal);
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentCustomerUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.

            int rowsAffected = getContentResolver().update(mCurrentCustomerUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentCustomerUri == null) {
            MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
            menuItemDelete.setVisible(false);
            MenuItem menuItemEdit = menu.findItem(R.id.edit_item);
            menuItemEdit.setVisible(false);
        } else {
            mNameEditText = (EditText) findViewById(R.id.edit_customername);
            mAccountEditText = (EditText) findViewById(R.id.edit_account);
            mMobileEditText = (EditText) findViewById(R.id.edit_mobile);
            mNameEditText.setEnabled(false);
            mAccountEditText.setEnabled(false);
            mMobileEditText.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveCustomer();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case R.id.edit_item:
                // It will enable the editable items.
                enableEditableItems();
                mActivityEditMode = true;
                return true;
            case R.id.edit_history:

                Intent intent = new Intent(EditorActivity.this,HistoryActivity.class);
                int IntentPassId = mCurrentId;
                Uri currentCustomerUri = ContentUris.withAppendedId(CustomerEntry.CONTENT_URI, mCurrentId);
                //intent.putExtra("currentId", IntentPassId);
                intent.setData(currentCustomerUri);

                startActivity(intent);
                return true;
            case android.R.id.home:
                // If the customer hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mCustomerHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enableEditableItems() {
        // Enable name customer account number, name and mobile number
        mNameEditText = (EditText) findViewById(R.id.edit_customername);
        mAccountEditText = (EditText) findViewById(R.id.edit_account);
        mMobileEditText = (EditText) findViewById(R.id.edit_mobile);
        mNameEditText.setEnabled(true);
        mAccountEditText.setEnabled(true);
        mMobileEditText.setEnabled(true);
        // Spinner, date entry and receipt will be gone and will not be available.
        mSpinnerLayout = (LinearLayout) findViewById(R.id.spinne_layout);
        mDateLayout = (LinearLayout) findViewById(R.id.date_layout);
        mReceiptLayout = (LinearLayout) findViewById(R.id.receipt_layout);
        mSpinnerLayout.setVisibility(View.GONE);
        mDateLayout.setVisibility(View.GONE);
        mReceiptLayout.setVisibility(View.GONE);

    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mCustomerHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                CustomerEntry._ID,
                CustomerEntry.COLUMN_CUSTOMER_NAME,
                CustomerEntry.COLUMN_CUSTOMER_ACCOUNT,
                CustomerEntry.COLUMN_CUSTOMER_MOBILE,
                CustomerEntry.COLUMN_CUSTOMER_TOTAL};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,    // Parent activity context
                mCurrentCustomerUri,           //Provider content to URI
                projection,                     //Columns to include in resulting Cursor
                null,                  // No Selection clause
                null,               // No selection arguments
                null);                 // Default sort order.

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of customer attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(CustomerEntry.COLUMN_CUSTOMER_NAME);
            int accountColumnIndex = cursor.getColumnIndex(CustomerEntry.COLUMN_CUSTOMER_ACCOUNT);
            int mobileColumnIndex = cursor.getColumnIndex(CustomerEntry.COLUMN_CUSTOMER_MOBILE);
            int totalColumnIndex = cursor.getColumnIndex((CustomerEntry.COLUMN_CUSTOMER_TOTAL));
            int idCoulmnIndex = cursor.getColumnIndex(CustomerEntry._ID);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String account = cursor.getString(accountColumnIndex);
            String mobile = cursor.getString(mobileColumnIndex);
            int Total = cursor.getInt(totalColumnIndex);
            mCurrentId = cursor.getInt(idCoulmnIndex);
            //System.out.println("hello");

            mCurrentTotal = Total;

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAccountEditText.setText(account);
            mMobileEditText.setText(mobile);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mAccountEditText.setText("");
        mMobileEditText.setText("");

    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentCustomerUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentCustomerUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
