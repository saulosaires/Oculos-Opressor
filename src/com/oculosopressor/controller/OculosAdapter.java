package com.oculosopressor.controller;

import com.oculosopressor.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class OculosAdapter extends ArrayAdapter<Integer> {

	public OculosAdapter(Context context,  Integer[] objects) {
		super(context, 0, objects);
	 
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

			int image = getItem(position);    
	   
	       if (convertView == null) {
	          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_opressor, parent, false);
	       }
	  
	       ImageView img = (ImageView) convertView.findViewById(R.id.instrumento);
	      
	       img.setBackgroundResource(image);
 
	       return convertView;
		
	}

}
