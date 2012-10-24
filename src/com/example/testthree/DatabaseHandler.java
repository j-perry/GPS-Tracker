package com.example.testthree;


import java.util.ArrayList;
import java.util.List;

import com.example.testthree.entity.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHandler extends SQLiteOpenHelper {

	public String text;
    // All Static variables
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
    private static final String[] TableUsersColumnNames = { 	"_id", 		"active", 	"fname", 	"sname", 	"email", 	"age", 		"weight", 	"height" };
    private static final String[] TableUsersColumnTypes = { 	"INTEGER", 	"INTEGER", 	"TEXT", 	"TEXT", 	"TEXT", 	"INTEGER", 	"INTEGER", 	"INTEGER" };
    private static final String[] TableUsersColumnKeys  = {		"PRIMARY KEY AUTOINCREMENT", "", "", "", "", "", "", "" };
    	
    // Table Workouts
    private static final String[] TableWorkoutsColumnNames = { 	"_id", 		"user_id", 	"start", 	"stop", 	"distance" };
    private static final String[] TableWorkoutsColumnTypes = { 	"INTEGER", 	"INTEGER", 	"INTEGER", 	"INTEGER", 	"INTEGER" };
    private static final String[] TableWorkoutsColumnKeys  = {	"PRIMARY KEY AUTOINCREMENT", "", "", "", "" };
    	
    // Table Locations
    private static final String[] TableLocationsColumnNames = { "_id", 		"workout_id", 	"time", 	"workoutTime", 	"latitude",	"longtitude", 	"altitude" };
    private static final String[] TableLocationsColumnTypes = { "INTEGER", 	"INTEGER", 		"INTEGER", 	"INTEGER", 		"REAL",		"REAL", 		"REAL" };
    private static final String[] TableLocationsColumnKeys  = {	"PRIMARY KEY AUTOINCREMENT", "", "", "", "", "", "" };
    	
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
 	
 	@Override
	public void onCreate(SQLiteDatabase db) {
 		// creating users table
 		String CREATE_TABLE;

 		CREATE_TABLE = "CREATE TABLE " + TABLE_USERS + " ( ";
		for( int i=0; i<TableUsersColumnNames.length; i++){
			String txt = TableUsersColumnNames[i] + " " + TableUsersColumnTypes[i] + " " + TableUsersColumnKeys[i];
			if( i < TableUsersColumnNames.length-1)
				txt += ", ";
			else
				txt += ");";
			CREATE_TABLE += txt;
		}
		db.execSQL(CREATE_TABLE);

		// creating workouts table
 		CREATE_TABLE = "CREATE TABLE " + TABLE_WORKOUTS + " ( ";
		for( int i=0; i<TableWorkoutsColumnNames.length; i++){
			String txt = TableWorkoutsColumnNames[i] + " " + TableWorkoutsColumnTypes[i] + " " + TableWorkoutsColumnKeys[i];
			if( i < TableWorkoutsColumnNames.length-1)
				txt += ", ";
			else
				txt += ");";
			CREATE_TABLE += txt;
		}
		db.execSQL(CREATE_TABLE);
	
		// creating locations table
 		CREATE_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + " ( ";
		for( int i=0; i<TableLocationsColumnNames.length; i++){
			String txt = TableLocationsColumnNames[i] + " " + TableLocationsColumnTypes[i] + " " + TableLocationsColumnKeys[i];
			if( i < TableLocationsColumnNames.length-1)
				txt += ", ";
			else
				txt += ");";
			CREATE_TABLE += txt;
		}
		db.execSQL(CREATE_TABLE);
}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
 
        // Create tables again
        onCreate(db);
	}

	/* =======================
	 * Adding new user to table users
	 * parameters:
	 * 	user
	 * returns:
	 * 	the row ID of the newly inserted row, or -1 if an error occurred 
	 * 
	 */
	public int addUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put("active", 	USER_NON_ACTIVE);					// set to non-active
        values.put("fname", 	user.getFname());	
        values.put("sname", 	user.getSname());	
        values.put("email", 	user.getEmail());	
        values.put("age", 		user.getAge());	
        values.put("weight",	user.getWeight());	
        values.put("height",	user.getHeight());	
 
        // Inserting Row
        int id = (int)db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection
        return	id;
    }

	public int updateUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put("_id", 		user.getID());	
        values.put("active", 	user.getActive());					// set to non-active
        values.put("fname", 	user.getFname());	
        values.put("sname", 	user.getSname());	
        values.put("email", 	user.getEmail());	
        values.put("age", 		user.getAge());	
        values.put("weight",	user.getWeight());	
        values.put("height",	user.getHeight());	
 
        // Inserting Row
        int id = db.update(TABLE_USERS, values, TableUsersColumnNames[0] + " = ?",
                new String[] { String.valueOf(user.getID()) });
        db.close(); // Closing database connection
        return	id;
    }


	public User getUser(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
	 
		Cursor cursor = db.query(TABLE_USERS, TableUsersColumnNames, TableUsersColumnNames[0] + "=" + String.valueOf(id),
	                null, null, null, null, null );

		if ( !cursor.moveToFirst() )
		{
			db.close();
			return null;
		}

    	 
	    User user = new User(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
	                cursor.getString(2), cursor.getString(3), cursor.getString(4),
	                Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7)));
	        // return user
        db.close(); // Closing database connection
	    return user;
	}
	
	
	public User getActiveUser() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor	cursor;
	 
		cursor = db.query(TABLE_USERS, TableUsersColumnNames, TableUsersColumnNames[1] + "=" + String.valueOf(USER_ACTIVE),
	                null, null, null, null, null );

		if ( !cursor.moveToFirst() )
		{
			db.close();
			return null;
		}
  
	 
	    User user = new User(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
	                cursor.getString(2), cursor.getString(3), cursor.getString(4),
	                Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7)));
	        // return user
        db.close(); // Closing database connection
	    return user;

	}
	
	
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
    	                cursor.getString(2), cursor.getString(3), cursor.getString(4),
    	                Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7)));
                // Adding contact to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return userList;
    }
    
	/* =======================
	 * Making one of the user active (only one can be active at any moment )
	 * parameters:
	 * 	user id
	 * 
	 */
	public void setUserActive( int id ){

		User user = getActiveUser();
		if( user != null ){				/* if active user exist set him non active */
			user.setActive(USER_NON_ACTIVE);
			updateUser(user);
		}
		
		user = getUser(id);
		user.setActive(USER_ACTIVE);
		updateUser(user);
	}
    
    // Deleting single user
    public void deleteUser( int id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, TableUsersColumnNames[0] + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
    
        
}



