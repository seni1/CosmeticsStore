package com.example.android.cosmeticsstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
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
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);


        int nameColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY);


        String cosName = cursor.getString(nameColumnIndex);
        String cosPrice = cursor.getString(priceColumnIndex);
        String cosQuantity = cursor.getString(quantityColumnIndex);


        nameTextView.setText(cosName);
        priceTextView.setText(cosPrice);
        quantityTextView.setText(cosQuantity);
    }

}
