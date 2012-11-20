package com.team2.dash.entity;

import android.text.format.DateFormat;

public class LocationP {
	
	/**
	 * Private members
	 */
	int		id;				// primary key
	int		workoutId;		// 
	long 	time;			// real time of the reading
	int		workoutTime;	// time since workout started excluding breaks
	double	latitude;		// 
	double 	longtitude;		// 
	double	altitude;		// 
	
	/**
	 * 
	 * @param _id
	 * @param workout_id
	 * @param time
	 * @param workoutTime
	 * @param latitude
	 * @param longtitude
	 * @param altitude
	 */
	public LocationP(int id, int workoutId, long time, int workoutTime, double latitude, double longtitude, double altitude) {
		this.id = id;
		this.workoutId = workoutId;
		this.time = time;
		this.workoutTime = workoutTime;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.altitude = altitude;
	}
	
	/**
	 * @return 
	 */
    @Override
	public String toString() {    	
		String txt = DateFormat.format("dd/MM/yy h:mm:ssaa ", time).toString();
		txt += ", ";
		txt += Integer.toString((int)workoutTime);
		return (txt);
	}
	
    /**
     * 
     * @return
     */
	public int getID() {
		return	this.id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setID(int id ){
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public int getWorkoutID() {
		return this.workoutId;
	}
	
	/**
	 * 
	 * @param workoutId
	 */
	public void setWorkoutID( int workoutId ){
		this.workoutId = workoutId;
	}

	/**
	 * 
	 * @return
	 */
	public long getTime() {
		return	this.time;
	}
	
	/**
	 * 
	 * @param time
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * 
	 * @return
	 */
	public int getWorkoutTime(){
		return	this.workoutTime;
	}
	
	/**
	 * 
	 * @param workoutTime
	 */
	public void setWorkoutTime(int workoutTime) {
		this.workoutTime = workoutTime;
	}

	/**
	 * 
	 * @return
	 */
	public double getLatitude() {
		return	this.latitude;
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
	public double getLongtitude() {
		return this.longtitude;
	}
	
	/**
	 * 
	 * @param longtitude
	 */
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	/**
	 * 
	 * @return
	 */
	public double getAltitude() {
		return this.altitude;
	}
	
	/**
	 * 
	 * @param altitude
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
}
