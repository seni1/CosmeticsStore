package com.example.android.cosmeticsstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.cosmeticsstore.data.CosContract.CosmeticsEntry;

import java.security.Provider;

/**
 * {@link android.content.ContentProvider} for Cosmetics App.
 */

public class CosProvider extends ContentProvider {

    /** URI matcher code for the content URI for the cosmetics table */
    private static final int COSMS = 900;

    /** URI matcher code for the content URI for a single products in the cosmetics table */
    private static final int COS_ID = 901;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(CosContract.CONTENT_AUTHORITY, CosContract.PATH_COSMS, COSMS);

        sUriMatcher.addURI(CosContract.CONTENT_AUTHORITY, CosContract.PATH_COSMS + "/#", COS_ID);
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = CosProvider.class.getSimpleName();

    /** Database helper object */
    private CosDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new CosDbHelper(getContext());
        return true;
    }



    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case COSMS:
                // For the COSMS code, query the cosmetics table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the cosmetics table.
                cursor = database.query(CosmeticsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case COS_ID:
                // For the COS_ID code, extract out the ID from the URI.
                selection = CosmeticsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(CosmeticsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return Cursor
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COSMS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(CosmeticsEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the price is not null
        Integer price = values.getAsInteger(CosmeticsEntry.COLUMN_PRODUCT_PRICE);

        if (price == null) {
            throw new IllegalArgumentException("Pet requires valid price");
        }

        // Check that the price is not null
        Integer quantity = values.getAsInteger(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY);

        if (quantity == null) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        // Check that the price is not null
        String supplier = values.getAsString(CosmeticsEntry.COLUMN_SUPPLIER_NAME);

        if (supplier == null) {
            throw new IllegalArgumentException("Product requires supplier name");
        }

        // Check that the price is not null
        Integer contacts = values.getAsInteger(CosmeticsEntry.COLUMN_CONTACTS);

        if (contacts == null) {
            throw new IllegalArgumentException("Product requires supplier contacts");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(CosmeticsEntry.TABLE_NAME, null, values);

        //If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        // uri: content://com.example.android/cosmetics
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COSMS:
                return updateCos(uri, contentValues, selection, selectionArgs);
            case COS_ID:
                // For the COS_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CosmeticsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateCos(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update products in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more products).
     * Return the number of rows that were successfully updated.
     *
     * In the CosProvider update() method, perform sanity checks for each of the possible
     * updated values. First ,we use ContentValues containsKey() method to check if each attribute
     * is present or not. If the key is present, then we can proceed with extracting the value
     * from it, and then checking if it’s valid.
     * Another way to think about this code change is that we’re wrapping an “if” check around
     * the code block for each product attribute (from the insertCos() method) and making sure
     * the attribute is present first.
     */

    private int updateCos(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link CosmeticsEntry#COLUMN_PRRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(CosmeticsEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(CosmeticsEntry.COLUMN_PRODUCT_NAME);

            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link CosEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(CosmeticsEntry.COLUMN_PRODUCT_PRICE)) {


            Integer price = values.getAsInteger(CosmeticsEntry.COLUMN_PRODUCT_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        // If the {@link CosEntry#COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY)) {


            Integer quantity = values.getAsInteger(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        // If the {@link CosEntry#COLUMN_SUPPLIER_NAME} key is present,
        // check that the supplier value is not null.
        if (values.containsKey(CosmeticsEntry.COLUMN_SUPPLIER_NAME)) {


            String supplier = values.getAsString(CosmeticsEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires a supplier name");
            }
        }

        // If the {@link CosEntry#COLUMN_CONTACTS} key is present,
        // check that the contacts value is valid.
        if (values.containsKey(CosmeticsEntry.COLUMN_CONTACTS)) {


            String contacts = values.getAsString(CosmeticsEntry.COLUMN_CONTACTS);
            if (contacts == null) {
                throw new IllegalArgumentException("Product requires valid supplier contacts");
            }
        }


        /**
         * This is also a chance to do a quick check on the size of the ContentValues object.
         * If there are no key/value pairs in it, then just return 0 rows affected.
         * There is no need to do the database operation if there are no new values to update with,
         * and every database operation costs some amount of memory resources on the device.
         */

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(CosmeticsEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get a writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COSMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CosmeticsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case COS_ID:
                // Delete a single row given by te ID in the URI
                selection = CosmeticsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(CosmeticsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COSMS:
                return CosmeticsEntry.CONTENT_LIST_TYPE;

            case COS_ID:
                return CosmeticsEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }

    }

}
