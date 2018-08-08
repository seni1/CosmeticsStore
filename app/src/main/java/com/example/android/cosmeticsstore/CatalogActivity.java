package com.example.android.cosmeticsstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.example.android.cosmeticsstore.data.CosDbHelper;
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

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView petListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new CosCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        // Setup item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link CosmeticsEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.cosmetics/cosmetics/2
                // if the product with ID 2 was clicked on.
                Uri currentCosUri = ContentUris.withAppendedId(CosmeticsEntry.CONTENT_URI, id);

                // Set teh URI on the data field of the intent
                intent.setData(currentCosUri);

                // Launch the {@link EditorActivity} to display teh data for the current pet.
                startActivity(intent);

            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(CosmeticsEntry.COLUMN_PET_NAME, "Toto");
        values.put(CosmeticsEntry.COLUMN_PET_BREED, "Terrier");
        values.put(CosmeticsEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(CosmeticsEntry.COLUMN_PET_WEIGHT, 7);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
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

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,  // Parent activity context
                CosmeticsEntry.CONTENT_URI,    // Provider content URI to query
                projection,                    // Columns to include in the resulting Cursor
                null,                 // No selection clause
                null,              // No selection arguments
                null);                // Default sort order

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update {@link CosCursorAdapter with this new cursor containing updated product data
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }

}
