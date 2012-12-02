package com.team2.dash.entity;

import android.text.format.DateFormat;

public class userReview {
	private int 	reviewId;
	private String	locationName;
	private String  reviewTxt;
	private	long	dateTime;

	
	public userReview( int checkinId, String locationName, String reviewTxt, int dateTime ) {
		this.reviewId = checkinId;
		this.locationName = locationName;
		this.reviewTxt = reviewTxt;
		this.dateTime = dateTime;
	}

	public userReview(){
		
	}
	
	public void setReviewId( int checkinId ){
		this.reviewId = checkinId;
	}
	
	public int getReviewId(){
		return	this.reviewId;
	}
	
	public void setLocationName( String name ){
		this.locationName = name;
	}
	
	public String getLocationName(){
		return this.locationName;
	}
	
	public void setReviewTxt( String name ){
		this.reviewTxt = name;
	}
	
	public String getReviewTxt(){
		return this.reviewTxt;
	}
	
	public void setDateTime( long dateTime ){
		this.dateTime = dateTime;
	}
	
	public long getDateTime(){
		return this.dateTime;
	}

    @Override
	public String toString() {

    	String txt = DateFormat.format("dd/MM/yy h:mm:ssaa ", this.dateTime).toString();
    		
		return( this.locationName + "\nReviewed: " + txt + "\n" + this.reviewTxt );
	}

}
