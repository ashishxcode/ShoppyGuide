package co.shoppyguide.App.Activity.Category;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Date;
import java.util.Objects;

import co.shoppyguide.App.Activity.Item.ViewItemActivity;
import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Database.DatabaseClient;
import co.shoppyguide.App.Model.Category;
import co.shoppyguide.App.Model.ListItem;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityUpdateCategoryBinding;
import es.dmoral.toasty.Toasty;
import spencerstudios.com.bungeelib.Bungee;

public class UpdateCategoryActivity extends AppCompatActivity {

    ActivityUpdateCategoryBinding binding;
    private long mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_category);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), ManageCategoryActivity.class);
                startActivity(mainIntent);
                Bungee.swipeLeft(UpdateCategoryActivity.this);
            }
        });

        final Category category = (Category) getIntent().getSerializableExtra("category");

        assert category != null;
        loadCategory(category);

        binding.buttonSaveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategory(category);
            }
        });

        binding.buttonDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(category);
            }
        });

    }

    private void deleteCategory(final Category category) {

        @SuppressLint("StaticFieldLeak")
        class DeleteCategory extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .getDAO()
                        .deleteCategory(String.valueOf(mCategory));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                deleteItem();
            }
        }

        DeleteCategory deleteCategory = new DeleteCategory();
        deleteCategory.execute();
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
                Toasty.success(getApplicationContext(), "Category Deleted", Toasty.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(getApplicationContext(), ManageCategoryActivity.class));
                Bungee.swipeRight(UpdateCategoryActivity.this);
            }
        }

        DeleteItem deleteItem = new DeleteItem();
        deleteItem.execute();
    }

    private void updateCategory(final Category category) {
        final String mCategoryName = binding.editTextCategoryName.getText().toString();

        if (mCategoryName.isEmpty()) {
            binding.editTextCategoryName.setError("Category Name Required");
            binding.editTextCategoryName.requestFocus();
        }
        @SuppressLint("StaticFieldLeak")
        class UpdateCategory extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                category.setCategoryname(mCategoryName);
                category.setCategoryAddDate(new Date());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .getDAO()
                        .updateCategory(category);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toasty.success(getApplicationContext(), "Category Updated", Toasty.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(getApplicationContext(), ManageCategoryActivity.class));
                Bungee.swipeRight(UpdateCategoryActivity.this);
            }
        }
        UpdateCategory updateCategory = new UpdateCategory();
        updateCategory.execute();


    }


    private void loadCategory(Category category) {
        binding.editTextCategoryName.setText(category.getCategoryname());
        mCategory = category.getCategoryId();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ManageCategoryActivity.class);
        startActivity(intent);
        finish();
        Bungee.swipeRight(UpdateCategoryActivity.this);
        super.onBackPressed();
    }
}
