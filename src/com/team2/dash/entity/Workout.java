package com.team2.dash.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Workout implements Parcelable {
	int		_id;
	int		user_id;
	int		start;
	int		stop;
	int		distance;
	String	description;
	
	public Workout(Parcel in){
		_id = in.readInt();
		user_id = in.readInt();
		start = in.readInt();
		stop = in.readInt();
		distance = in.readInt();
		description = in.readString();
	}
	
	public Workout( int _id, int user_id, int start, int stop, int distance, String description ){		
		this._id = _id;
		this.user_id = user_id;
		this.start = start;
		this.stop = stop;
		this.distance = distance;
		this.description = description;
	}
	
	public Workout(){
		
	}
	
    @Override
	public String toString(){
		return getDescription();
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
	
	public String getDescription(){
		return	this.description;
	}
	
	public void setDescription( String description ){
		this.description = description;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeInt(user_id);
		dest.writeInt(start);
		dest.writeInt(stop);
		dest.writeInt(distance);
		dest.writeString(description);
		
	}
	
	public static final Parcelable.Creator<Workout> CREATOR = new Creator<Workout>() {
		public Workout[] newArray(int size){
			return new Workout[size];
		}
		
		public Workout createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Workout(source);
		}
	};
	
}
