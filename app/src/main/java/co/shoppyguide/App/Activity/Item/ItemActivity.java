package co.shoppyguide.App.Activity.Item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Adapter.CategoryAdapater;
import co.shoppyguide.App.Adapter.ItemAdapter;
import co.shoppyguide.App.Database.DAO;
import co.shoppyguide.App.Database.DatabaseClient;
import co.shoppyguide.App.Model.Category;
import co.shoppyguide.App.Model.ListItem;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityItemBinding;
import es.dmoral.toasty.Toasty;
import spencerstudios.com.bungeelib.Bungee;

public class ItemActivity extends AppCompatActivity {

    ActivityItemBinding binding;
    private Dialog dialog;
    private EditText editTextItemName, editTextItemDescription, editTextItemQunatity, editTextItemConsumption;
    private String mCategoryName;
    private ItemAdapter itemAdapter;
    private Long mCategoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ItemActivity.this, R.layout.activity_item);

        final Category category = (Category) getIntent().getSerializableExtra("category");

        assert category != null;
        try {
            loadCategory(category);
            getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }


        setSupportActionBar(binding.toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                Bungee.swipeLeft(ItemActivity.this);
            }
        });

        binding.addFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


    }

    private void loadCategory(Category category) {
        mCategoryName = category.getCategoryname();
        mCategoryID = category.getCategoryId();
        binding.toolbar.setTitle(mCategoryName);
    }

    private void getItems() {


        @SuppressLint("StaticFieldLeak")
        class GetItems extends AsyncTask<Void, Void, List<ListItem>> {

            @Override
            protected List<ListItem> doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .getDAO()
                        .getItemByCategory(String.valueOf(mCategoryID));
            }

            @Override
            protected void onPostExecute(List<ListItem> listItems) {
                super.onPostExecute(listItems);
                if (listItems.isEmpty()) {
                    binding.llEmptyBox.setVisibility(View.VISIBLE);
                } else {
                    binding.llEmptyBox.setVisibility(View.GONE);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    itemAdapter = new ItemAdapter(getApplicationContext(), listItems);
                    binding.recyclerView.setAdapter(itemAdapter);
                }
            }
        }

        GetItems getItems = new GetItems();
        getItems.execute();
    }

    public void showDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_dialog);
        TextView cancelButton = dialog.findViewById(R.id.buttonClose);
        final Button buttonSaveCategory = dialog.findViewById(R.id.buttonSave);
        editTextItemName = dialog.findViewById(R.id.editTextItemName);
        editTextItemDescription = dialog.findViewById(R.id.editTextItemDescription);
        editTextItemQunatity = dialog.findViewById(R.id.editTextItemQunatity);
        editTextItemConsumption = dialog.findViewById(R.id.editTextItemConsumption);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonSaveCategory.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Assert")
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        dialog.show();
    }

    private void saveItem() {
        final String mItemName = editTextItemName.getText().toString();
        final String mItemDescription = editTextItemDescription.getText().toString();
        final String mItemQuantity = editTextItemQunatity.getText().toString();
        final String mItemConsumption = editTextItemConsumption.getText().toString();
        if (mItemName.isEmpty()) {
            editTextItemName.setError("Name Required");
            editTextItemName.requestFocus();
            return;
        } else if (mItemDescription.isEmpty()) {
            editTextItemDescription.setError("Description Required");
            editTextItemDescription.requestFocus();
            return;
        } else if (mItemQuantity.isEmpty()) {
            editTextItemQunatity.setError("Quantity Required");
            editTextItemQunatity.requestFocus();
            return;
        } else if (mItemConsumption.isEmpty()) {
            editTextItemConsumption.setError("Consumption Required");
            editTextItemConsumption.requestFocus();
            return;
        } else {
            Integer quantity = Integer.valueOf(mItemQuantity);
            Integer consumption = Integer.valueOf(mItemConsumption);
            if (consumption > quantity) {
                Toasty.error(this, "Consumption can't bigger than Quantity", Toasty.LENGTH_SHORT).show();
            } else {
                class SaveItem extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {

                        //Creating Item
                        ListItem listItem = new ListItem();
                        listItem.setCategoryID(mCategoryID);
                        listItem.setItemName(mItemName);
                        listItem.setItemAddDate(new Date());
                        listItem.setItemCategory(mCategoryName);
                        listItem.setItemQuantity(mItemQuantity);
                        listItem.setItemDescription(mItemDescription);
                        listItem.setItemConsumption(mItemConsumption);
                        //adding to database
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                .getDAO()
                                .insertListItem(listItem);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        getItems();
                        dialog.dismiss();
                        Toasty.success(getApplicationContext(), "Item Added", Toasty.LENGTH_LONG).show();
                    }
                }
                SaveItem saveItem = new SaveItem();
                saveItem.execute();
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
        Bungee.swipeLeft(ItemActivity.this);
        super.onBackPressed();
    }
}
