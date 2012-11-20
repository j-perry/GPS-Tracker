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

	private LocationManager locationManager = null;
	private Location location;
	private Handler handleTimer, handleChrono;
	private long startTime, elapsedTime;
	private boolean stopChrono = false;
	private boolean terminateCount = false;
	private int runTimerCount = 0;
	String	currentTime, provider;
    int		activeUserID;
    GeoPoint	gp1, gp2;

	private	Workout		workout = null; // active workout, null if not in use
    DatabaseHandler 	db;

	double 		latitude;
	double 		longitude;
	double 		altitude;
	double 		distance;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        db = new DatabaseHandler(this);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        	activeUserID = extras.getInt("activeUserID");
        else
        {
        	User user = db.getActiveUser();
        	activeUserID = user.getID();
        }
        
        Button button_start = (Button) findViewById(R.id.button1);
        button_start.setEnabled(false);
        Button button_end = (Button) findViewById(R.id.button2);
        button_end.setEnabled(false);
        Button button_restart = (Button) findViewById(R.id.button3);
        button_restart.setEnabled(false);

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
         
    private final Runnable UpdateTimeOnUI = new Runnable() 
    {        
    	public void run() {
    		UpdateTime();
        	if( (workout != null) && (stopChrono == false) && (location != null) )
        		SaveLocation();
        }
    };
    
    private final Runnable UpdateGPSOnUI = new Runnable() 
    {        
    	public void run() {
    		UpdateGPS();
        }
    };            

    private void UpdateTime()
    {
		TextView t = (TextView)findViewById(R.id.textTime);
		currentTime = DateFormat.format("hh:mm:ss ", new java.util.Date()).toString();
		t.setText(currentTime);
   }
    
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
    
    private void UpdateGPS()
    {
    	String		txt;
    	Criteria hdCrit = new Criteria();
    	hdCrit.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(hdCrit, false);        
        location = locationManager.getLastKnownLocation(provider);
        Button button_start = (Button) findViewById(R.id.button1);
        Button button_end = (Button) findViewById(R.id.button2);
        Button button_restart = (Button) findViewById(R.id.button3);        
        if (location != null) 
        {
//          System.out.println("Provider " + provider + " has been selected.");
        	onLocationChanged(location);
        	txt = "GPS working";            
            button_start.setEnabled(true);            
            button_end.setEnabled(true);            
            button_restart.setEnabled(true);                
        } 
        else 
        {
            txt = "Location not available";
            button_start.setEnabled(false);            
            button_end.setEnabled(false);            
            button_restart.setEnabled(false);               
            Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
        }
     	((TextView)findViewById(R.id.textGPS)).setText(txt);
    	
    }        
        
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

     private Runnable startTimer = new Runnable() 
     {
 	   public void run() {
 		   elapsedTime = System.currentTimeMillis() - startTime;
 		   updateTimer(elapsedTime);
 		   handleChrono.postDelayed(this, 100);
 	   }
     };    

     private void startNewWorkout(){
      	workout = new Workout( 0, activeUserID, 0, 0, 0, DateFormat.format("dd/MM/yy hh:mm", new java.util.Date()).toString() );
      	int id = db.addWorkout(workout);
      	workout.setID( id );
      	distance = 0;
      	gp1 = null;

      	String txt = "New workout started\n" + workout;
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
   	 
     }
     
     public void onStartClick(View view)
     {    
     	if(stopChrono == true)
     	{    	
     		startTime = System.currentTimeMillis() - elapsedTime;
     	}
     	else
     	{
     		startTime = System.currentTimeMillis();
     		startNewWorkout();
     		
     	}
     	handleChrono.removeCallbacks(startTimer);
     	handleChrono.postDelayed(startTimer, 0);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(ProgressBar.VISIBLE);
     	
     	
     }

     public void onStopClick(View view)
     {       
     	handleChrono.removeCallbacks(startTimer);
     	stopChrono = true;
     }  
     
     public void onResetClick(View view)
     {
     	stopChrono = false;
     	((TextView)findViewById(R.id.textWorkoutTime)).setText("No Timer");
 // ================================================
     	workout = null;
 // ================================================

     }    
        
	
}
