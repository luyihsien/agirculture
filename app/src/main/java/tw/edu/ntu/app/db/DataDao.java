package tw.edu.ntu.app.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import sun.bob.mcalendarview.vo.DateData;


@Dao
public interface DataDao {
    @Query("SELECT * FROM Data")
    List<Data> getAll();

    @Query("SELECT * FROM Data WHERE record_id IN (:recordIds)")
    List<Data> loadAllByIds(int[] recordIds);

    @Query("SELECT * FROM Data WHERE year LIKE :year AND month LIKE :month AND day LIKE :day")
    List<Data> findByDate(int year, int month, int day);

    @Query("SELECT * FROM Data WHERE year LIKE :year AND month LIKE :month")
    List<Data> findByYearMonth(int year, int month);

    @Query("SELECT * FROM Data WHERE relative_id LIKE :relative_id")
    List<Data> findByRelativeID(long relative_id);


    @Update
    void updateData(Data data);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(Data data);


    @Query("DELETE  FROM Data WHERE relative_id LIKE :relative_id")
    void deleteByRelativeID(long relative_id);


}









