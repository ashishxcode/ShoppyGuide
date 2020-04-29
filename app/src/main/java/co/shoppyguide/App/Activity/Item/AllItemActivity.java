package co.shoppyguide.App.Activity.Item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import java.util.List;
import java.util.Objects;

import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Adapter.ItemAdapter;
import co.shoppyguide.App.Database.DatabaseClient;
import co.shoppyguide.App.Model.ListItem;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityAllItemBinding;
import spencerstudios.com.bungeelib.Bungee;

public class AllItemActivity extends AppCompatActivity {

    ActivityAllItemBinding binding;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_item);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                Bungee.slideLeft(AllItemActivity.this);
            }
        });

        getItems();
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
                        .getAllItems();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        Bungee.swipeLeft(AllItemActivity.this);
        super.onBackPressed();
    }
}
