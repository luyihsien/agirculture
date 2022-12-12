package tw.edu.ntu.app.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Data.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "Data.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class, DB_NAME).build();
        }
        return instance;
    }


    private static AppDatabase create(final Context context){
        return Room.databaseBuilder(context, AppDatabase.class,DB_NAME).build();
    }


    public abstract DataDao dataDao();

}
