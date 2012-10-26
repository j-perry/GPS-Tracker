package com.team2.dash.entity;

public class User {

	int		_id;		// user ID - _id should be used for primary key in android
	int 	active;		// there will be only one active user in the table when active = 1, otherways active = 0 
	String	fname;		// first name
	String	sname;		// second name
	String	email;		// email
	String  password; 
	int		age;		// age
	int		weight;		// weight
	int 	height;		// height
	
	public User( int _id, int active, String fname, String sname, String email, int age, int weight, int height){
		this._id = _id;
		this.active = active;
		this.fname = fname;
		this.sname = sname;
		this.email = email;
		this.age = age;
		this.weight = weight;
		this.height = height;
	}
	
	public User(){
		
	}
	
    @Override
	public String toString(){
		return( this.getFname() + " " + this.getSname() + " " + this.getActive() );
	}
	
	public int getID(){		
		return	_id;	
	}
	
	public void setID(int _id){		
		this._id = _id;	
	}
	
	public int getActive(){		
		return	this.active;	
	}
	
	public void setActive(int active){		
		this.active = active;	
	}
	
	public String getFname(){		
		return	this.fname;	
	}
	
	public void setFname(String fname){
		this.fname = fname;
	}
	
	public String getSname(){
		return	this.sname;
	}
	
	public void setSname(String sname){
		this.sname = sname;
	}
	
	public String getEmail(){
		return	this.email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public int getAge(){		
		return	this.age;	
	}
	
	public void setAge(int age){		
		this.age = age;	
	}
	
	public int getWeight(){		
		return	this.weight;	
	}
	
	public void setWeight(int weight){		
		this.weight = weight;	
	}
	
	public int getHeight(){		
		return	this.height;	
	}
	
	public void setHeight(int height){		
		this.height = height;	
	}
	
}
