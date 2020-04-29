package co.shoppyguide.App.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import co.shoppyguide.App.Activity.Item.ItemActivity;
import co.shoppyguide.App.Model.Category;
import co.shoppyguide.R;
import spencerstudios.com.bungeelib.Bungee;

public class CategoryAdapater extends RecyclerView.Adapter<CategoryAdapater.TasksViewHolder> {

    private Context mCtx;
    private List<Category> categoryList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    public CategoryAdapater(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_category, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        Category t = categoryList.get(position);
        holder.textViewCategory.setText(t.getCategoryname());
        holder.textViewCategoryDate.setText(sdf.format(t.getCategoryAddDate()));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewCategory, textViewCategoryDate;

        public TasksViewHolder(View itemView) {
            super(itemView);

            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewCategoryDate = itemView.findViewById(R.id.textViewCategoryDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Category category = categoryList.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, ItemActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
            intent.putExtra("category", category);
            mCtx.startActivity(intent);
        }
    }
}