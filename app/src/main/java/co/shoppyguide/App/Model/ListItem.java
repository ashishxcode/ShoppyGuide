package co.shoppyguide.App.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import co.shoppyguide.App.Database.AppDatabase;

@Entity(tableName = AppDatabase.TABLE_NAME_ITEM)
public class ListItem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long ItemId;

    @ColumnInfo
    private Long categoryID;
    @ColumnInfo
    private String itemName;

    @ColumnInfo
    private String itemDescription;

    @ColumnInfo
    private String itemCategory;

    @ColumnInfo
    private String itemQuantity;

    @ColumnInfo
    private String itemConsumption;

    @ColumnInfo
    private Boolean itemOutOfStock;

    @ColumnInfo
    private Date itemAddDate;

    public long getItemId() {
        return ItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public Long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public String getItemConsumption() {
        return itemConsumption;
    }

    public Boolean getItemOutOfStock() {
        return itemOutOfStock;
    }

    public Date getItemAddDate() {
        return itemAddDate;
    }

    public void setItemId(long itemId) {
        ItemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public void setItemConsumption(String itemConsumption) {
        this.itemConsumption = itemConsumption;
    }

    public void setItemOutOfStock(Boolean itemOutOfStock) {
        this.itemOutOfStock = itemOutOfStock;
    }

    public void setItemAddDate(Date itemAddDate) {
        this.itemAddDate = itemAddDate;
    }
}
