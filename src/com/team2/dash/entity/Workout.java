package com.team2.dash.entity;

public class Workout {
	
	/**
	 * Private members
	 */
	int		id;				// 
	int		userId;			// 
	int		start;			// 
	int		stop;			// 
	int		distance;		// 
	String	description;	// 
	
	/**
	 * 
	 * @param id
	 * @param userId
	 * @param start
	 * @param stop
	 * @param distance
	 * @param description
	 */
	public Workout(int id, int userId, int start, int stop, int distance, String description) {		
		this.id = id;
		this.userId = userId;
		this.start = start;
		this.stop = stop;
		this.distance = distance;
		this.description = description;
	}
	
	/**
	 * 
	 */
	public Workout() {
		
	}
	
	/**
	 * 
	 */
    @Override
	public String toString() {
		return getDescription();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setID(int id){
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public int getUserID(){
		return	this.userId;
	}
	
	/**
	 * 
	 * @param userId
	 */
	public void setUsetID(int userId){
		this.userId = userId;
	}

	/**
	 * 
	 * @return
	 */
	public int getStart() {
		return this.start;
	}
	
	/**
	 * 
	 * @param start
	 */
	public void setStart(int start){
		this.start = start;
	}

	/**
	 * 
	 * @return
	 */
	public int getStop(){
		return this.stop;
	}
	
	/**
	 * 
	 * @param stop
	 */
	public void setStop( int stop ){
		this.stop = stop;
	}

	/**
	 * 
	 * @return
	 */
	public int getDistance() {
		return this.distance;
	}
	
	/**
	 * 
	 * @param distance
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description){
		this.description = description;
	}
}
