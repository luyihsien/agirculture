package tw.edu.ntu.app;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class CalenderAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bData = intent.getExtras();
        Calendar calendar = (Calendar) bData.get("calendar");
        String userInput = (String) bData.get("userInput");
        String tag = "Notification time is " + calendar.getTime().toString();
        Log.i("CalenderAlarmReceiver", tag);
        Log.i("CalenderAlarmReceiver", "userInput is " + userInput);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        // The argument of channelId should be the same with the id of the NotificationChannel.
        Notification notification = new NotificationCompat.Builder(context, "FlowerNotification")
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle("提醒")
                .setContentText(userInput)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        // Using tag as tag argument fo notify() to create unique notification tag
        notificationManagerCompat.notify(tag, 0, notification);
    }

}