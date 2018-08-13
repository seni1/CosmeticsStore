package com.example.android.cosmeticsstore;


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
import android.widget.Toast;

import com.example.android.cosmeticsstore.data.CosContract.CosmeticsEntry;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_COS_LOADER = 0;

    private Uri mCurrentCosUri;

    private EditText mNameEditText;
    private EditText priceET;
    private EditText quantityET;
    private EditText supplierET;
    private EditText contactsET;

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
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentCosUri = intent.getData();

        if (mCurrentCosUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_COS_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.edit_name);
        priceET = findViewById(R.id.edit_price);
        quantityET = findViewById(R.id.edit_quantity);
        supplierET = findViewById(R.id.edit_supplier);
        contactsET = findViewById(R.id.edit_contacts);

        mNameEditText.setOnTouchListener(mTouchListener);
        priceET.setOnTouchListener(mTouchListener);
        quantityET.setOnTouchListener(mTouchListener);
        supplierET.setOnTouchListener(mTouchListener);
        contactsET.setOnTouchListener(mTouchListener);

    }

    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = priceET.getText().toString().trim();
        String quantityString = quantityET.getText().toString().trim();
        String supplierString = supplierET.getText().toString().trim();
        String contactsString = contactsET.getText().toString().trim();

        if (mCurrentCosUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(contactsString)) {

            Toast.makeText(this, getString(R.string.product_not_saved_all_fields_must_be_entered),
                    Toast.LENGTH_LONG).show();

            return;
        }

        ContentValues values = new ContentValues();
        values.put(CosmeticsEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(CosmeticsEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(CosmeticsEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(CosmeticsEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(CosmeticsEntry.COLUMN_CONTACTS, contactsString);

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
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mCosHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

            mNameEditText.setText(name);
            priceET.setText(price);
            quantityET.setText(quantity);
            supplierET.setText(supplier);
            contactsET.setText(contacts);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        priceET.setText("");
        quantityET.setText("");
        supplierET.setText("");
        contactsET.setText("");
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


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteProduct() {
        if (mCurrentCosUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentCosUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}