package tw.edu.ntu.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tw.edu.ntu.app.db.CalendarData;

public class CalendarDataAdapter extends ArrayAdapter<CalendarData> {
    public CalendarDataAdapter(Activity context, ArrayList<CalendarData> calendarData) {
        super(context, 0, calendarData);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
        }

        CalendarData currentData = getItem(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentData.calendar_time));

        TextView txtView = (TextView) view.findViewById(R.id.textView);
        txtView.setText("時間：" + calendar.get(Calendar.HOUR_OF_DAY)
                + ":"
                + calendar.get(Calendar.MINUTE)
                + "  訊息："
                + currentData.notification_message);

        return view;
    }
}
