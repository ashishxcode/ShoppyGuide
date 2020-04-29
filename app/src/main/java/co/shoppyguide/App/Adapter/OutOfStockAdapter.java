package co.shoppyguide.App.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import co.shoppyguide.App.Activity.Item.ViewItemActivity;
import co.shoppyguide.App.Model.ListItem;
import co.shoppyguide.R;

public class OutOfStockAdapter extends RecyclerView.Adapter<OutOfStockAdapter.ItemViewHolder> {

    private Context mCtx;
    private List<ListItem> listItems;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
//
//    public OutOfStockAdapter(Context mCtx, List<ListItem> listItems) {
//        this.mCtx = mCtx;
//        this.listItems = listItems;
//    }
//
//    @NonNull
//    @Override
//    public OutOfStockAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_outofstock, parent, false);
//        return new OutOfStockAdapter.ItemViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull OutOfStockAdapter.ItemViewHolder holder, int position) {
//        ListItem listItem = listItems.get(position);
//        holder.textViewItem.setText(listItem.getItemName());
//        holder.textViewDescription.setText(listItem.getItemDescription());
//        holder.textViewItemCategory.setText(listItem.getItemCategory());
//        holder.textViewItemDate.setText(sdf.format(listItem.getItemAddDate()));
//        holder.mCategoryID = listItem.getCategoryID();
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    public class ItemViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
//        TextView textViewItem, textViewItemDate, textViewItemCategory, textViewDescription;
//        long mCategoryID;
//
//        public ItemViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewItem = itemView.findViewById(R.id.tvItemName);
//            textViewDescription = itemView.findViewById(R.id.tvItemDescription);
//            textViewItemCategory = itemView.findViewById(R.id.tvItemCategory);
//            textViewItemDate = itemView.findViewById(R.id.tvItemDate);
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            ListItem listItem =  listItems.get(getAdapterPosition());
//            Intent intent = new Intent(mCtx, ViewItemActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("listItem", listItem);
//            mCtx.startActivity(intent);
//        }
//    }

    public OutOfStockAdapter(Context mCtx, List<ListItem> listItems) {
        this.mCtx = mCtx;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public OutOfStockAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_outofstock, parent, false);
        return new OutOfStockAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutOfStockAdapter.ItemViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        holder.textViewItem.setText(listItem.getItemName());
        holder.textViewDescription.setText(listItem.getItemDescription());
        holder.textViewItemCategory.setText(listItem.getItemCategory());
        holder.textViewItemDate.setText(sdf.format(listItem.getItemAddDate()));
        holder.mCategoryID = listItem.getCategoryID();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewItem, textViewItemDate, textViewItemCategory, textViewDescription;
        long mCategoryID;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.tvItemName);
            textViewDescription = itemView.findViewById(R.id.tvItemDescription);
            textViewItemCategory = itemView.findViewById(R.id.tvItemCategory);
            textViewItemDate = itemView.findViewById(R.id.tvItemDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ListItem listItem = listItems.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, ViewItemActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("listItem", listItem);
            mCtx.startActivity(intent);
        }
    }
}
