package tw.edu.ntu.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;

import tw.edu.ntu.app.db.CalendarData;
import tw.edu.ntu.app.db.CalendarDatabase;

public class FlowerActivity extends AppCompatActivity {
    private AlertDialog.Builder AlertDialogBuilder;
    private CalendarView calendarView;
    private ListView listView;
    CalendarDataAdapter listViewAdapter;
    ArrayList<CalendarData> listViewElementArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower);
        AlertDialogBuilder = new AlertDialog.Builder(this);
        createNotificationChannel();

        initListViewElement();
        updateListViewElement(Calendar.getInstance());

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setHeaderColor(R.color.buttonpurple);

        calendarView.setOnDayClickListener(eventDay -> {
            updateListViewElement(eventDay.getCalendar());
            Calendar calendar = Calendar.getInstance();
            int nowHour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            int nowMinute = calendar.get(java.util.Calendar.MINUTE);
            new TimePickerDialog(FlowerActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // Get the date which is selected by user and then set the time
                    Calendar dateAndTime = eventDay.getCalendar();
                    dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    dateAndTime.set(Calendar.MINUTE, minute);
                    showUserInputAlert(dateAndTime);
                }
            }, nowHour, nowMinute, false).show();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListViewElement(Calendar.getInstance());
    }

    static private Intent intentGenerator(Context context, Calendar calendar, String userInput) {
        Intent intent = new Intent(context, CalenderAlarmReceiver.class);
        // Using time string to compose unique category
        intent.setAction("CALENDAR_ALARM_RECEIVER");
        intent.addCategory("ID." + String.valueOf(calendar.get(Calendar.MONTH)) + "." + String.valueOf(calendar.get(Calendar.DATE)) + "-" + String.valueOf((calendar.get(Calendar.HOUR_OF_DAY) )) + "." + String.valueOf(calendar.get(Calendar.MINUTE)) + "." + String.valueOf(calendar.get(Calendar.SECOND)));
        intent.putExtra("calendar", calendar);
        intent.putExtra("userInput", userInput);
        return intent;
    }

    static public int addAlarm(Context context, Calendar calendar, String userInput) {
        Intent intent = intentGenerator(context, calendar, userInput);
        int alarmId = SharedPreUtils.getInt(context, "alarm_id", 0);
        SharedPreUtils.setInt(context, "alarm_id", ++alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        // Register the alarm
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "您已設置提醒", Toast.LENGTH_SHORT).show();
        return alarmId;
    }

    static public void cancelAlarm(Context context, CalendarData calendarData) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarData.calendar_time);
        Intent intent = intentGenerator(context, calendar, calendarData.notification_message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, calendarData.alarm_id, intent, 0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "您已取消提醒", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "在設定的日期及時間提醒";
            NotificationChannel channel = new NotificationChannel("FlowerNotification", "提醒事項", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showUserInputAlert(Calendar calendar) {
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected
        AlertDialogBuilder.setView(input);
        AlertDialogBuilder.setTitle("請輸入備忘訊息");
        AlertDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                ViewParent vp = input.getParent();
                if (vp instanceof ViewGroup) {
                    ((ViewGroup) vp).removeView(input);
                }
            }
        });

        // Set up the buttons
        AlertDialogBuilder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString();
                // Add system alarm
                int alarm_id = addAlarm(FlowerActivity.this, calendar, userInput);

                // Add alarm setting data to db
                new Thread(() -> {
                    CalendarDatabase.getInstance(FlowerActivity.this)
                            .calendarDataDao()
                            .insertData(new CalendarData(
                                    alarm_id,
                                    calendar.getTimeInMillis(),
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH), userInput));

                    updateListViewElement(calendar);
                }).start();
            }
        });
        AlertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialogBuilder.show();

    }

    private void updateListViewElement(Calendar calendar) {
        new Thread(() -> {  // Load alarm which be set data from db
            List<CalendarData> calendarDataList = CalendarDatabase.getInstance(FlowerActivity.this)
                    .calendarDataDao()
                    .findByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            // Sort calendarDataList with ascending order by calendar time
            Collections.sort(calendarDataList, (d1, d2) -> {
                return (int) (d1.calendar_time - d2.calendar_time);
            });

            listViewElementArray.clear();
            listViewElementArray.addAll(calendarDataList);

            // This will update the content of ListView
            runOnUiThread(() -> {
                listViewAdapter.notifyDataSetChanged();
            });

        }).start();
    }

    private void initListViewElement() {
        listView = (ListView) findViewById(R.id.listViewNotification);
        listViewElementArray = new ArrayList<CalendarData>();
        listViewAdapter = new CalendarDataAdapter(this, listViewElementArray);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            CalendarData calendarData = listViewElementArray.get(position);

            AlertDialogBuilder.setTitle("是否刪除提醒");
            AlertDialogBuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelAlarm(FlowerActivity.this, calendarData);
                    new Thread(() -> {
                        CalendarDatabase.getInstance(FlowerActivity.this).calendarDataDao().deleteByCalenderTime(calendarData.calendar_time);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(calendarData.calendar_time);
                        updateListViewElement(calendar);
                    }).start();
                }
            });
            AlertDialogBuilder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialogBuilder.show();
        }
    });
    }

}

