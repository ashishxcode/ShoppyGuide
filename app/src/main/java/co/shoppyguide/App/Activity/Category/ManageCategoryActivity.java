package co.shoppyguide.App.Activity.Category;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Activity.Profile.EditProfileActivity;
import co.shoppyguide.App.Activity.Profile.ProfileActivity;
import co.shoppyguide.App.Adapter.CategoryAdapater;
import co.shoppyguide.App.Adapter.ManageCategoryAdapter;
import co.shoppyguide.App.Database.AppDatabase;
import co.shoppyguide.App.Database.DAO;
import co.shoppyguide.App.Database.DatabaseClient;
import co.shoppyguide.App.Model.Category;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityManageCategoryBinding;
import es.dmoral.toasty.Toasty;
import muyan.snacktoa.SnackToa;
import spencerstudios.com.bungeelib.Bungee;

public class ManageCategoryActivity extends AppCompatActivity {

    ActivityManageCategoryBinding binding;
    private List<Category> categoryList;
    private ManageCategoryAdapter manageCategoryAdapter;
    private EditText editTextCategoryName;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_category);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                Bungee.swipeLeft(ManageCategoryActivity.this);
            }
        });

        binding.addCategoryFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        getCategory();
    }

    private void getCategory() {

        @SuppressLint("StaticFieldLeak")
        class GetCategory extends AsyncTask<Void, Void, List<Category>> {

            @Override
            protected List<Category> doInBackground(Void... voids) {
                categoryList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .getDAO()
                        .getAllCategory();
                return categoryList;
            }

            @Override
            protected void onPostExecute(List<Category> categoryList) {
                super.onPostExecute(categoryList);
                if (categoryList.isEmpty()) {
                    binding.llEmptyBox.setVisibility(View.VISIBLE);
                } else {
                    binding.llEmptyBox.setVisibility(View.GONE);
                    binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    manageCategoryAdapter = new ManageCategoryAdapter(getApplicationContext(), categoryList);
                    binding.recyclerViewCategory.setAdapter(manageCategoryAdapter);
                }
            }
        }

        GetCategory getCategory = new GetCategory();
        getCategory.execute();
    }

    public void showDialog() {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.category_dialog);
        TextView cancelButton = dialog.findViewById(R.id.buttonClose);
        final Button buttonSaveCategory = dialog.findViewById(R.id.buttonSaveCategory);
        editTextCategoryName = dialog.findViewById(R.id.editTextCategoryName);
        final TextView tvHeader = dialog.findViewById(R.id.tvHeader);

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
                saveCategory();
            }
        });

        dialog.show();
    }

    private void saveCategory() {
        final String mCategory = editTextCategoryName.getText().toString();

        if (mCategory.isEmpty()) {
            editTextCategoryName.setError("Category Name Required");
            editTextCategoryName.requestFocus();
            return;
        }

        class SaveCategory extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //Creating Category
                Category category = new Category();
                category.setCategoryname(mCategory);
                category.setCategoryAddDate(new Date());

                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .getDAO()
                        .insertCategory(category);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getCategory();
                Toasty.success(getApplicationContext(), "Category Added", Toasty.LENGTH_LONG).show();
                dialog.dismiss();

            }
        }
        SaveCategory sc = new SaveCategory();
        sc.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
        Bungee.swipeLeft(ManageCategoryActivity.this);
    }
}
