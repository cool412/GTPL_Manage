package com.example.gtpl_manage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.example.gtpl_manage.data.CustomerContract.CustomerEntry;

public class HistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    CustomerHistoryCursorAdapter mHistoryAdapter;

    private static final int HISTORY_LOADER = 0;

    private Uri mHistoryUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Intent intent = getIntent();
        //mHistoryUri = CustomerEntry.RECORD_URI;
        //int currentId = intent.getIntExtra("currentId",0);

        //Find the view by id.
        ListView historyListView = (ListView)findViewById(R.id.history);

        mHistoryAdapter = new CustomerHistoryCursorAdapter(this,null);
        historyListView.setAdapter(mHistoryAdapter);

        // Kick off the loader.
        final LoaderManager supportLoaderManager = getSupportLoaderManager();
        supportLoaderManager.initLoader(HISTORY_LOADER,null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // define projection.
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
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mHistoryAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {
        mHistoryAdapter.swapCursor(null);

    }
}
