package com.example.android.cosmeticsstore.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.cosmeticsstore.data.CosContract.CosmeticsEntry;

public class CosDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CosDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "cosmetics.db";

    private static final int DATABASE_VERSION = 1;


    public CosDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_COSMO_TABLE = "CREATE TABLE " + CosmeticsEntry.TABLE_NAME + " ("
                + CosmeticsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CosmeticsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + CosmeticsEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + CosmeticsEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + CosmeticsEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + CosmeticsEntry.COLUMN_CONTACTS + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_COSMO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
