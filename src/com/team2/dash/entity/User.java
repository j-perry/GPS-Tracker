package com.team2.dash.entity;

public class User {

	/**
	 *  Private Members 
	 */
	int		id;			// user ID - _id should be used for primary key in android
	int 	active;		// there will be only one active user in the table when active = 1, otherways active = 0 
	String	firstName;	// first name
	String	secondName;	// second name
	String	email;		// email
	String  password; 	// password
	int		age;		// age
	int		weight;		// weight
	int 	height;		// height
	
	/**
	 * 
	 * @param id
	 * @param active
	 * @param firstName
	 * @param secondName
	 * @param email
	 * @param age
	 * @param weight
	 * @param height
	 */
	public User(int id, int active, String firstName, String secondName, String email, int age, int weight, int height) {
		this.id = id;
		this.active = active;
		this.firstName = firstName;
		this.secondName = secondName;
		this.email = email;
		this.age = age;
		this.weight = weight;
		this.height = height;
	}
	
	/**
	 * 
	 */
	public User() {
		
	}
	
	/**
	 * 
	 */
    @Override
	public String toString() {
		return( this.getFname() + " " + this.getSname() + " " + this.getActive() );
	}
	
    /**
     * 
     * @return
     */
	public int getID() {		
		return id;	
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setID(int id) {		
		this.id = id;	
	}
	
	/**
	 * 
	 * @return
	 */
	public int getActive() {		
		return this.active;	
	}
	
	/**
	 * 
	 * @param active
	 */
	public void setActive(int active) {		
		this.active = active;	
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFname(){		
		return this.firstName;	
	}
	
	/**
	 * 
	 * @param firstName
	 */
	public void setFname(String firstName){
		this.firstName = firstName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSname(){
		return	this.secondName;
	}
	
	/**
	 * 
	 * @param secondName
	 */
	public void setSname(String secondName){
		this.secondName = secondName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEmail(){
		return this.email;
	}
	
	/**
	 * 
	 * @param email
	 */
	public void setEmail(String email){
		this.email = email;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAge(){		
		return this.age;	
	}
	
	/**
	 * 
	 * @param age
	 */
	public void setAge(int age){		
		this.age = age;	
	}
	
	/**
	 * 
	 * @return
	 */
	public int getWeight(){		
		return	this.weight;	
	}
	
	/**
	 * 
	 * @param weight
	 */
	public void setWeight(int weight){		
		this.weight = weight;	
	}
	
	/**
	 * 
	 * @return
	 */
	public int getHeight(){		
		return this.height;	
	}
	
	/**
	 * 
	 * @param height
	 */
	public void setHeight(int height){		
		this.height = height;	
	}
}
