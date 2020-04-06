package com.example.gtpl_manage;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.gtpl_manage.data.CustomerContract.CustomerEntry;
import com.example.gtpl_manage.data.CustomerContract;

public class CustomerHistoryCursorAdapter extends CursorAdapter {
    /* Constructor of class. */
    public CustomerHistoryCursorAdapter(Context context, Cursor c) { super(context, c, 0 /* flags */); }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_view.xml
        return LayoutInflater.from(context).inflate(R.layout.list_history,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find the text views by id.
        TextView amountTextView = (TextView)view.findViewById(R.id.list_amount);
        TextView dateTextView = (TextView)view.findViewById(R.id.list_date);
        TextView receiptTextView = (TextView)view.findViewById(R.id.list_amount);

        // find the column of credit_record table attribute.
        int amountCoulmnIndex = cursor.getColumnIndex(CustomerEntry.ADD_DEBIT);
        int dateColumnIndex = cursor.getColumnIndex(CustomerEntry.DEBIT_DATE);
        int receiptColumnIndex = cursor.getColumnIndex(CustomerEntry.DEBIT_RECEIPT);

        // Read attribute from the cursor.
        int amount = cursor.getInt(amountCoulmnIndex);
        String date = cursor.getString(dateColumnIndex);
        String receipt = cursor.getString(receiptColumnIndex);

        //set text view with data.
        amountTextView.setText(amount);
        dateTextView.setText(date);
        receiptTextView.setText(receipt);

    }
}
