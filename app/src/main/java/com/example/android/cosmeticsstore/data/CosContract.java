package com.example.android.cosmeticsstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class CosContract {

    private CosContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.cosmeticsstore";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COSMS = "cosms";

    public static final class CosmeticsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COSMS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COSMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COSMS;

        public final static String TABLE_NAME = "cosmetics";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME = "name";

        public final static String COLUMN_PRODUCT_PRICE = "price";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        public final static String COLUMN_SUPPLIER_NAME = "supplier";

        public final static String COLUMN_CONTACTS = "contacts";

    }
}



































