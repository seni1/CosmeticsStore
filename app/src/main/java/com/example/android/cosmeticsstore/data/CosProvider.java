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

public class CosProvider extends ContentProvider {

    public static final String LOG_TAG = CosProvider.class.getSimpleName();
    private static final int COSMS = 900;
    private static final int COS_ID = 901;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(CosContract.CONTENT_AUTHORITY, CosContract.PATH_COSMS, COSMS);

        sUriMatcher.addURI(CosContract.CONTENT_AUTHORITY, CosContract.PATH_COSMS + "/#", COS_ID);
    }

    private CosDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new CosDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {

            case COSMS:
                cursor = database.query(CosmeticsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case COS_ID:

                selection = CosmeticsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(CosmeticsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COSMS:
                return insertCos(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertCos(Uri uri, ContentValues values) {

        String name = values.getAsString(CosmeticsEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = values.getAsInteger(CosmeticsEntry.COLUMN_PRODUCT_PRICE);

        if (price == null) {
            throw new IllegalArgumentException("Pet requires valid price");
        }

        Integer quantity = values.getAsInteger(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY);

        if (quantity == null) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        String supplier = values.getAsString(CosmeticsEntry.COLUMN_SUPPLIER_NAME);

        if (supplier == null) {
            throw new IllegalArgumentException("Product requires supplier name");
        }

        String contacts = values.getAsString(CosmeticsEntry.COLUMN_CONTACTS);

        if (contacts == null) {
            throw new IllegalArgumentException("Product requires supplier contacts");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(CosmeticsEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case COSMS:
                return updateCos(uri, contentValues, selection, selectionArgs);

            case COS_ID:

                selection = CosmeticsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateCos(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateCos(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(CosmeticsEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(CosmeticsEntry.COLUMN_PRODUCT_NAME);

            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(CosmeticsEntry.COLUMN_PRODUCT_PRICE)) {

            Integer price = values.getAsInteger(CosmeticsEntry.COLUMN_PRODUCT_PRICE);

            if (price == null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.containsKey(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY)) {


            Integer quantity = values.getAsInteger(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if (values.containsKey(CosmeticsEntry.COLUMN_SUPPLIER_NAME)) {


            String supplier = values.getAsString(CosmeticsEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires a supplier name");
            }
        }

        if (values.containsKey(CosmeticsEntry.COLUMN_CONTACTS)) {


            String contacts = values.getAsString(CosmeticsEntry.COLUMN_CONTACTS);
            if (contacts == null) {
                throw new IllegalArgumentException("Product requires valid supplier contacts");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(CosmeticsEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COSMS:

                rowsDeleted = database.delete(CosmeticsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case COS_ID:

                selection = CosmeticsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(CosmeticsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

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
