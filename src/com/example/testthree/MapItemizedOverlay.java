package com.example.testthree;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapItemizedOverlay extends ItemizedOverlay{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
	
	public MapItemizedOverlay(Drawable marker){
		super(boundCenterBottom(marker));
	}
	
	public MapItemizedOverlay(Drawable marker, Context context){
		super(boundCenterBottom(marker));
		this.context = context;
	}
	
	public void addOverlay(OverlayItem item){
		mOverlays.add(item);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);		
	}
	

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		// TODO Auto-generated method stub
		return super.onTap(index);
	}

}
