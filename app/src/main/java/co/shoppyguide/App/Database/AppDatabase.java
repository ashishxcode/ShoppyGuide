package co.shoppyguide.App.Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Model.Category;
import co.shoppyguide.App.Model.ListItem;

@Database(entities = {Category.class, ListItem.class}, version = 1, exportSchema = false)
@TypeConverters({co.shoppyguide.App.Database.TypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "app_db";
    public static final String TABLE_NAME_ITEM = "items";
    public static final String TABLE_NAME_CATEGORY = "category";
    public abstract DAO getDAO();
    private static AppDatabase INSTANCE;
    public static AppDatabase getDb(MainActivity context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "lists")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
