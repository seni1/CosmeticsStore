package com.example.android.cosmeticsstore;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.cosmeticsstore.data.CosmeticsContract;
import com.example.android.cosmeticsstore.data.CosmeticsDbHelper;
import com.example.android.cosmeticsstore.data.CosmeticsContract.CosmeticsEntry;

import static com.example.android.cosmeticsstore.data.CosmeticsContract.CosmeticsEntry.*;

/**
 * Allows user to create a new cosmetics product or edit and existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the product's name */
    private EditText nameEditText;

    /** EditText field to enter the product's price */
    private EditText priceEditText;

    /** EditText field to enter the product's quantity */
    private EditText quantityEditText;

    /** EditText field to enter supplier's name */
    private EditText supplierEditText;

    /** EditText field to enter supplier's contacts */
    private EditText contactsEditText;


    /**
     * Get user input from editor and save new product into database.
     */
    private void insertPet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierString = supplierEditText.getText().toString().trim();
        String contactsString = contactsEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        int quantity = Integer.parseInt(quantityString);
        int contacts = Integer.parseInt(contactsString);

        CosmeticsDbHelper dbHelper = new CosmeticsDbHelper(this);

        // Gets the data repository in the write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and products attributes are the values.
        ContentValues values = new ContentValues();
        values.put(CosmeticsEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(CosmeticsEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(CosmeticsEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(CosmeticsEntry.COLUMN_CONTACTS, contacts);

        // Insert a new row for product in the database, returning the ID of that new row.
        // The first argument for db.insert() is the products table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for product.
        long newRowId = db.insert(CosmeticsEntry.TABLE_NAME,  null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }



    }
}
































