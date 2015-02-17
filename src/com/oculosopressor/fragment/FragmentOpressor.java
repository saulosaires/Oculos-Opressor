package com.oculosopressor.fragment;
 
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.oculosopressor.R;
import com.oculosopressor.activity.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FragmentOpressor extends Fragment implements  View.OnTouchListener, OnSeekBarChangeListener, Callback{

 
	private View rootView;
	private int _xDelta;
	private int _yDelta;
	RelativeLayout relativeLayout;
	private ImageView esquerdaCaviar;
	private ImageView instrumentopressor;
	private SeekBar seekBarTamanho;
	private SeekBar seekBarGirar;
	
	float width;
	float height;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_main, container,false);
		
		init();
		
		return rootView;
	}
  
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		ViewTreeObserver observer = relativeLayout.getViewTreeObserver();
	    observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

	        @Override
	        public void onGlobalLayout() {

	        		//String.format("new width=%d; new height=%d", relativeLayout.getWidth(),relativeLayout.getHeight());
	                    
	            relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	            init();
	        }
	    });
		
	}
	 
	 
	public View getViewBitMap(){
		
		return relativeLayout;
	}
	
	public ImageView getEsquerdaCaviar(){
		return esquerdaCaviar;
	}

	public void init(){
		
	    // Consultar o AdView como um recurso e carregar uma solicitação.
	    AdView adView = (AdView)rootView.findViewById(R.id.topopressor).findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
		
		relativeLayout = (RelativeLayout) rootView.findViewById(R.id.drag_layer);;
		
	    esquerdaCaviar = (ImageView) relativeLayout.findViewById(R.id.esquerdaCaviar);
	    instrumentopressor= (ImageView) relativeLayout.findViewById (R.id.instrumentopressor);
	    
	    seekBarTamanho =(SeekBar) rootView.findViewById(R.id.topopressor).findViewById(R.id.seekBar_containerTamanho).findViewById (R.id.seekBarTamanho);
	    seekBarGirar   =(SeekBar) rootView.findViewById(R.id.topopressor).findViewById(R.id.seekBar_containerGirar).findViewById (R.id.seekBarGirar);
	    
	    seekBarTamanho.setProgress(100);
	    seekBarGirar.setProgress(50);
	    
		width =instrumentopressor.getLayoutParams().width;
		height = instrumentopressor.getLayoutParams().height;

	    instrumentopressor.setOnTouchListener(this);
		instrumentopressor.setImageResource(R.drawable.deal);
		
	    seekBarTamanho.setOnSeekBarChangeListener(this);
	    seekBarGirar.setOnSeekBarChangeListener(this);
 
		Uri picture = ((MainActivity) getActivity()).getSelectedImage();
	    
		if(picture!=null){
  		        setImagemOprimido(picture);
	    }
		
	    
	    
	}
  
 	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		final int X = (int) event.getRawX();
		final int Y = (int) event.getRawY();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			_xDelta = X - lParams.leftMargin;
			_yDelta = Y - lParams.topMargin;
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			layoutParams.leftMargin = X - _xDelta;
			layoutParams.topMargin = Y - _yDelta;
			layoutParams.rightMargin = -250;
			layoutParams.bottomMargin = -250;
			view.setLayoutParams(layoutParams);
			break;
		}
		relativeLayout.invalidate();
		return true;
	}

	public void setInstrumentoOpressor(int img){
		instrumentopressor.setImageResource(img);
	}
 
	public void setImagemOprimido(final Uri picture) {

    	
    	Picasso.with(getActivity()).load(picture).placeholder(R.drawable.icon).into(getEsquerdaCaviar(),this);
		
 		 
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		
		if(seekBar.equals(seekBarGirar)){

			instrumentopressor.setRotation((float) (1.8*progress-90));
			
		}else if(seekBar.equals(seekBarTamanho)){

			float fprogress=progress;

			
			fprogress=fprogress/100;
			float mwidth=width*fprogress;
			float mheight=height*fprogress;
	 
			//System.out.println("height: "+height+" width:"+width+" mwidth:"+mwidth+"  height:"+mheight+" fprogress:"+fprogress);
			
			instrumentopressor.getLayoutParams().height=(int) mheight;
			instrumentopressor.getLayoutParams().width=(int) mwidth;
			instrumentopressor.requestLayout();
		}

		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess() {
 
		
		relativeLayout.requestLayout();
		relativeLayout.invalidate();
 		
		BitmapDrawable drawable = (BitmapDrawable) esquerdaCaviar.getDrawable();
		Bitmap bitmap = drawable.getBitmap();
		
	 	
		
		float ratioImg = ((float)bitmap.getWidth())/ ((float)bitmap.getHeight());
		float ratioContainer= ((float)relativeLayout.getWidth())/ ((float)relativeLayout.getHeight());
		
		int width=0;
		int height=0;
		
		
		if (ratioImg >= ratioContainer) {
		    // Image is wider than the display (ratio)
		     width = relativeLayout.getWidth();
		     height = (int)(width / ratioImg);
		     
		} else {
		    // Image is taller than the display (ratio)
		     height = relativeLayout.getHeight();
		     width = (int)(height * ratioImg);
		    
		} 	
		
		
		esquerdaCaviar.getLayoutParams().width=width;
		esquerdaCaviar.getLayoutParams().height=height;
		
//		esquerdaCaviar.destroyDrawingCache();
//		esquerdaCaviar.setImageBitmap(yourSelectedImage);
			
		
		
	}
 

}
