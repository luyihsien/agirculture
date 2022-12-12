package tw.edu.ntu.app.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CalendarData.class}, version = 1, exportSchema = false)
public abstract class CalendarDatabase extends RoomDatabase {

    public static final String DB_NAME = "CalenderData.db";
    private static volatile CalendarDatabase instance;

    public static synchronized CalendarDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    CalendarDatabase.class, DB_NAME).build();
        }
        return instance;
    }

    public abstract CalendarDataDao calendarDataDao();

}
