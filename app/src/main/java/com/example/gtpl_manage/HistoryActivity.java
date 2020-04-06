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
        Intent intent = getIntent();
        mHistoryUri = CustomerEntry.RECORD_URI;
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
                CustomerEntry.D_ID,
                CustomerEntry.ADD_DEBIT,
                CustomerEntry.DEBIT_DATE,
                CustomerEntry.DEBIT_RECEIPT};

        return new CursorLoader(this,
                mHistoryUri,
                projection,
                null,
                null,
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
