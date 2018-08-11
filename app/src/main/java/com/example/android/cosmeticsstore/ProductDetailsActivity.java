package com.example.android.cosmeticsstore;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.widget.Button;
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

    private Button decreaseButton;
    private Button increaseButton;

    private Button call;

    private boolean mCosHasChanged = false;

    int quantity;


    public void addQuantity (View view) {
        quantity++;
        quantityTV.setText(String.valueOf(quantity));

    }

    public void decreaseQuantity (View view) {
        quantity--;
        if (quantity < 0) {
            quantity = 0;
        }
        quantityTV.setText(String.valueOf(quantity));
    }

   private void callBtn() {
        if (!TextUtils.isEmpty(contactsTV.getText().toString())) {
            Intent intentCall = new Intent(Intent.ACTION_DIAL);
            intentCall.setData(Uri.parse("tel:" + contactsTV.getText().toString()));
            startActivity(intentCall);
        }
   }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mCosHasChanged = true;
            return false;
        }
    };

    private Button.OnTouchListener buttonTouchListener = new Button.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mCosHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        Intent intent = getIntent();
        mCurrentCosUri = intent.getData();

        setTitle(getString(R.string.editor_activity_title_product_details));

        getLoaderManager().initLoader(EXISTING_COS_LOADER, null, this);

        nameTV = findViewById(R.id.prod_name_tv);
        priceTV = findViewById(R.id.prod_price_tv);
        quantityTV = findViewById(R.id.prod_quant_tv);
        supplierTV = findViewById(R.id.prod_sup_tv);
        contactsTV = findViewById(R.id.sup_cont_tv);
        call = findViewById(R.id.order_button);

        nameTV.setOnTouchListener(mTouchListener);
        priceTV.setOnTouchListener(mTouchListener);
        quantityTV.setOnTouchListener(mTouchListener);
        supplierTV.setOnTouchListener(mTouchListener);
        contactsTV.setOnTouchListener(mTouchListener);

        increaseButton = findViewById(R.id.decrease_button);
        decreaseButton = findViewById(R.id.increase_button);

        increaseButton.setOnTouchListener(buttonTouchListener);
        decreaseButton.setOnTouchListener(buttonTouchListener);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBtn();
            }
        });

    }

    private void saveProduct() {
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
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentCosUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
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
                saveProduct();
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
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String contacts = cursor.getString(contactsColumnIndex);

            nameTV.setText(name);
            priceTV.setText(String.valueOf(price));
            quantityTV.setText(String.valueOf(quantity));
            supplierTV.setText(supplier);
            contactsTV.setText(contacts);
            this.quantity = quantity;
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
