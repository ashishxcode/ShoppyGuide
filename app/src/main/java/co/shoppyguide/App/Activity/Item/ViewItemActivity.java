package co.shoppyguide.App.Activity.Item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import co.shoppyguide.App.Activity.Category.ManageCategoryActivity;
import co.shoppyguide.App.Activity.Category.UpdateCategoryActivity;
import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Database.DatabaseClient;
import co.shoppyguide.App.Model.Category;
import co.shoppyguide.App.Model.ListItem;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityViewItemBinding;
import es.dmoral.toasty.Toasty;
import spencerstudios.com.bungeelib.Bungee;

public class ViewItemActivity extends AppCompatActivity {

    /* Note Don't Change intent to ItemActivity i don't know but after assign intent as a
     ItemActivity all listItem became empty so don't do that */

    ActivityViewItemBinding binding;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private long mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_item);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                Bungee.swipeLeft(ViewItemActivity.this);
            }
        });

        final ListItem listItem = (ListItem) getIntent().getSerializableExtra("listItem");

        assert listItem != null;
        loadItemListView(listItem);

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem(listItem);
            }
        });
        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });
    }

    private void loadItemListView(ListItem listItem) {
        binding.editTextItemName.setText(listItem.getItemName());
        binding.editTextItemDescription.setText(listItem.getItemDescription());
        binding.editTextItemQunatity.setText(listItem.getItemQuantity());
        binding.editTextItemConsumption.setText(listItem.getItemConsumption());
        binding.tvItemCategory.setText(listItem.getItemCategory());
        binding.tvItemDate.setText(sdf.format(listItem.getItemAddDate()));
        mCategory = listItem.getCategoryID();
    }


    private void updateItem(final ListItem listItem) {
        final String mItemName = Objects.requireNonNull(binding.editTextItemName.getText()).toString();
        final String mItemDescription = Objects.requireNonNull(binding.editTextItemDescription.getText()).toString();
        final String mItemQuantity = Objects.requireNonNull(binding.editTextItemQunatity.getText()).toString();
        final String mItemConsumption = Objects.requireNonNull(binding.editTextItemConsumption.getText()).toString();

        if (mItemName.isEmpty()) {
            binding.editTextItemName.setError("Field can't be empty");
            binding.editTextItemName.requestFocus();
        } else if (mItemDescription.isEmpty()) {
            binding.editTextItemDescription.setError("Field can't be empty");
            binding.editTextItemDescription.requestFocus();
        } else if (mItemQuantity.isEmpty()) {
            binding.editTextItemQunatity.setError("Field can't be empty");
            binding.editTextItemQunatity.requestFocus();
        } else if (mItemConsumption.isEmpty()) {
            binding.editTextItemConsumption.setError("Field can't be empty");
            binding.editTextItemConsumption.requestFocus();
        } else {
            Integer quantity = Integer.valueOf(mItemQuantity);
            Integer consumption = Integer.valueOf(mItemConsumption);
            if (consumption > quantity) {
                Toasty.error(this, "Consumption can't bigger than Quantity", Toasty.LENGTH_SHORT).show();
            } else {
                @SuppressLint("StaticFieldLeak")
                class UpdateItem extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        listItem.setItemName(mItemName);
                        listItem.setItemDescription(mItemDescription);
                        listItem.setItemQuantity(mItemQuantity);
                        listItem.setItemConsumption(mItemConsumption);
                        listItem.setItemAddDate(new Date());
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                .getDAO()
                                .updateListItem(listItem);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Toasty.success(getApplicationContext(), "Item Updated", Toasty.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Bungee.swipeRight(ViewItemActivity.this);
                    }
                }
                UpdateItem updateItem = new UpdateItem();
                updateItem.execute();

            }
        }


    }


    private void deleteItem() {
        @SuppressLint("StaticFieldLeak")
        class DeleteItem extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .getDAO()
                        .deleteItemByCategory(String.valueOf(mCategory));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toasty.success(getApplicationContext(), "Item Deleted", Toasty.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Bungee.swipeRight(ViewItemActivity.this);
            }
        }

        DeleteItem deleteItem = new DeleteItem();
        deleteItem.execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        Bungee.swipeRight(ViewItemActivity.this);
        super.onBackPressed();
    }
}
