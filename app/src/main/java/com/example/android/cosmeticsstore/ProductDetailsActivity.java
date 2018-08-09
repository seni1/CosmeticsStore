package com.example.android.cosmeticsstore;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cosmeticsstore.data.CosContract.CosmeticsEntry;


public class ProductDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_COS_LOADER = 0;

    private Uri mCurrentCosUri;

    private TextView nameTV;
    private TextView priceTV;
    private TextView quantityTV;
    private TextView supplierTV;
    private TextView contactsTV;

    private boolean mCosHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mCosHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        Intent intent = getIntent();
        mCurrentCosUri = intent.getData();

        if (mCurrentCosUri == null) {
            setTitle(getString(R.string.editor_activity_title_product_details));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_COS_LOADER, null, this);
        }

        nameTV = findViewById(R.id.prod_name_tv);
        priceTV = findViewById(R.id.prod_price_tv);
        quantityTV = findViewById(R.id.prod_quant_tv);
        supplierTV = findViewById(R.id.prod_sup_tv);
        contactsTV = findViewById(R.id.sup_cont_tv);

        nameTV.setOnTouchListener(mTouchListener);
        priceTV.setOnTouchListener(mTouchListener);
        quantityTV.setOnTouchListener(mTouchListener);
        supplierTV.setOnTouchListener(mTouchListener);
        contactsTV.setOnTouchListener(mTouchListener);

    }

    private void savePet() {
        String nameString = nameTV.getText().toString().trim();
        String priceString = priceTV.getText().toString().trim();
        String quantityString = quantityTV.getText().toString().trim();
        String supplierString = supplierTV.getText().toString().trim();
        String contactsString = contactsTV.getText().toString().trim();

        if (mCurrentCosUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(contactsString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CosmeticsEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(CosmeticsEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(CosmeticsEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(CosmeticsEntry.COLUMN_CONTACTS, contactsString);

        if (mCurrentCosUri == null) {
            Uri newUri = getContentResolver().insert(CosmeticsEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentCosUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentCosUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePet();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mCosHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {

        if (!mCosHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                CosmeticsEntry._ID,
                CosmeticsEntry.COLUMN_PRODUCT_NAME,
                CosmeticsEntry.COLUMN_PRODUCT_PRICE,
                CosmeticsEntry.COLUMN_PRODUCT_QUANTITY,
                CosmeticsEntry.COLUMN_SUPPLIER_NAME,
                CosmeticsEntry.COLUMN_CONTACTS
        };

        return new CursorLoader(this,   // Parent activity context
                mCurrentCosUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_SUPPLIER_NAME);
            int contactsColumnIndex = cursor.getColumnIndex(CosmeticsEntry.COLUMN_CONTACTS);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String contacts = cursor.getString(contactsColumnIndex);

            nameTV.setText(name);
            priceTV.setText(price);
            quantityTV.setText(quantity);
            supplierTV.setText(supplier);
            contactsTV.setText(contacts);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameTV.setText("");
        priceTV.setText("");
        quantityTV.setText("");
        supplierTV.setText("");
        contactsTV.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteProduct() {
        if (mCurrentCosUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentCosUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}
