package com.movindu.pub;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.movindu.pub.Activities.Reminder;

public class MyForegroundService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent activityIntent = new Intent(getApplicationContext(), Reminder.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);

        // Stop the service after starting the activity
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a notification for the foreground service
//        Notification notification = new NotificationCompat.Builder(this, "CHANNEL_ID")
//                .setContentTitle("Starting Activity")
//                .setContentText("Foreground service is running")
//                //.setSmallIcon(R.drawable.ic_notification)
//                .build();
//        startForeground(1, notification);
    }
}

