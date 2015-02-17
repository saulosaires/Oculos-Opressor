package com.oculosopressor.activity;

import java.util.HashMap;

import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.oculosopressor.R;

import android.app.Application;

public class OpressorApp extends Application {

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-42957667-2";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public OpressorApp() {
        super();
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
    	
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            
            Tracker t =  analytics.newTracker(PROPERTY_ID);
                            
            t.enableAdvertisingIdCollection(true);
            
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
