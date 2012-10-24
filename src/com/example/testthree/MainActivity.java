package com.example.testthree;

import java.util.ArrayList;
import java.util.List;
import com.example.testthree.R;
import com.example.testthree.entity.LocationPoint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.*;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends Activity implements LocationListener
{

	private Handler handleTimer, handleChrono;
	private TextView latituteField, longitudeField;	
	private int runTimerCount = 0;	
	private String hours, minutes, seconds, milliseconds, provider, currentTime;
	private long startTime, elapsedTime;
	private boolean stopChrono = false;
	private boolean terminateCount = false;
	private LocationManager locationManager = null;	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latituteField = (TextView) findViewById(R.id.LatValue);
        longitudeField = (TextView) findViewById(R.id.LongValue);                
        CheckForGPSEnabled();
        handleTimer = new Handler();
        handleTimer.removeCallbacks(runUpdateTimerTask);
        handleTimer.postDelayed(runUpdateTimerTask, 100);
        handleChrono = new Handler();
        runOnUiThread(UpdateGPSOnUI);
    }
    
    private Runnable runUpdateTimerTask = new Runnable() 
    {
        public void run() {      
        	runOnUiThread(UpdateTimeOnUI);  
        	if(terminateCount == true)
        	{
        		runTimerCount++;
        	}
        	
        	if(runTimerCount > 25)
        	{
        		handleTimer.removeCallbacksAndMessages(runUpdateTimerTask);
        	} 
        	else 
        	{
        		handleTimer.postDelayed(this, 1000);	        		
        	}                         
        }
    };   
         
    private final Runnable UpdateTimeOnUI = new Runnable() 
    {        
    	public void run() {
    		UpdateTime();
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
		TextView t = (TextView)findViewById(R.id.TimeValue);
		currentTime = DateFormat.format("h:mm:ssaa ", new java.util.Date()).toString();
		t.setText(currentTime);
    }
    
    private void UpdateGPS()
    {    	    	    
    	Criteria hdCrit = new Criteria();
    	hdCrit.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(hdCrit, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) 
        {
          System.out.println("Provider " + provider + " has been selected.");
          onLocationChanged(location);
        } 
        else 
        {
          latituteField.setText("Location not available");
          longitudeField.setText("Location not available");
        }
    	
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
        
    public void onLocationChanged(Location location) 
    {
    	double x = location.getLatitude();
    	double y = location.getLongitude();
        latituteField.setText("Lat: " + x);
        longitudeField.setText("Long:" + y); 
    }
    
    public void onStatusChanged(String provider, int status, Bundle extras) 
    {
      // TODO Auto-generated method stub
    }

    public void onProviderEnabled(String provider) 
    {
      Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    public void onProviderDisabled(String provider) 
    {
      Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onResume()
    {
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

    public void onUserClick(View view) {
        Intent intent = new Intent(this, RouteMap.class); 
        List<LocationPoint> points = new ArrayList<LocationPoint>();
        Time startTime = new Time();
        startTime.setToNow();
        
        Time endTime = new Time();
        endTime.set(20, 20, 4, 10, 10, 2012);
        LocationPoint p1 = new LocationPoint( -0.1106,50.86524,startTime,true,false,"This is the starting point");
        points.add(p1);
        LocationPoint p2 = new LocationPoint( -0.0939,50.8133,endTime,false,true,"This is the end point");
        points.add(p2);
        
        intent.putParcelableArrayListExtra("com.example.testthree.entity.LocationPoint", (ArrayList<? extends Parcelable>) points);
        
    	startActivity(intent);
    }

    private void updateTimer (float time){
		long secs = (long)(time/1000);
		long mins = (long)((time/1000)/60);
		long hrs = (long)(((time/1000)/60)/60);
		//
		seconds = String.valueOf(secs % 60);
    	if(secs == 0)
    	{
    		seconds = "00";
    	}
    	if(secs <10 && secs > 0)
    	{
    		seconds = "0" + seconds;
    	}
    	
		minutes = String.valueOf(mins % 60);
    	if(mins == 0)
    	{
    		minutes = "00";
    	}
    	if(mins <10 && mins > 0)
    	{
    		minutes = "0" + minutes;
    	}

    	hours = String.valueOf(hrs);
    	if(hrs == 0)
    	{
    		hours = "00";
    	}
    	if(hrs <10 && hrs > 0)
    	{
    		hours = "0" + hours;
    	}
    	
    	milliseconds = String.valueOf((long)time);
    	if(milliseconds.length() == 2)
    	{
    		milliseconds = "0"+ milliseconds;
    	}
    	else if(milliseconds.length() <= 1)
    	{
    		milliseconds = "00";
    	}
    	else 
    	{
    		milliseconds = milliseconds.substring(milliseconds.length()- 3 , milliseconds.length()-1);
    	}
		    
   
		((TextView)findViewById(R.id.chronoTimer)).setText(hours + ":" + minutes + ":" + seconds + "." + milliseconds);		
	}    
    
    private Runnable startTimer = new Runnable() 
    {
	   public void run() {
		   elapsedTime = System.currentTimeMillis() - startTime;
		   updateTimer(elapsedTime);
		   handleChrono.postDelayed(this, 100);
	   }
    };    
    public void onStartClick(View view)
    {    
    	if(stopChrono == true)
    	{    	
    		startTime = System.currentTimeMillis() - elapsedTime;
    	}
    	else
    	{
    		startTime = System.currentTimeMillis();
    	}
    	handleChrono.removeCallbacks(startTimer);
    	handleChrono.postDelayed(startTimer, 0);
    }

    public void onStopClick(View view)
    {       
    	handleChrono.removeCallbacks(startTimer);
    	stopChrono = true;
    }  
    
    public void onResetClick(View view)
    {
    	stopChrono = false;
    	((TextView)findViewById(R.id.chronoTimer)).setText("No Timer");    	
    }    
}
