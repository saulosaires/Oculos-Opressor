package com.oculosopressor.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.oculosopressor.activity.OpressorApp;
import com.oculosopressor.activity.OpressorApp.TrackerName;
import com.oculosopressor.util.NotificationUtil;

public class AlarmReciever extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		NotificationUtil.notificationApp(context);
		NotificationUtil.schedulerNotification(context);
	}


	
}
