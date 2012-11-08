package com.team2.dash.entity;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;
/**
 * A class to hold the runtime latitude and longitude of the phone when a user is running. This class exist so has to have a consistent way of passing 
 * locations between the MainActivity and the RouteMap Activity
 * @author ndy40
 *
 */
public class LocationPoint implements Parcelable {
	
	/**
	 *  Private Members 
	 */
	private double 	longitude;
	private double 	latitude;
	private Time   	timeAtPosition;
	private boolean isStartLocation;
	private boolean isFinishLocation;
	private String 	description;	
	
	/**
	 * Default constructor for comfort
	 */
	public LocationPoint() {
		
	}
	
	/**
	 * 
	 * @param in
	 */
	public LocationPoint(Parcel in) {
		longitude = in.readDouble();
		latitude = in.readDouble();

		//create a time object and get the value in milliseconds and then convert to Time object via Time.Set method
		Time tmp = new Time();
		tmp.set(in.readLong());
		timeAtPosition = tmp;
		//I read the boolean values as integer since I didn't want to create a boolean array for just one element
		isStartLocation = (in.readInt() == 1);
		isFinishLocation = (in.readInt() == 1); 
		description = in.readString();
		
	}
	
	/**
	 * This class is used to hold the position of the phone/runner at during movement. Always ensure you set the start and finish location carefully.
	 * E.g a location cannot or shouldn't be both the start and finish on a regular day. 
	 * @param longitude  
	 * @param latitude
	 * @param time
	 * @param isStartLocation  this indicates that the locationpoint is the starting point of the race
	 * @param isFinishLocation this indicates that the locationpoint is the finish point of the race. This is when the user stops moving.
	 * 
	 */
	public LocationPoint(double longitude, double latitude, Time time, boolean isStartLocation, boolean isFinishLocation,String description){
		this.longitude = longitude;
		this.latitude = latitude;
		this.timeAtPosition = time;
		this.isStartLocation = isStartLocation;
		this.isFinishLocation = isFinishLocation;
		this.description = description;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * 
	 * @param longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * 
	 * @param latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public Time getTimeAtPosition() {
		return timeAtPosition;
	}
	
	/**
	 * 
	 * @param timeAtPosition
	 */
	public void setTimeAtPosition(Time timeAtPosition) {
		this.timeAtPosition = timeAtPosition;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isStartLocation() {
		return isStartLocation;
	}
	
	/**
	 * 
	 * @param isStartLocation
	 */
	public void setStartLocation(boolean isStartLocation) {
		this.isStartLocation = isStartLocation;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isFinishLocation() {
		return isFinishLocation;
	}
	
	/**
	 * 
	 * @param isFinishLocation
	 */
	public void setFinishLocation(boolean isFinishLocation) {
		this.isFinishLocation = isFinishLocation;
	}
	
	/**
	 * 
	 */
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 
	 * @return 
	 * @return 
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeLong(timeAtPosition.toMillis(false));
		dest.writeInt(isStartLocation ? 0 : 1);
		dest.writeInt(isFinishLocation?0:1);	
		dest.writeString(description);
	}
	
	/**
	 * 
	 */
	public static final Parcelable.Creator<LocationPoint> CREATOR = new Creator<LocationPoint>() {
		
		/**
		 * 
		 */
		public LocationPoint[] newArray(int size) {
			// TODO Auto-generated method stub
			return new LocationPoint[size];
		}
		
		/**
		 * 
		 */
		public LocationPoint createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new LocationPoint(source);
		}	
	};	
}
