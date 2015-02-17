package com.oculosopressor.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.oculosopressor.R;
import com.oculosopressor.activity.OpressorApp.TrackerName;
import com.oculosopressor.controller.OculosAdapter;
import com.oculosopressor.fragment.FragmentOpressor;
import com.oculosopressor.util.NotificationUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends ActionBarActivity {

    private static final String FRAGMENT_INSTANCE = "FRAGMENT_INSTANCE";
    private static final String INSTRUMENTO_POS = "INSTRUMENTO_POS";
    
    
	public static final int SELECT_PHOTO = 100;
	FragmentOpressor opressor;
	private static Uri selectedImage;
	private FrameLayout mContainer;
	private Fragment currentFragment = null;
 
	private static final String FILE_TYPE = "image/*";
 	File storageDir;
	private int instrumentoPos=1;
	Dialog dialog;
	Integer[] imgs=new Integer[3];
	
	String screen_initial="MainActivity";
	
	ProgressDialog progressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		
		
		NotificationUtil.schedulerNotification(this);
		
	    analyticsSend(screen_initial);
				
		imgs[0]=R.drawable.oculos_opressor_direita;
		imgs[1]=R.drawable.deal;
		imgs[2]=R.drawable.oculos_opressor_esquerda;
		
        configFrameLayout();
        
        configActionBar(getSupportActionBar());
       
		storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), File.separator+ getString(R.string.app_name)	);
        
	//	List<File> list = FileUtils.getListFiles(storageDir);
	//	System.out.println(list);

        if (savedInstanceState != null) {

            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_INSTANCE);
            instrumentoPos= savedInstanceState.getInt(INSTRUMENTO_POS);
            
          

        }
	}


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
      
        if(currentFragment!=null){
        	
        	  if(currentFragment instanceof FragmentOpressor)
        		  opressor = (FragmentOpressor) currentFragment;
        	
        	 replaceFragment(currentFragment);
        }else{
        	
            opressor= new FragmentOpressor();
            replaceFragment(opressor);
 
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (getSupportFragmentManager() != null && currentFragment != null) {

            getSupportFragmentManager().putFragment(outState,FRAGMENT_INSTANCE,currentFragment);
        }

        outState.putInt(INSTRUMENTO_POS, instrumentoPos);
        
        super.onSaveInstanceState(outState);
    }
    
    
    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, null);
    }

    public void replaceFragment(Fragment fragment, int transaction, Fragment from) {

        FragmentManager fragmentManager = getSupportFragmentManager();

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

        currentFragment = fragment;
        replace.commitAllowingStateLoss();
    }

	
    private void configActionBar(ActionBar actionBar) {

	        actionBar.setTitle("");
	        actionBar.setCustomView(R.layout.actionbar_custom);
	        actionBar.setDisplayShowCustomEnabled(true);

	    }

	   private void configFrameLayout() {
	        mContainer = (FrameLayout) findViewById(R.id.container);
	    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		
		switch(id){
		
			case R.id.add_glass:{
			
				analyticsSend("add_glass");
				
				initInstrumentoOpressor();
				
				break;
			}
		
			case R.id.photo:{
				
				analyticsSend("photo");
				
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
			}
				
			case R.id.save_opression:{
  
				analyticsSend("save_opression");
				
				String file = saveToInternalSorage(getBitmap(opressor.getViewBitMap()));
				 
				showAlertDialog(getString(R.string.picture_saved_msg)+file);
				
				//Toast.makeText(this, getString(R.string.picture_saved_msg)+file,Toast.LENGTH_LONG).show();
				
				break;	
			}

			case R.id.gallery_opression:{

				analyticsSend("gallery_opression");
				
                openGalery();
                break;
			}
			case R.id.configuration:{

			        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		          
		            startActivity(new Intent(this, PreferenceActivity.class));
                break;
			}			
			
			
			
		}
	 
		return super.onOptionsItemSelected(item);
	}
	
	public void showAlertDialog(String msg){
		
		Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(msg);
		dialog.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Do stuff if user accepts
            }
        }).setNegativeButton(android.R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Do stuff when user neglects.
            }
        }).setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                // Do stuff when cancelled
            }
        }).create();
		dialog.show();
		
	}
	
	public void initInstrumentoOpressor(){
		
		ListView listView = new ListView(this);

		
		OculosAdapter adapter = new OculosAdapter(this,imgs);
		
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setItemChecked(instrumentoPos,true);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				System.out.println(position);
				
				if(opressor!=null){
				   opressor.setInstrumentoOpressor(imgs[position]);
				}else{
					Toast.makeText(MainActivity.this, getString(R.string.shit_happens), Toast.LENGTH_LONG).show();
				}
				dialog.dismiss();
			}
		});
		
		dialog = new Dialog(this);
		dialog.setContentView(listView);
		dialog.setTitle(getString(R.string.add_glass));
		dialog.show();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data); 

	    switch(requestCode) { 
	    
	    case SELECT_PHOTO:
	        if(resultCode == RESULT_OK){  
	        	
	        	if(data==null)return;
	        	
	            selectedImage = data.getData();
	            
	        	if(selectedImage==null)return;
	            selectPicture(selectedImage);
 	        }
	    }		
	}
 
	private void selectPicture(Uri selectedImage){

        try {
        	
        	
//        	Bitmap selectedImageBitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream(selectedImage));
//        	opressor.setImagemOprimido(selectedImageBitmap);

        	opressor.setImagemOprimido(selectedImage);
        	
		}catch (Exception e) {
			Toast.makeText(this, getString(R.string.shit_happens), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		
	}
		
	
	private String saveToInternalSorage(Bitmap bitmapImage){

 
//				progressDialog=ProgressDialog.show(MainActivity.this, "", "kjhjk", true, false);
	 

		
		storageDir.mkdirs();
        File mypath=new File(storageDir,"opressao"+new SimpleDateFormat("dd.MM.yyyy.hhss",Locale.getDefault()).format(new Date())+".jpg");
        String path="";
        
        FileOutputStream fos = null;
        try {           

            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            
            path = mypath.getCanonicalPath();
            ContentValues values = new ContentValues();

            values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA,path);

            getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
            
//            runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//					 progressDialog.dismiss();	
//				}
//			});
            
           
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return path;
    }	
	
	public  Uri getSelectedImage() {
		
		return selectedImage;
	}

	private Bitmap getBitmap(View v) {
	    v.clearFocus();
	    v.setPressed(false);

	    boolean willNotCache = v.willNotCacheDrawing();
	    v.setWillNotCacheDrawing(false);

	    // Reset the drawing cache background color to fully transparent
	    // for the duration of this operation
	    int color = v.getDrawingCacheBackgroundColor();
	    v.setDrawingCacheBackgroundColor(0);

	    if (color != 0) {
	        v.destroyDrawingCache();
	    }
	    v.buildDrawingCache();
	    Bitmap cacheBitmap = v.getDrawingCache();
	    if (cacheBitmap == null) {
	        Toast.makeText(this,getString(R.string.shit_happens),Toast.LENGTH_SHORT).show();
	        return null;
	    }

	    Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

	    // Restore the view
	    v.destroyDrawingCache();
	    v.setWillNotCacheDrawing(willNotCache);
	    v.setDrawingCacheBackgroundColor(color);

	    int width = opressor.getEsquerdaCaviar().getWidth();
	    int height = opressor.getEsquerdaCaviar().getHeight();
	    
	    if(width==0 || height==0){
	    	Toast.makeText(this,getString(R.string.shit_happens),Toast.LENGTH_SHORT).show();
	        return null;	
	    }
	    
	    int[] locationEsquerdaCaviar=new int[2];
	    opressor.getEsquerdaCaviar().getLocationOnScreen(locationEsquerdaCaviar);
		    

	    int[] locationRelativeLayout=new int[2];
	    opressor.getViewBitMap().getLocationOnScreen(locationRelativeLayout);
 	    
	    return Bitmap.createBitmap(bitmap,locationEsquerdaCaviar[0],locationEsquerdaCaviar[1]-locationRelativeLayout[1], width, height);
	}	

	public void openGalery(){
		
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(storageDir),FILE_TYPE);
		startActivity(intent);

		
	}
	
	public void analyticsSend(String screenName){
		
		Tracker t = ((OpressorApp) getApplication()).getTracker(TrackerName.GLOBAL_TRACKER);
        t.setScreenName(screenName);
        t.send(new HitBuilders.AppViewBuilder().build());
        
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
	       return true;
	   }
	   return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	   if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
	       openOptionsMenu();
	       return true;
	   }
	   return super.onKeyUp(keyCode, event);
	}


 

}
