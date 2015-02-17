package com.oculosopressor.util;

import com.oculosopressor.activity.PreferenceActivity;

import android.content.Context;

public class SharedPreferences {
 
	 

    public static boolean getBoolean(Context context,String name,boolean defValue){
  	 
	     if(context!=null)    	 
	    	 return context.getSharedPreferences(PreferenceActivity.PREFS_NAME,Context.MODE_PRIVATE).getBoolean(name, defValue);
	     else
	    	 return false;
	    	 
    }

    public static boolean putValue(Context context,String key,String value){
     
	 	if(context==null) return false;
	 	
	 	  return context.getSharedPreferences(PreferenceActivity.PREFS_NAME,Context.MODE_PRIVATE)
	     .edit()
	     .putString(key, value)
	     .commit();
 	
 	
    }

 
    
    
}
