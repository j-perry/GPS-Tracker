package com.team2.dash.entity;

public class VenueInfo 
{
	public String FourSquareId;
	public String VenueName;
	public String VenueAddress;
	public double Latitude;
	public double Longitude;
	public int FourSquareCheckins;
	
	public String toString()
	{
		return VenueName + "\n" + VenueAddress;
	}
	
	public String getFourSquareId() {
		return FourSquareId;
	}
	public void setFourSquareId(String fourSquareId) {
		FourSquareId = fourSquareId;
	}
	public String getVenueName() {
		return VenueName;
	}
	public void setVenueName(String venueName) {
		VenueName = venueName;
	}
	public String getVenueAddress() {
		return VenueAddress;
	}
	public void setVenueAddress(String venueAddress) {
		VenueAddress = venueAddress;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}
	public int getFourSquareCheckins() {
		return FourSquareCheckins;
	}
	public void setFourSquareCheckins(int fourSquareCheckins) {
		FourSquareCheckins = fourSquareCheckins;
	}	
}
