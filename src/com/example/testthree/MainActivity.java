package com.example.testthree;

import com.example.testthree.R;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.*;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends Activity implements LocationListener
{

	private Handler handleTimer;
	private TextView latituteField;
	private TextView longitudeField;		
	private LocationManager locationManager = null;
	private int runTimerCount = 0;	
	private boolean terminateCount = false;
	private String provider;
	private String currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latituteField = (TextView) findViewById(R.id.LatValue);
        longitudeField = (TextView) findViewById(R.id.LongValue);                
        CheckForGPSEnabled();     
        UpdateGPS();
        locationManager.requestLocationUpdates(provider, 0, 0, this);
        handleTimer = new Handler();
        handleTimer.removeCallbacks(runUpdateTimerTask);
        handleTimer.postDelayed(runUpdateTimerTask, 100);
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
		currentTime = DateFormat.format("h:mm:ssaa, EEE dd-MM-yyyy ", new java.util.Date()).toString();
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
        boolean enabled_GPS = false;
        boolean enabled_NETWORK = false;
        try
        {        	        	
        	enabled_GPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        	enabled_NETWORK = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);        	
        }
        catch (Exception e)
        {        	
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Error");
			alertDialog.setMessage("Error with GPS Location.\n" + e.getMessage());
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() 
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
		if (enabled_GPS == false && enabled_NETWORK == false) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("GPS Disabled");
			alertDialog.setMessage("Location services are currently disabled\nGo to Settings to activate them.");
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Location Services", new DialogInterface.OnClickListener() 
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
        locationManager.requestLocationUpdates(provider, 0, 0, this);
    }  
        
    @Override
    protected void onPause() 
    {
      super.onPause();
      locationManager.removeUpdates(this);
    }

}
