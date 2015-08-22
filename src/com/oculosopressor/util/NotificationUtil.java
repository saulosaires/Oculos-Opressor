package com.oculosopressor.util;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.oculosopressor.R;
import com.oculosopressor.activity.MainActivity;
import com.oculosopressor.controller.AlarmReciever;

 
public class NotificationUtil {

	static int[] hours  = {9,12,18,20};  
							
	
	
	
	public static int  notificationIdApp=1;
	
	public static void notificationApp(Context context){
		
	    int icon = R.drawable.icon;
	    
		Intent notificationIntent = new Intent(context,MainActivity.class);
	    
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

 
		
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
															        .setSmallIcon(icon)
															        .setContentTitle(context.getString(R.string.app_name))
															        .setContentIntent(intent)
															        .setContentText(context.getString(R.string.describe_notification))
															        .setAutoCancel(true);

           
    	NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(notificationIdApp, mBuilder.build());
	}

	
	public static void schedulerNotification(Context context){	
					
		
		
		boolean enable_notification = SharedPreferences.getBoolean(context, "enable_notification",true);
		
		if(!enable_notification)return;
		
		 Calendar cal = Calendar.getInstance();  
		 cal.add(Calendar.DATE, 1);
		 cal.set(Calendar.HOUR_OF_DAY, hours[(int) (Math.random()%3)]); 
		 
 
		 Intent intentAlarm = new Intent(context, AlarmReciever.class);
	       
         // create the object
         AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

         //set the alarm for particular time
         alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), PendingIntent.getBroadcast(context,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		
	}
 
 
}
