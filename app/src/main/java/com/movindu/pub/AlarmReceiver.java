package com.movindu.pub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.movindu.pub.Activities.Reminder;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BeepUtils.playBeep(context);

        Intent serviceIntent = new Intent(context, MyForegroundService.class);
        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent);
//        }else{
//                Intent activityIntent = new Intent(context, Reminder.class);
//                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(activityIntent);
//        }

            Intent activityIntent = new Intent(context, Reminder.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);

    }
}
