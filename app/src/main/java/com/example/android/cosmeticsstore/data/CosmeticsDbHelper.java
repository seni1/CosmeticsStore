package com.example.android.cosmeticsstore.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.cosmeticsstore.data.CosmeticsContract.CosmeticsEntry;

public class CosmeticsDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CosmeticsDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "cosmetics.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    //Constructs a new instance of {@link CosmeticsDbHelper}
    public CosmeticsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //CREATE TABLE cosmetics (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, weight

        String SQL_CREATE_COSMO_TABLE = "CREATE TABLE " + CosmeticsEntry.TABLE_NAME + " ("
                + CosmeticsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CosmeticsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + CosmeticsEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + CosmeticsEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + CosmeticsEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + CosmeticsEntry.COLUMN_CONTACTS + " INTEGER NOT NULL);";

        //Execute the SQL statement
        db.execSQL(SQL_CREATE_COSMO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
