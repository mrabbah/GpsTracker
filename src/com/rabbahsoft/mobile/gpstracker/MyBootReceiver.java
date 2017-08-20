package com.rabbahsoft.mobile.gpstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBootReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("info", intent.getAction());
		Intent gpsTrackerIntent = new Intent(context, GpsReadingService.class);
		PendingIntent pendingGpsIntent = PendingIntent.getService(context, 1, gpsTrackerIntent, 0);
        AlarmManager gpsAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        gpsAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                1 * 1000, CommonUtils.ALARM_MANAGER_INTERVAL, pendingGpsIntent);
		Log.i("info", "Starting gps services at boot time");
        //context.startService(i); 
        /*
        Intent dataTransfertIntent = new Intent(context, DataTransfertService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 2, dataTransfertIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                13 * 1000, CommonUtils.ALARM_MANAGER_INTERVAL, pendingIntent);
        
        Log.i("info", "Starting data transfert services at boot time");
        */
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


	}

}
