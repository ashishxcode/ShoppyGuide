package co.shoppyguide.App.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import co.shoppyguide.App.Model.Category;
import co.shoppyguide.App.Model.ListItem;

@Dao
public interface DAO {

    /* Category Model */
    @Query("SELECT * FROM " + AppDatabase.TABLE_NAME_CATEGORY + " ORDER BY categoryAddDate DESC ")
    List<Category> getAllCategory();

    @Insert
    void insertCategory(Category categoryList);

    @Query("DELETE FROM " + AppDatabase.TABLE_NAME_CATEGORY + " WHERE categoryID = :mCateogryID")
    void deleteCategory(String mCateogryID);

    @Update
    void updateCategory(Category categoryList);


    @Query("SELECT * FROM " + AppDatabase.TABLE_NAME_ITEM + " WHERE categoryID = :mCateogryID ")
    List<ListItem> getItemByCategory(String mCateogryID);

    @Query("SELECT * FROM " + AppDatabase.TABLE_NAME_ITEM + " ORDER BY itemAddDate DESC ")
    List<ListItem> getAllItems();

    @Query("SELECT * FROM " + AppDatabase.TABLE_NAME_ITEM + " WHERE itemQuantity= :mItemQuantity ")
    List<ListItem> getOutOfStock(String mItemQuantity);

    @Insert
    void insertListItem(ListItem listItem);

    @Query("DELETE FROM " + AppDatabase.TABLE_NAME_ITEM + " WHERE categoryID = :mCateogryID")
    void deleteItemByCategory(String mCateogryID);

    @Delete
    void deleteListItem(ListItem listItem);

    @Update
    void updateListItem(ListItem listItem);



}
