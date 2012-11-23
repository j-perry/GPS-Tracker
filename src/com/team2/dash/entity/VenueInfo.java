package com.team2.dash.entity;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class VenueInfo implements Parcelable
{
	public String venueAddress;
	public String venueName;
	public double latitude;
	public double longitude;
	public String id;
	public List<VenueReview> reviews;
	
	public VenueInfo()
	{
		//Empty constructor
	}
	
	public VenueInfo(Parcel in)
	{
		this.id = in.readString();
		this.venueName = in.readString();
		this.venueAddress = in.readString();
		this.latitude = in.readDouble();
		this.longitude = in.readDouble();
		this.reviews = new ArrayList<VenueReview>();
		in.readList(this.reviews, null);
	}	
	
	public String toString()
	{
		return venueName + "\n" + venueAddress + "\nRating:";
	}
	
	public String getId() 
	{
		return id;
	}
	
	public void setId(String id) 
	{
		this.id = id;
	}
	
	public String getVenueName() 
	{
		return venueName;
	}
	
	public void setVenueName(String venueName) 
	{
		this.venueName = venueName;
	}
	
	public String getVenueAddress() 
	{
		return venueAddress;
	}
	
	public void setVenueAddress(String venueAddress) 
	{
		this.venueAddress = venueAddress;
	}
	
	public double getLatitude() 
	{
		return latitude;
	}
	
	public void setLatitude(double latitude) 
	{
		this.latitude = latitude;
	}
	
	public double getLongitude() 
	{
		return longitude;
	}
	
	public void setLongitude(double longitude) 
	{
		this.longitude = longitude;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(id);
		dest.writeString(venueName);
		dest.writeString(venueAddress);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeList(reviews);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() 
	{
        public VenueInfo createFromParcel(Parcel in)
        {
            return new VenueInfo(in);
        }

		public VenueInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new VenueInfo[size];
		} 
    };	
}
