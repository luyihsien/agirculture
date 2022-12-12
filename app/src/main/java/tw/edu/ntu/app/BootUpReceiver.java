package tw.edu.ntu.app;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.edu.ntu.app.db.CalendarData;
import tw.edu.ntu.app.db.CalendarDatabase;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            new Thread(() -> {
                List<CalendarData> calendarDataList = CalendarDatabase.getInstance(context)
                        .calendarDataDao()
                        .findGreaterThanCalendarTime(Calendar.getInstance().getTimeInMillis());

                for (CalendarData calendarData: calendarDataList) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(calendarData.calendar_time);
                    FlowerActivity.addAlarm(context, calendar, calendarData.notification_message);
                }

            }).start();
        }

    }
}
