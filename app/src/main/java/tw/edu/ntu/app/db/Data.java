package tw.edu.ntu.app.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import sun.bob.mcalendarview.vo.DateData;

@Entity
public class Data {

        @PrimaryKey(autoGenerate = true)
        public int record_id;

        @ColumnInfo(name = "relative_id")
        public long relative_id;

        @ColumnInfo(name = "year")
        public int year;

        @ColumnInfo(name = "month")
        public int month;

        @ColumnInfo(name = "day")
        public int day;

        @ColumnInfo(name = "fertilize")
        public int fertilize; // 0 ->插秧, 1->第一追肥, 2...

        @ColumnInfo(name = "gdd")
        public int gdd; //

        @ColumnInfo(name = "color")
        public int color;

        @ColumnInfo(name = "type")
        public int type;

        @ColumnInfo(name = "history")
        public boolean history;

        @ColumnInfo(name = "update")
        public boolean update;

        @ColumnInfo(name = "content")
        public String content;


        public Data(long relative_id, int year, int month, int day, int fertilize, int gdd, int color, int type, boolean history, boolean update, String content) {
                this.relative_id = relative_id;
                this.year = year;
                this.month = month;
                this.day = day;
                this.fertilize = fertilize;
                this.gdd = gdd;
                this.color = color;
                this.type = type;
                this.history = history;
                this.update = update;
                this.content = content;
        }

        @Ignore
        public Data(int record_id, long relative_id, int year, int month, int day, int fertilize, int gdd, int color, int type, boolean history, boolean update, String content) {
                this.record_id = record_id;
                this.relative_id = relative_id;
                this.year = year;
                this.month = month;
                this.day = day;
                this.fertilize = fertilize;
                this.gdd = gdd;
                this.color = color;
                this.type = type;
                this.history = history;
                this.update = update;
                this.content = content;
        }
}


