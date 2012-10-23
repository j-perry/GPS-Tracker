package com.example.testthree;

public class Workout {
	int		_id;
	int		user_id;
	int		start;
	int		stop;
	int		distance;
	
	public Workout( int _id, int user_id, int start, int stop, int distance ){
		this._id = _id;
		this.user_id = user_id;
		this.start = start;
		this.stop = stop;
		this.distance = distance;
		
	}
	
	public int getID(){
		return	this._id;
	}
	
	public void setID( int _id ){
		this._id = _id;
	}

	public int getUserID(){
		return	this.user_id;
	}
	
	public void setUsetID( int user_id ){
		this.user_id = user_id;
	}

	public int getStart(){
		return	this.start;
	}
	
	public void setStart( int start ){
		this.start = start;
	}

	public int getStop(){
		return	this.stop;
	}
	
	public void setStop( int stop ){
		this.stop = stop;
	}

	public int getDistance(){
		return	this.distance;
	}
	
	public void setDistance( int distance ){
		this.distance = distance;
	}
}
