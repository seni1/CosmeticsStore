package com.example.android.cosmeticsstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.cosmeticsstore.data.CosContract.CosmeticsEntry;

public class CatalogActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COS_LOADER = 8;

    CosCursorAdapter mCursorAdapter;

    private TextView listQuantityTV;

    private Button buyButton;

    int listQuantity;

    public void decListQuantity (View vew) {
        listQuantity--;
        if (listQuantity < 0) {
            listQuantity = 0;
        }
        listQuantityTV.setText(String.valueOf(listQuantity));
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView cosListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        cosListView.setEmptyView(emptyView);

        mCursorAdapter = new CosCursorAdapter(this, null);
        cosListView.setAdapter(mCursorAdapter);

        cosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, ProductDetailsActivity.class);

                Uri currentCosUri = ContentUris.withAppendedId(CosmeticsEntry.CONTENT_URI, id);

                intent.setData(currentCosUri);

                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(COS_LOADER, null,  this);

        listQuantityTV = findViewById(R.id.quantity);

        buyButton = findViewById(R.id.buy_button);



    }

    private void insertCos() {

        ContentValues values = new ContentValues();
        values.put(CosmeticsEntry.COLUMN_PRODUCT_NAME, "Moroccanoil Shampoo");
        values.put(CosmeticsEntry.COLUMN_PRODUCT_PRICE, 20);
        values.put(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY, 13);
        values.put(CosmeticsEntry.COLUMN_SUPPLIER_NAME, "Moroccanoil");
        values.put(CosmeticsEntry.COLUMN_CONTACTS, "89216579965");

        Uri newUri = getContentResolver().insert(CosmeticsEntry.CONTENT_URI, values);
    }

    private void deleteAllCos() {
        int rowsDeleted = getContentResolver().delete(CosmeticsEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:
                insertCos();
                return true;

            case R.id.action_delete_all_entries:

                deleteAllCos();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                CosmeticsEntry._ID,
                CosmeticsEntry.COLUMN_PRODUCT_NAME,
                CosmeticsEntry.COLUMN_PRODUCT_PRICE,
                CosmeticsEntry.COLUMN_PRODUCT_QUANTITY,
                CosmeticsEntry.COLUMN_SUPPLIER_NAME,
                CosmeticsEntry.COLUMN_CONTACTS};

        return new CursorLoader(this,  // Parent activity context
                CosmeticsEntry.CONTENT_URI,    // Provider content URI to query
                projection,                    // Columns to include in the resulting Cursor
                null,                 // No selection clause
                null,              // No selection arguments
                null);                // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);



    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);

    }

}
