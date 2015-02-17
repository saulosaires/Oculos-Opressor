package com.oculosopressor.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.oculosopressor.R;
import com.oculosopressor.activity.PreferenceActivity;

public class OculosPreferenceFragment extends PreferenceFragment {
	
	String TAG = getClass().getSimpleName();
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		getPreferenceManager().setSharedPreferencesName(PreferenceActivity.PREFS_NAME);

		addPreferencesFromResource(R.xml.pref_general);
	}

 
 
}
