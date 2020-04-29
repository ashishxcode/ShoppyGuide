package co.shoppyguide.App.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Dao;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.Buffer;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import co.shoppyguide.App.Activity.Auth.LoginActivity;
import co.shoppyguide.App.Activity.Category.ManageCategoryActivity;
import co.shoppyguide.App.Activity.Item.AllItemActivity;
import co.shoppyguide.App.Activity.OutOfStock.OutOfStockActivity;
import co.shoppyguide.App.Activity.Profile.ProfileActivity;
import co.shoppyguide.App.Adapter.CategoryAdapater;
import co.shoppyguide.App.Adapter.ItemAdapter;
import co.shoppyguide.App.Adapter.ManageCategoryAdapter;
import co.shoppyguide.App.Database.DatabaseClient;
import co.shoppyguide.App.Model.Category;
import co.shoppyguide.App.Model.ListItem;
import co.shoppyguide.App.Model.User;
import co.shoppyguide.R;
import es.dmoral.toasty.Toasty;
import muyan.snacktoa.SnackToa;
import spencerstudios.com.bungeelib.Bungee;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;

    private MenuItem menuHome, menuProfile, menuMyList, menuManageCategory,menuOutOfStock, menuAbout, menuLogout;
    private boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String mUserName, mCategoryName;
    private User user;
    private View llEmptyBox;
    private FloatingActionButton fabAddCategory;
    private RecyclerView recyclerViewCategory;
    private EditText editTextCategoryName;
    private CategoryAdapater categoryAdapater;
    private TextView textViewCategoryCount, textViewItemCount, textViewOutOfStockCount;
    private Dialog dialog;
    private int CategoryCount = 0, ItemCount = 0, OutOfStockCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //tool bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseFirestore.disableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadUserName();
                firebaseFirestore.enableNetwork().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadUserName();
                    }
                });
            }
        });

        //navigation drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null); //disable tint on each icon to use color icon svg

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        textViewCategoryCount = findViewById(R.id.tvCategoryCount);
        textViewItemCount = findViewById(R.id.tvItemCount);
        textViewOutOfStockCount = findViewById(R.id.tvOutOfStockCount);

        //add category
        fabAddCategory = findViewById(R.id.addCategoryFloatingButton);
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        llEmptyBox = findViewById(R.id.llEmptyBox);
        getCategory();
        getRecyclerViewItemCount();
    }

    private void getCategory() {
        @SuppressLint("StaticFieldLeak")
        class GetCategory extends AsyncTask<Void, Void, List<Category>> {

            @Override
            protected List<Category> doInBackground(Void... voids) {
                List<Category> categories = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .getDAO()
                        .getAllCategory();
                return categories;
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onPostExecute(List<Category> categoryList) {
                super.onPostExecute(categoryList);
                if (categoryList.isEmpty()) {
                    llEmptyBox.setVisibility(View.VISIBLE);
                } else {
                    llEmptyBox.setVisibility(View.GONE);
                    textViewCategoryCount.setText("" + categoryList.size());
                    recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
                    recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    categoryAdapater = new CategoryAdapater(getApplicationContext(), categoryList);
                    recyclerViewCategory.setAdapter(categoryAdapater);
                }
            }
        }

        GetCategory getCategory = new GetCategory();
        getCategory.execute();
    }

    // count
    @SuppressLint("SetTextI18n")
    private void getRecyclerViewItemCount() {
        try {

            class GetItem extends AsyncTask<Void, Void, List<ListItem>> {

                @Override
                protected List<ListItem> doInBackground(Void... voids) {
                    List<ListItem> listItems = DatabaseClient
                            .getInstance(getApplicationContext())
                            .getAppDatabase()
                            .getDAO()
                            .getAllItems();
                    return listItems;
                }

                @Override
                protected void onPostExecute(List<ListItem> listItems) {
                    super.onPostExecute(listItems);
                    if (listItems.isEmpty()) {

                    } else {
                        textViewItemCount.setText("" + listItems.size());
                    }
                }
            }

            GetItem getItem = new GetItem();
            getItem.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.category_dialog);
        TextView cancelButton = dialog.findViewById(R.id.buttonClose);
        final Button buttonSaveCategory = dialog.findViewById(R.id.buttonSaveCategory);
        editTextCategoryName = dialog.findViewById(R.id.editTextCategoryName);

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
                dialog.dismiss();
                getCategory();
                Toasty.success(getApplicationContext(), "Category Added", Toasty.LENGTH_LONG).show();
            }
        }
        SaveCategory sc = new SaveCategory();
        sc.execute();
    }

    /**
     * Menu Bottom Navigation Drawer
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.menu_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Bungee.swipeRight(MainActivity.this);
                break;
            case R.id.menu_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                Bungee.swipeRight(MainActivity.this);
                break;
            case R.id.menu_list:
                startActivity(new Intent(getApplicationContext(), AllItemActivity.class));
                Bungee.swipeRight(MainActivity.this);
                break;
            case R.id.menu_category:
                startActivity(new Intent(getApplicationContext(), ManageCategoryActivity.class));
                Bungee.swipeRight(MainActivity.this);
                break;
            case R.id.menu_out_of_stock:
                startActivity(new Intent(getApplicationContext(), OutOfStockActivity.class));
                Bungee.swipeRight(MainActivity.this);
                break;
            case R.id.menu_about:
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_logout:
                dialogExit();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void dialogExit() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // click double tap to exit
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            SnackToa.snackBarError(this, "Tap again to exit");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private void loadUserName() {
        user = new User();

        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mUserName = (firebaseUser.getDisplayName());
                Objects.requireNonNull(getSupportActionBar()).setTitle("Hi " + mUserName);
                getSupportActionBar().setSubtitle("Welcome to Shoppy Guide");
            }
        });
    }
}
