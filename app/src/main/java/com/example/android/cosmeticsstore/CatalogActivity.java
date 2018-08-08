package com.example.android.cosmeticsstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.cosmeticsstore.data.CosContract.CosmeticsEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PET_LOADER = 8;

    CosCursorAdapter mCursorAdapter;

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

        ListView petListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        mCursorAdapter = new CosCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentCosUri = ContentUris.withAppendedId(CosmeticsEntry.CONTENT_URI, id);

                intent.setData(currentCosUri);

                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(PET_LOADER, null, (android.app.LoaderManager.LoaderCallbacks<Object>) this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {

        ContentValues values = new ContentValues();
        values.put(CosmeticsEntry.COLUMN_PRODUCT_NAME, "Moroccanoil Shampoo");
        values.put(CosmeticsEntry.COLUMN_PRODUCT_PRICE, 20);
        values.put(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY, 13);
        values.put(CosmeticsEntry.COLUMN_SUPPLIER_NAME, "Moroccanoil");
        values.put(CosmeticsEntry.COLUMN_CONTACTS, "89216579965");

        Uri newUri = getContentResolver().insert(CosmeticsEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
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
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Delete all pets
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
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
