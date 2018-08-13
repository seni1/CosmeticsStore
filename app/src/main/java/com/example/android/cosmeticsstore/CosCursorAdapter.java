package com.example.android.cosmeticsstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.cosmeticsstore.data.CosContract;
import com.example.android.cosmeticsstore.data.CosContract.CosmeticsEntry;

public class CosCursorAdapter extends CursorAdapter {

    public CosCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.quantity);
        final Button buttonTextView = view.findViewById(R.id.buy_button);

        int idColumnIndex = cursor.getColumnIndex(CosmeticsEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY);

        int cosId = cursor.getInt(idColumnIndex);
        String cosName = cursor.getString(nameColumnIndex);
        int cosPrice = cursor.getInt(priceColumnIndex);
        final int cosQuantity = cursor.getInt(quantityColumnIndex);


        nameTextView.setText(cosName);
        priceTextView.setText(String.valueOf(cosPrice));
        quantityTextView.setText(String.valueOf(String.valueOf(cosQuantity)));
        final Uri mCurrentCosUri = ContentUris.withAppendedId(CosmeticsEntry.CONTENT_URI, cosId);

        buttonTextView.setOnClickListener(new View.OnClickListener() {
            //TODO: explain the method...
            @Override
            public void onClick(View v) {
                if (cosQuantity > 0) {

                    int currQunatity = cosQuantity - 1;
                    quantityTextView.setText(String.valueOf(currQunatity));
                    ContentValues values = new ContentValues();
                    values.put(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY, currQunatity);
                    context.getContentResolver().update(mCurrentCosUri, values, null, null);

                }
            }
        });

        if (quantityTextView.getText().equals("0")) {
            buttonTextView.setVisibility(View.GONE);
        } else {
            buttonTextView.setVisibility(View.VISIBLE);
        }

    }


}
