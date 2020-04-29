package co.shoppyguide.App.Activity.OutOfStock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.List;
import java.util.Objects;

import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Adapter.ItemAdapter;
import co.shoppyguide.App.Adapter.OutOfStockAdapter;
import co.shoppyguide.App.Database.DatabaseClient;
import co.shoppyguide.App.Model.ListItem;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityOutOfStockBinding;
import spencerstudios.com.bungeelib.Bungee;

public class OutOfStockActivity extends AppCompatActivity {

    ActivityOutOfStockBinding binding;
    private OutOfStockAdapter outOfStockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_out_of_stock);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                Bungee.slideLeft(OutOfStockActivity.this);
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
                        .getOutOfStock(String.valueOf(0));
            }

            @Override
            protected void onPostExecute(List<ListItem> listItems) {
                super.onPostExecute(listItems);
                if (listItems.isEmpty()) {
                    binding.llEmptyBox.setVisibility(View.VISIBLE);
                } else {
                    binding.llEmptyBox.setVisibility(View.GONE);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    outOfStockAdapter = new OutOfStockAdapter(getApplicationContext(), listItems);
                    binding.recyclerView.setAdapter(outOfStockAdapter);

                }
            }
        }

        GetItems getItems = new GetItems();
        getItems.execute();
    }
    public void onResume() {
        super.onResume();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(outOfStockAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        Bungee.swipeLeft(OutOfStockActivity.this);
        super.onBackPressed();
    }
}
