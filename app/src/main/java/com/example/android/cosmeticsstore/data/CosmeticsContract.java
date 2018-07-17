package com.example.android.cosmeticsstore.data;

import android.provider.BaseColumns;

/**
 * API Contract for the Cosmetics Store app.
 */

public class CosmeticsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CosmeticsContract () {}

    /**
     * Inner class that defines constant values for the cosmetics database table.
     * Each entry in the table represents a single cosmetics product.
     */
    public static final class CosmeticsEntry implements BaseColumns {

        /** Name of database table for cosmetics */
        public final static String TABLE_NAME = "cosmetics";

        /**
         * Unique ID number for the cosmetics (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Product name.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * Price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Quantity of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Supplier name.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier";

        /**
         * Supplier Phone number
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CONTACTS = "contacts";

    }
}



































