package com.team2.dash.entity;

import java.util.*;

public class VenueReview 
{
	
	public Date normalDate;
	public double ReviewRating;
	public int ReviewDateTime;
	public int ReviewPositive;
	public int ReviewNegative;
	public String ReviewText;
	public int DBLocationId;
	public int DBReviewId;	
	public int DBUserId;
	
	
	public String toString()
	{		
		String fullReturn;
		fullReturn = normalDate.toString() + " - Rating: ";
		if(ReviewRating == 0.0)
		{
			fullReturn = fullReturn + "No Rating";
		}
		else
		{
			fullReturn = fullReturn + ReviewRating + "";
		}
		fullReturn = fullReturn +"\n" + ReviewText;
		
		//fullReturn = fullReturn + "\nPositive:" + ReviewPositive;
		//fullReturn = fullReturn + "- Negative:" + ReviewNegative;
		
		return fullReturn;
	}	
	
	public double getReviewRating() 
	{
		return ReviewRating;
	}
	
	public void setReviewRating(double reviewRating) 
	{
		ReviewRating = reviewRating;
	}
	
	public int getReviewDateTime() 
	{
		return ReviewDateTime;
	}
	
	public void setReviewDateTime(int reviewDateTime) 
	{
		ReviewDateTime = reviewDateTime;
		normalDate = new Date((long)ReviewDateTime*1000);		
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
