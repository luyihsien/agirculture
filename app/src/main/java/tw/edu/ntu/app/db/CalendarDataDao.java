package tw.edu.ntu.app.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface CalendarDataDao {
    @Query("SELECT * FROM CalendarData")
    List<CalendarData> getAll();

    @Query("SELECT * FROM CalendarData WHERE calendar_time >= :calendar_time")
    List<CalendarData> findGreaterThanCalendarTime(long calendar_time);

    @Query("SELECT * FROM CalendarData WHERE year LIKE :year AND month LIKE :month AND day LIKE :day")
    List<CalendarData> findByDate(int year, int month, int day);

    @Update
    void updateData(CalendarData data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(CalendarData data);

    @Query("DELETE  FROM CalendarData WHERE calendar_time LIKE :calendar_time")
    void deleteByCalenderTime(long calendar_time);

}









