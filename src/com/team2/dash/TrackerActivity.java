package com.team2.dash;

import com.google.android.maps.GeoPoint;
import com.team2.dash.entity.LocationP;
import com.team2.dash.entity.User;
import com.team2.dash.entity.Workout;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


public class TrackerActivity  extends Activity implements LocationListener {

	private LocationManager 	locationManager = null;			// manages the user's location
	private Location 			location;						// stores the user's location
	private Handler 			handleTimer, handleChrono;		// handles the workout time
	private long 				startTime, elapsedTime;			// stores workout data
//	private boolean stopChrono = false;
	private boolean 			terminateCount = false;			// determines whether ...
	
	private int 				runTimerCount = 0;				// 
	private String				currentTime, provider;			// 
    private int					activeUserID;					// stores the user's ID
    private GeoPoint			gp1, gp2;						// store's geo points 1 + 2
    private Button				startBtn,						// start button  
    							pauseBtn, 						// pause button
    							endBtn, 						// end button
    							mapBtn;							// map button

	private	Workout				workout = null; 				// active workout, null if not in use
    private DatabaseHandler 	db;								// writes data to the database

	// actual status of activity
    // WAIT  - waiting for start of workout
	// START - during workout with GPS locked
	// PAUSE - pause (when pause button pressed or GPS signal lost)
	// STOP  - end of workout pressed
	private enum		tStatus { WAIT, PAUSE, START, STOP };	// determines whether the state of the workout
	private tStatus 	trackerStatus;

	private double 		latitude;								// stores the ... latitude
	private double 		longitude;								// ... longitude
	private double 		altitude;								// ... altitude
	private double 		distance;								// ... distance
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        
        trackerStatus = tStatus.WAIT;

        db = new DatabaseHandler(this);

        startBtn = (Button) findViewById(R.id.button1);		// start
        pauseBtn = (Button) findViewById(R.id.button2);		// pause
        endBtn = (Button) findViewById(R.id.button3);   	// end
        mapBtn = (Button) findViewById(R.id.button4);   	// results 
        
        // hide the stop button (for performance)
        endBtn.setVisibility(View.GONE);
        
        // hide the results button until the workout has finished
        mapBtn.setVisibility(View.GONE);

        //ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        //progressBar.setVisibility(ProgressBar.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        	activeUserID = extras.getInt("activeUserID");
        else
        {
        	User user = db.getActiveUser();
        	activeUserID = user.getID();
        }
        
        /***********************************************
         * Disable start, end, pause, results buttons
         */
        
        // start
        startBtn.setEnabled(false);
        
        // end
        endBtn.setEnabled(false);
        
        // pause
        pauseBtn.setEnabled(false);
        
        // results (map)
        mapBtn.setEnabled(false);

        
        
        // check GPS is enabled
        CheckForGPSEnabled();

