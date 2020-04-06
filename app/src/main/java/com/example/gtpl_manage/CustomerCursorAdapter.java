package com.example.gtpl_manage;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.gtpl_manage.data.CustomerContract.CustomerEntry;
/*
Here CustomerCursorAdapter is adapter for list or grid view.
that uses {@Link Cursor} as its data source from database.
 */
public class CustomerCursorAdapter extends CursorAdapter {
    /*
    Constructs a new cursor Adapter.
     */
    public CustomerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_view.xml
        return LayoutInflater.from(context).inflate(R.layout.list_view,parent,false);
    }
    /**
     * This method binds the Customer data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView accountTextView = (TextView) view.findViewById(R.id.account);
        TextView mobileTextView = (TextView) view.findViewById(R.id.mobile);
        TextView totalTextView = (TextView) view.findViewById(R.id.total);

        // Find the columns of customer attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(CustomerEntry.COLUMN_CUSTOMER_NAME);
        int accountColumnIndex = cursor.getColumnIndex(CustomerEntry.COLUMN_CUSTOMER_ACCOUNT);
        int mobileColumnIndex = cursor.getColumnIndex(CustomerEntry.COLUMN_CUSTOMER_MOBILE);
        int totalColumnIndex = cursor.getColumnIndex(CustomerEntry.COLUMN_CUSTOMER_TOTAL);

        // Read the pet attributes from the Cursor for the current pet
        String customerName = cursor.getString(nameColumnIndex);
        String customerAccount = cursor.getString(accountColumnIndex);
        String customerMobile = cursor.getString(mobileColumnIndex);
        String customerTotal = cursor.getString(totalColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(customerName);
        accountTextView.setText(customerAccount);
        mobileTextView.setText(customerMobile);
        totalTextView.setText(customerTotal);
    }
}
