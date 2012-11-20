package com.team2.dash.entity;

public class LocationReview 
{
	public int DBReviewId;
	public int DBLocationId;
	public int DBUserId;
	public String DBUserName;
	public int ReviewDateTime;
	public String ReviewText;
	
	public int getDBReviewId() {
		return DBReviewId;
	}
	public void setDBReviewId(int dBReviewId) {
		DBReviewId = dBReviewId;
	}
	public int getDBLocationId() {
		return DBLocationId;
	}
	public void setDBLocationId(int dBLocationId) {
		DBLocationId = dBLocationId;
	}
	public int getDBUserId() {
		return DBUserId;
	}
	public void setDBUserId(int dBUserId) {
		DBUserId = dBUserId;
	}
	public String getDBUserName() {
		return DBUserName;
	}
	public void setDBUserName(String dBUserName) {
		DBUserName = dBUserName;
	}
	public int getReviewDateTime() {
		return ReviewDateTime;
	}
	public void setReviewDateTime(int reviewDateTime) {
		ReviewDateTime = reviewDateTime;
	}
	public String getReviewText() {
		return ReviewText;
	}
	public void setReviewText(String reviewText) {
		ReviewText = reviewText;
	}
}
