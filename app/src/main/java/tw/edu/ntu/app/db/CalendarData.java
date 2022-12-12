package tw.edu.ntu.app.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class CalendarData {

    @PrimaryKey(autoGenerate = true)
    public int record_id;

    @ColumnInfo(name = "alarm_id")
    public int alarm_id;

    @ColumnInfo(name = "calendar_time")
    public long calendar_time;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "month")
    public int month;

    @ColumnInfo(name = "day")
    public int day;

    @ColumnInfo(name = "notification_message")
    public String notification_message;

    public CalendarData(int alarm_id, long calendar_time, int year, int month, int day, String notification_message) {
        this.alarm_id = alarm_id;
        this.calendar_time = calendar_time;
        this.year = year;
        this.month = month;
        this.day = day;
        this.notification_message = notification_message;
    }

}


