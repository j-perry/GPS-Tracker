package com.team2.dash;

import java.util.ArrayList;
import java.util.List;

import com.team2.dash.entity.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	/**
	 * 	Private members
	 */
	public String text;		// 
	
    /**
     * All Static variables
     */
	
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "GPSTracker";
 
    // Names of tables
    private static final String TABLE_USERS = "users";
    private static final String TABLE_WORKOUTS = "workouts";
    private static final String TABLE_LOCATIONS = "locations";
    
    // 
    static final int USER_ACTIVE = 		1;
    static final int USER_NON_ACTIVE = 	0;
    
    // Table Users
    private static final String[] TableUsersColumnNames = { 	"_id", 		"active", 	"fname", 	"sname", 	"email", 	"password",	"age", 		"weight", 	"height", "serverUserID" };
    private static final String[] TableUsersColumnTypes = { 	"INTEGER", 	"INTEGER", 	"TEXT", 	"TEXT", 	"TEXT", 	"TEXT",		"INTEGER", 	"INTEGER", 	"INTEGER", "INTEGER" };
    private static final String[] TableUsersColumnKeys  = {		"PRIMARY KEY AUTOINCREMENT", "", "", "", "UNIQUE", "", "", "", "", "" };
    	
    // Table Workouts
    private static final String[] TableWorkoutsColumnNames = { 	"_id", 		"user_id", 	"start", 	"stop", 	"distance",	"description" };
    private static final String[] TableWorkoutsColumnTypes = { 	"INTEGER", 	"INTEGER", 	"INTEGER", 	"INTEGER", 	"INTEGER", 	"TEXT" };
    private static final String[] TableWorkoutsColumnKeys  = {	"PRIMARY KEY AUTOINCREMENT", "", "", "", "", "" };
    	
    // Table Locations
    private static final String[] TableLocationsColumnNames = { "_id", 		"workout_id", 	"time", 	"workoutTime", 	"latitude",	"longtitude", 	"altitude" };
    private static final String[] TableLocationsColumnTypes = { "INTEGER", 	"INTEGER", 		"INTEGER", 	"INTEGER", 		"REAL",		"REAL", 		"REAL" };
    private static final String[] TableLocationsColumnKeys  = {	"PRIMARY KEY AUTOINCREMENT", "", "", "", "", "", "" };
    	
    /**
     * 
     * @param context
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
 	/**
 	 * Creates all tables in the database
 	 */
 	@Override
	public void onCreate(SQLiteDatabase db) {
 		// creating users table
 		String CREATE_TABLE;

 		CREATE_TABLE = "CREATE TABLE " + TABLE_USERS + " ( ";
		for( int i = 0; i < TableUsersColumnNames.length; i++) {
			String txt = TableUsersColumnNames[i] + " " + TableUsersColumnTypes[i] + " " + TableUsersColumnKeys[i];
			
			if( i < TableUsersColumnNames.length-1) {
				txt += ", ";
			}
			else {
				txt += ");";
			}
			
			CREATE_TABLE += txt;
		}
		
		try{
			db.execSQL(CREATE_TABLE);			
		}
		catch(SQLiteException e)
		{
			Log.e("SQL Error, ", "", e);
		}

		// creating workouts table
 		CREATE_TABLE = "CREATE TABLE " + TABLE_WORKOUTS + " ( ";
 		
		for( int i = 0; i < TableWorkoutsColumnNames.length; i++) {
			String txt = TableWorkoutsColumnNames[i] + " " + TableWorkoutsColumnTypes[i] + " " + TableWorkoutsColumnKeys[i];
			
			if( i < TableWorkoutsColumnNames.length-1) {
				txt += ", ";
			}
			else {
				txt += ");";
			}
			CREATE_TABLE += txt;
		}
		
		db.execSQL(CREATE_TABLE);
	
		// creating locations table
 		CREATE_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + " ( ";
 		
		for( int i = 0; i < TableLocationsColumnNames.length; i++) {
			String txt = TableLocationsColumnNames[i] + " " + TableLocationsColumnTypes[i] + " " + TableLocationsColumnKeys[i];
			
			if( i < TableLocationsColumnNames.length - 1) {
				txt += ", ";
			}
			else {
				txt += ");";
			}
			
			CREATE_TABLE += txt;
		}
		
		db.execSQL(CREATE_TABLE);
}

 	/**
 	 * 
 	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
 
        // Create tables again
        onCreate(db);
	}
	
	/**
	 * Adds new user to table users
	 * @param user
	 * @return the row ID of the newly inserted row, or -1 if an error occurred 
	 */
	public int addUser(User user) {
		int id = 0;
 
        ContentValues values = new ContentValues();
        values.put("active", 	USER_NON_ACTIVE);					// set to non-active
        values.put("fname", 	user.getFname());	
        values.put("sname", 	user.getSname());	
        values.put("email", 	user.getEmail());	
        values.put("password", 	user.getPassword());	
        values.put("age", 		user.getAge());	
        values.put("weight",	user.getWeight());	
        values.put("height",	user.getHeight());	
        values.put("serverUserID",	user.getServerUserID());	
 

        // Inserting Row
        try{
        	SQLiteDatabase db = this.getWritableDatabase();
        	id = (int) db.insert(TABLE_USERS, null, values);        
        	db.close(); // Closing database connection
        }
		catch(SQLiteException e)
		{
			Log.e("SQL Error, ", "", e);
		}
        
        return id;
    }

	/**
	 * 
	 * @param user
	 * @return
	 */
	public int updateUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put("_id", 		user.getID());	
        values.put("active", 	user.getActive());					
        values.put("fname", 	user.getFname());	
        values.put("sname", 	user.getSname());	
        values.put("email", 	user.getEmail());	
        values.put("password", 	user.getPassword());	
        values.put("age", 		user.getAge());	
        values.put("weight",	user.getWeight());	
        values.put("height",	user.getHeight());	
        values.put("serverUserID",	user.getServerUserID());	
 
        // Inserting Row
        int id = db.update(TABLE_USERS, values, TableUsersColumnNames[0] + " = ?",
                new String[] { String.valueOf(user.getID()) });
        
        db.close(); // Closing database connection
        
        return	id;
    }

	/**
	 * 
	 * @param id
	 * @return
	 */
	public User getUserByEmail(String email) {
		SQLiteDatabase 	db = null;
		Cursor			cursor = null;
		
        try{
        	db = this.getReadableDatabase();
	 
        	cursor = db.query(TABLE_USERS, TableUsersColumnNames, "email='" + email + "'",
	                null, null, null, null, null );
        
        	if( cursor != null )
        		if ( !cursor.moveToFirst() ) {
        			db.close();
        			return null;
        		}
        }
		catch(SQLiteException e)
		{
			Log.e("SQL Error, ", "", e);
		}
 

	    User user = new User(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
	                cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
	                Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7)), 
	                Integer.parseInt(cursor.getString(8)), Integer.parseInt(cursor.getString(9)));
	    
	    // return user
        db.close(); // Closing database connection
        
	    return user;
	}
	
	public User getUser(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
	 
		Cursor cursor = db.query(TABLE_USERS, TableUsersColumnNames, TableUsersColumnNames[0] + "=" + String.valueOf(id),
	                null, null, null, null, null );

		if ( !cursor.moveToFirst() ) {
			db.close();
			return null;
		}

	    User user = new User(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
	                cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
	                Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7)), 
	                Integer.parseInt(cursor.getString(8)), Integer.parseInt(cursor.getString(9)));
	    
	    // return user
        db.close(); // Closing database connection
        
	    return user;
	}
	
	/**
	 * 
	 * @return
	 */
	public User getActiveUser() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor	cursor;
	 
		cursor = db.query(TABLE_USERS, TableUsersColumnNames, TableUsersColumnNames[1] + "=" + String.valueOf(USER_ACTIVE),
	                null, null, null, null, null );

		if ( !cursor.moveToFirst() ) {
			db.close();
			return null;
		}
  	 
	    User user = new User(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
                Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7)), 
                Integer.parseInt(cursor.getString(8)), Integer.parseInt(cursor.getString(9)));
	    
	    // return user
        db.close(); // Closing database connection
	    
        return user;

	}
	
	/**
	 * 
	 * @return
	 */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
        	    User user = new User(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
                        Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7)), 
                        Integer.parseInt(cursor.getString(8)), Integer.parseInt(cursor.getString(9)));
                // Adding contact to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return userList;
    }
    	    
    /**
     * Makes one of the user active (only one can be active at any moment)
     * @param id
     */
	public void setUserActive(int id) {
		User user = getActiveUser();
		
		if( user != null ) { /* if active user exist set him non active */	
			user.setActive(USER_NON_ACTIVE);
			updateUser(user);
		}
		
		user = getUser(id);
		user.setActive(USER_ACTIVE);
		updateUser(user);
	}
        
	/**
	 * Deletes single user
	 * @param id
	 */
    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, TableUsersColumnNames[0] + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
    
    /**
     * Adds new workout
     * @param workout
     * @return
     */
	public int addWorkout(Workout workout) {
		SQLiteDatabase db = this.getWritableDatabase();
 
		ContentValues values = new ContentValues();
        values.put("user_id", 		workout.getUserID());		
        values.put("start", 		workout.getStart());
        values.put("stop", 			workout.getStop());
        values.put("distance", 		workout.getStop());
        values.put("description", 	workout.getDescription());
         
        // Inserting Row
        int id = (int)db.insert(TABLE_WORKOUTS, null, values);
        db.close(); // Closing database connection
        return id;
    }
	
	/**
	 * Deletes workout with given workout ID
	 * @param id
	 */
    public void deleteWorkout( int id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORKOUTS, TableUsersColumnNames[0] + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
	
    /**
     * 
     * @param workout
     * @return
     */
	public int updateWorkout(Workout workout) {
		SQLiteDatabase db = this.getWritableDatabase(); 

		ContentValues values = new ContentValues();
        values.put("_id", 			workout.getID());		
        values.put("user_id", 		workout.getUserID());		
        values.put("start", 		workout.getStart());
        values.put("stop", 			workout.getStop());
        values.put("distance", 		workout.getStop());
        values.put("description", 	workout.getDescription());
         
        // Updating Row
        int id = db.update(TABLE_WORKOUTS, values, "_id = ?",
                new String[] { String.valueOf(workout.getID()) });
        db.close(); // Closing database connection
        return id;
    }
	
    /**
     * 
     * @param userID
     * @return
     */
    public List<Workout> getAllWorkouts(int userID) {
        List<Workout> workoutList = new ArrayList<Workout>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WORKOUTS + " WHERE user_id=" + userID;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
        	    Workout workout = new Workout(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
        	    		Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), 
        	    		Integer.parseInt(cursor.getString(4)), cursor.getString(5));
                // Adding contact to list
                workoutList.add(workout);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return workoutList;
    }
    
    /**
     * 
     * @param workoutID
     * @return
     */
    public List<LocationP> getAllLocations( int workoutID ) {
        List<LocationP> locationList = new ArrayList<LocationP>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " WHERE workout_id=" + workoutID;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
        	    LocationP location = new LocationP(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
        	    		Long.parseLong(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), 
    	                Double.parseDouble(cursor.getString(4)), Double.parseDouble(cursor.getString(5)), 
    	                Double.parseDouble(cursor.getString(6)));
                // Adding contact to list
                locationList.add(location);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return locationList;
    }
    
    /**
     * 
     * @param location
     * @return
     */
	public int addLocation(LocationP location) {
		SQLiteDatabase db = this.getWritableDatabase();
 
		ContentValues values = new ContentValues();
        values.put("workout_id", 	location.getWorkoutID());		
        values.put("time", 			location.getTime());
        values.put("workoutTime", 	location.getWorkoutTime());
        values.put("latitude", 		location.getLatitude());
        values.put("longtitude", 	location.getLongtitude());
        values.put("altitude", 		location.getAltitude());
         
        // Inserting Row
        int id = (int) db.insert(TABLE_LOCATIONS, null, values);
        
        // Closing database connection
        db.close();
        
        return id;
    }
	
	/**
	 * 
	 * @param id
	 */
    public void deleteLocation( int id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, TableUsersColumnNames[0] + " = ?",
                new String[] { String.valueOf(id) });
        
        db.close();
    }
}



