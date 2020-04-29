package co.shoppyguide.App.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import co.shoppyguide.App.Database.AppDatabase;


@Entity(tableName = AppDatabase.TABLE_NAME_CATEGORY)
public class Category implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long categoryId;

    @ColumnInfo
    private String categoryname;

    @ColumnInfo
    private Date categoryAddDate;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public Date getCategoryAddDate() {
        return categoryAddDate;
    }

    public void setCategoryAddDate(Date categoryAddDate) {
        this.categoryAddDate = categoryAddDate;
    }
}