        handleTimer = new Handler();
        handleTimer.removeCallbacks(runUpdateTimerTask);
        handleTimer.postDelayed(runUpdateTimerTask, 100);
        handleChrono = new Handler();
        runOnUiThread(UpdateGPSOnUI);
    }

    @Override
    public void onResume() {
        super.onResume();
       	CheckForGPSEnabled();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        trackerStatus = tStatus.WAIT;
            
    }

    @Override
    protected void onPause() 
    {
      super.onPause();
      locationManager.removeUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tracker, menu);
        return true;
    }

	public void onLocationChanged(Location location) {
    	latitude = location.getLatitude();
    	longitude = location.getLongitude();
    	altitude = location.getAltitude();		
	}

	public void onProviderDisabled(String provider) {
	      Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
	}

	public void onProviderEnabled(String provider) {
	      Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();	
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Method event handler updates the workout timer
	 */
    private Runnable runUpdateTimerTask = new Runnable() 
    {
        public void run() {      
        	runOnUiThread(UpdateTimeOnUI);  
        	if(terminateCount == true)
        		runTimerCount++;
        	
        	if(runTimerCount > 25)
        		handleTimer.removeCallbacksAndMessages(runUpdateTimerTask);
        	else 
        		handleTimer.postDelayed(this, 1000);	        		
        	
        }
    };   
         
    /**
     * Method event handler updates the current time on the UI
     */
    private final Runnable UpdateTimeOnUI = new Runnable() 
    {        
    	public void run() {
    		UpdateTime();
//        	if( (workout != null) && (stopChrono == false) && (location != null) )
            if( (trackerStatus == tStatus.START) && (location != null) )
        		SaveLocation();
        }
    };
    
    /**
     * Method event handler updates the GPS on the UI
     */
    private final Runnable UpdateGPSOnUI = new Runnable() 
    {        
    	public void run() {
    		UpdateGPS();
        }
    };            

    /**
     * Method updates the current time (hh:mm:ss)
     */
    private void UpdateTime()
    {
		TextView t = (TextView)findViewById(R.id.textTime);
		currentTime = DateFormat.format("hh:mm:ss ", new java.util.Date()).toString();
		t.setText(currentTime);
    }
     
    /**
     * Method saves the user's current location
     */
    private void SaveLocation() {	
    	float[]	dist = new float[3];
    	LocationP loc = new LocationP( 0, workout.getID(), System.currentTimeMillis(), (int)elapsedTime, latitude, longitude, altitude );
    	db.addLocation(loc);
    	gp2 = gp1;
		gp1 = new GeoPoint((int)(latitude*1E6),(int)(longitude*1E6));		
		if( gp2 != null )
		{
			
			Location.distanceBetween(gp1.getLatitudeE6()/1E6 , gp1.getLongitudeE6()/1E6, 
									gp2.getLatitudeE6()/1E6, gp2.getLongitudeE6()/1E6, dist);
			distance += dist[0];
		}
		TextView t = (TextView)findViewById(R.id.textDistance);
		String	txt = "Distance : " + String.valueOf((int)distance);
		t.setText(txt);

    }
    
    /**
     * Updates the User's GPS location if enabled
     */
    private void UpdateGPS()
    {
    	String		gpsStatus;
    	Criteria hdCrit = new Criteria();
    	hdCrit.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(hdCrit, false);        
        location = locationManager.getLastKnownLocation(provider);
        if (location != null) 
        {
//          System.out.println("Provider " + provider + " has been selected.");
        	onLocationChanged(location);
        	gpsStatus = "GPS locked";
        	switch( trackerStatus )
        	{
        		case	WAIT:
                    startBtn.setEnabled(true);            
                    pauseBtn.setEnabled(false);            
                    endBtn.setEnabled(false);
                    break;
        		case	START:
                    startBtn.setEnabled(false);            
                    pauseBtn.setEnabled(true);            
                    endBtn.setEnabled(true);
                    break;
        		case	STOP:
                    startBtn.setEnabled(false);            
                    pauseBtn.setEnabled(false);            
                    endBtn.setEnabled(false);
                    break;
        		case	PAUSE:
                    startBtn.setEnabled(true);            
                    pauseBtn.setEnabled(false);            
                    endBtn.setEnabled(true);
                    break;
        	}     	
        } 
        else 
        {
        	switch( trackerStatus )
        	{
        		case	WAIT:
                    startBtn.setEnabled(false);            
                    pauseBtn.setEnabled(false);            
                    endBtn.setEnabled(false);
                    break;
        		case	START:
                    startBtn.setEnabled(false);            
                    pauseBtn.setEnabled(true);            
                    endBtn.setEnabled(true);
                    break;
        		case	STOP:
                    startBtn.setEnabled(false);            
                    pauseBtn.setEnabled(false);            
                    endBtn.setEnabled(false);
                    break;
        		case	PAUSE:
                    startBtn.setEnabled(false);            
                    pauseBtn.setEnabled(false);            
                    endBtn.setEnabled(true);
                    break;
        	}     	
        	gpsStatus = "No GPS Available";
//            button_start.setEnabled(false);            
//            button_pause.setEnabled(false);            
//            button_end.setEnabled(false);
            Toast.makeText(this, gpsStatus, Toast.LENGTH_LONG).show();
        }
        gpsStatus = "NO GPS";
        
     	((TextView)findViewById(R.id.textGPS)).setText(gpsStatus);
    	
    }        
    
    /**
     * Checks GPS is enabled
     */
    private void CheckForGPSEnabled()
    {    	
    	locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);    	
        boolean enabled = false;
        try
        {        	
        	enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        	//enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);        	
        }
        catch (Exception e)
        {
        	Log.e("Class", "Here!");
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Error");
			alertDialog.setMessage("Error with GPS Location.\n" + e.getMessage());
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int arg1) 
				{
					dialog.cancel();
					return;
	            }
			});						
			alertDialog.show();
        }

		// Check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to 
		// go to the settings
		if (enabled == false) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("GPS Disabled");
			alertDialog.setMessage("Location services are currently disabled\nGo to Settings to activate them.");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Location Services", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int arg1) 
				{
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
	            }
			});						
			alertDialog.show();
		} 
    }

     
     /**
      * Updates the timer
      * @param time
      */
     private void updateTimer (float time){
 		long secs = (long)(time/1000);
 		long mins = (long)((time/1000)/60);
 		long hrs = (long)(((time/1000)/60)/60);
 		//
 		String hours, minutes, seconds, milliseconds;

 		seconds = String.valueOf(secs % 60);
     	if(secs == 0)
     		seconds = "00";
     	if(secs <10 && secs > 0)
     		seconds = "0" + seconds;
     	
 		minutes = String.valueOf(mins % 60);
     	if(mins == 0)
     		minutes = "00";
     	if(mins <10 && mins > 0)
     		minutes = "0" + minutes;

     	hours = String.valueOf(hrs);
     	if(hrs == 0)
     		hours = "00";
     	if(hrs <10 && hrs > 0)
     		hours = "0" + hours;
     	
     	milliseconds = String.valueOf((long)time);
     	if(milliseconds.length() == 2)
     		milliseconds = "0"+ milliseconds;
     	else if(milliseconds.length() <= 1)
     		milliseconds = "00";
     	else 
     		milliseconds = milliseconds.substring(milliseconds.length()- 3 , milliseconds.length()-1);
 		    
    
 		((TextView)findViewById(R.id.textWorkoutTime)).setText(hours + ":" + minutes + ":" + seconds + "." + milliseconds);		
 	}    

     /**
      * Updates the timer from the beginning of the workout
      */
     private Runnable startTimer = new Runnable() 
     {
    	 public void run() {
    		 elapsedTime = System.currentTimeMillis() - startTime;
    		 updateTimer(elapsedTime);
    		 handleChrono.postDelayed(this, 100);
    	 }
     };    

     /**
      * Starts a new workout
      */
     private void startNewWorkout(){
    	 workout = new Workout( 0, activeUserID, 0, 0, 0, DateFormat.format("dd/MM/yy hh:mm", new java.util.Date()).toString() );
    	 int id = db.addWorkout(workout);
      	 workout.setID( id );
      	 distance = 0;
      	 gp1 = null;

      	 String txt = "New workout started\n" + workout;
    	 Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
   	 
     }
     
     /**
      * User starts a new workout
      * @param view
      */
     public void onStartClick(View view)
     {
    	/*
     	if(stopChrono == true)
     	{    	
     		startTime = System.currentTimeMillis() - elapsedTime;
     	}
     	else
     	{
     		startTime = System.currentTimeMillis();
     		startNewWorkout();
     		
     	}
     	*/

    	 switch( trackerStatus )
    	 {
    	 	case PAUSE:
    	 		startTime = System.currentTimeMillis() - elapsedTime;
    	 		break;
    	 	case WAIT:
    	 		startTime = System.currentTimeMillis();
    	 		startNewWorkout();
    	 		break;
    	 }

      	if( workout != null )
      		trackerStatus = tStatus.START;
      	
     	handleChrono.removeCallbacks(startTimer);
     	handleChrono.postDelayed(startTimer, 0);
        //ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        //progressBar.setVisibility(ProgressBar.VISIBLE);
     	
     	
     	
     	/*************************************
     	 * Configure the state of the buttons
     	 * when new workout has begun
     	 */
     	
     	// disable the start button
        startBtn.setEnabled(false);
        
        // display the pause button
        pauseBtn.setVisibility(View.VISIBLE);
        
        // enable the pause button
        pauseBtn.setEnabled(true);
        
        // hide the end button
        endBtn.setVisibility(View.GONE);
        
        // disable the end button
        endBtn.setEnabled(false);    	
     }

     /**
      * When the workout has been paused
      * @param view
      */
     public void onPauseClick(View view)
     {       
    	 // stop the start timer
     	 handleChrono.removeCallbacks(startTimer);
//     	stopChrono = true;
     	
     	// tracker status is paused
     	trackerStatus = tStatus.PAUSE;
     	
     	// enable the start button
        startBtn.setEnabled(true);            
        
        // change textual state of start button text to RESUME
        startBtn.setText("RESUME");       
        
        // disable the pause button
        pauseBtn.setEnabled(false);
        
        // hide the pause button
        pauseBtn.setVisibility(View.GONE);
        
        // display the end button
        endBtn.setVisibility(View.VISIBLE);
        
        // enable the end button
        endBtn.setEnabled(true);
        
        //ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        //progressBar.setVisibility(ProgressBar.INVISIBLE);
     }  
     
     /**
      * When the workout has been finished
      * @param view
      */
     public void onEndClick(View view)
     {
//     	stopChrono = false;
    	 
    	// stop the workout for the workout
      	handleChrono.removeCallbacks(startTimer);
      	
      	// Initialise the workout time back to hh:mm:ss.mm format
     	((TextView)findViewById(R.id.textWorkoutTime)).setText("00:00:00.00");
     	
     	// tracker status is stopped
     	trackerStatus = tStatus.STOP;
     	
     	// display the results button
     	mapBtn.setVisibility(View.VISIBLE);
     	
        // enable the map button
        mapBtn.setEnabled(true);
     	
        
        /*******************************************************
         * 	Disable the three main buttons (start, pause, end)
         */
        
     	// start
        startBtn.setEnabled(false);
        
        // pause
        pauseBtn.setEnabled(false);
        
        // end
        endBtn.setEnabled(false);
        
        
        //ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        //progressBar.setVisibility(ProgressBar.INVISIBLE);
     }    
        
     /**
      * When the results button has been pressed
      * @param view
      */
     public void onMapClick(View view)
     {
    	 if( workout == null)
    		 return;
    	 Intent intent = new Intent(this, RouteMap.class);
         intent.putExtra("workoutID", workout.getID());        
         startActivity(intent);
   	 
     }	
}



