package com.team2.dash.entity;

import android.text.format.DateFormat;

public class userCheckin {
	
	private int 	checkinId;
	private String	locationName;
	private	long	dateTime;

	
	public userCheckin( int checkinId, String locationName, int dateTime ) {
		this.checkinId = checkinId;
		this.locationName = locationName;
		this.dateTime = dateTime;
	}

	public userCheckin(){
		
	}
	
	public void setCheckinId( int checkinId ){
		this.checkinId = checkinId;
	}
	
	public int getCheckinId(){
		return	this.checkinId;
	}
	
	public void setLocationName( String name ){
		this.locationName = name;
	}
	
	public String getLocationName(){
		return this.locationName;
	}
	
	public void setDateTime( long dateTime ){
		this.dateTime = dateTime;
	}
	
	public long getDateTime(){
		return this.dateTime;
	}

    @Override
	public String toString() {

    	String txt = DateFormat.format("dd/MM/yy kk:mm:ss ", this.dateTime).toString();
    		
		return( this.locationName + "\nChecked in: " + txt );
	}

}
