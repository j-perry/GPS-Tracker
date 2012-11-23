package com.team2.dash.entity;

public class VenueReview 
{
	
	public double ReviewRating;
	public int ReviewDateTime;
	public int ReviewPositive;
	public int ReviewNegative;
	public String ReviewText;
	public int DBLocationId;
	public int DBReviewId;	
	public int DBUserId;
	
	public double getReviewRating() 
	{
		return ReviewRating;
	}
	
	public void setReviewRating(double reviewRating) 
	{
		ReviewRating = reviewRating;
	}
	
	public int getReviewDateTime() {
		return ReviewDateTime;
	}
	
	public void setReviewDateTime(int reviewDateTime) 
	{
		ReviewDateTime = reviewDateTime;
	}
	
	public int getReviewPositive() 
	{		
		return ReviewPositive;
	}
	
	public void setReviewPositive(int reviewPositive) 
	{
		ReviewPositive = reviewPositive;
	}
	
	public int getReviewNegative() 
	{
		return ReviewNegative;
	}
	
	public void setReviewNegative(int reviewNegative) 
	{
		ReviewNegative = reviewNegative;
	}
	
	public String getReviewText() 
	{
		return ReviewText;
	}
	
	public void setReviewText(String reviewText) 
	{
		ReviewText = reviewText;
	}
	
	public int getDBLocationId() 
	{
		return DBLocationId;
	}
	
	public void setDBLocationId(int dBLocationId) 
	{
		DBLocationId = dBLocationId;
	}
	
	public int getDBReviewId() 
	{
		return DBReviewId;
	}
	
	public void setDBReviewId(int dBReviewId) 
	{
		DBReviewId = dBReviewId;
	}
		
	public int getDBUserId() 
	{
		return DBUserId;
	}
	
	public void setDBUserId(int dBUserId) 
	{
		DBUserId = dBUserId;
	}

}
