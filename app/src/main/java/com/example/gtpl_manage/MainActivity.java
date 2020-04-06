package com.example.gtpl_manage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.gtpl_manage.data.CustomerContract.CustomerEntry;

/*
Display list of all available.
 */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CUSTOMER_LOADER = 0;

    CustomerCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup FAB on opening of this activity.

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // Find the list view which will be populated in display.

        ListView customerListView = (ListView)findViewById(R.id.list);

        // Find and set empty view on list view, so that it only shows when there is no entry.
        View emptyView = findViewById(R.id.empty_view);
        customerListView.setEmptyView(emptyView);

        // Setup an adapter to create list item fro each row of pet data in the cursor.
        // There is no pet data yet (until loader finishes) so pass in null cursor.
        mCursorAdapter = new CustomerCursorAdapter(this, null);
        customerListView.setAdapter(mCursorAdapter);


        //setup a click listener.
        customerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create a new intent to got to Editor activity.
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                //From the content URI that represents the specific pet that was clicked on,
                //by appending the "id" (passed as input this method on to the
                //{@link PetEntry#CONTENT_URI}.
                //For example URI would be "content://com.example.android.gtpl_manage/customers/2"
                //if the pet with ID 2 was clicked on.
                Uri currentCustomerUri = ContentUris.withAppendedId(CustomerEntry.CONTENT_URI, id);

                //Set URI on the data field of the intent.
                //Here setData will pass all the information regarding URI.
                //Instead if we want to pass only few strings or integer then we might
                //use putExtra method.
                //Both methods are predefined.
                intent.setData(currentCustomerUri);

                //Launch the {@link EditorActivity} to display the data of the current pet.
                //intent.getStr
                startActivity(intent);

            }
        });
        // Kick off the loader.
        final LoaderManager supportLoaderManager = getSupportLoaderManager();
        supportLoaderManager.initLoader(CUSTOMER_LOADER,null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_CUSTOMER_NAME, "Bharat Prajapati");
        values.put(CustomerEntry.COLUMN_CUSTOMER_ACCOUNT, "12");
        values.put(CustomerEntry.COLUMN_CUSTOMER_MOBILE, "9925219756");
        values.put(CustomerEntry.COLUMN_CUSTOMER_TOTAL, 0);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link CustomerEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.

        Uri newUri = getContentResolver().insert(CustomerEntry.CONTENT_URI, values);

    }
    // Create method to delete all pets.

    private  void deleteAllPet(){
        // Here second argument can be content which we need to look for such as by name or id etc.
        // 3rd argument can be selectionArgs.
        // Here I want to delete all the records hence I have put null for both the arguments.
        int deleteUri = getContentResolver().delete(CustomerEntry.CONTENT_URI,null,null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all:
                // It will delete all the entries in the database.
                deleteAllPet();
                /*
                Toast.makeText(this, "hello",
                        Toast.LENGTH_SHORT).show();*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns which we are going to display.
        String[] projection = {
                CustomerEntry._ID,
                CustomerEntry.COLUMN_CUSTOMER_NAME,
                CustomerEntry.COLUMN_CUSTOMER_ACCOUNT,
                CustomerEntry.COLUMN_CUSTOMER_MOBILE,
                CustomerEntry.COLUMN_CUSTOMER_TOTAL};
        // This loader will execute the ContentProvider's query method in background thread
        return new CursorLoader(this,    // Parent activity context
                CustomerEntry.CONTENT_URI,           //Provider content to URI
                projection,                     //Columns to include in resulting Cursor
                null,                  // No Selection clause
                null,               // No selection arguments
                null);                 // Default sort order.
    }

    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data) {
        // Update {@link CustomerCourserAdapter} with this new cursor containing updated pet data.
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {
        // Callback when data needs to be deleted.
        mCursorAdapter.swapCursor(null);
    }
/*
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/
}
