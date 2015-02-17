package com.oculosopressor.activity;
 
 
 
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
 
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.oculosopressor.R;
import com.oculosopressor.fragment.OculosPreferenceFragment;


public class PreferenceActivity extends ActionBarActivity {

	String TAG = getClass().getSimpleName();
  
	public static String PREFS_NAME="Preference";
	private FrameLayout mContainer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(getString(R.string.configuration));
 
		setContentView(R.layout.activity_main);
		 mContainer = (FrameLayout) findViewById(R.id.container);
 
		replaceFragment(new OculosPreferenceFragment());
	  
		 
		 
		 
	}
	
	   public void replaceFragment(Fragment fragment) {
	        replaceFragment(fragment, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, null);
	    }

	    public void replaceFragment(Fragment fragment, int transaction, Fragment from) {

	        FragmentManager fragmentManager = getFragmentManager();

	        FragmentTransaction replace = fragmentManager
	                .beginTransaction()
	                .setTransition(transaction);

	        if (from != null) {

	   
	            replace.add(mContainer.getId(), fragment, fragment.getClass().getName());
	            replace.addToBackStack(fragment.getClass().getName());
	            replace.hide(from);

	        } else {

	            try {
	                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }

	            replace.replace(mContainer.getId(), fragment, fragment.getClass().getName());
	        }

 
	        replace.commitAllowingStateLoss();
	    }
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
 	
		switch (item.getItemId()) {

			case android.R.id.home: {
	
				//startActivity(new Intent(getApplicationContext(),SlideMenuActivity.class));
				onBackPressed();
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
}
