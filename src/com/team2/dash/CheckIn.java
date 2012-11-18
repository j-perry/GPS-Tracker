package com.team2.dash;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.team2.dash.entity.*;

public class CheckIn extends ListActivity implements LocationListener
{

	private LocationManager locationManager = null;	
	private List<VenueInfo> VenueInformation;
	private Double latitude, longitude;
	private JSONObject results;
	private String provider;
	private String[][] vars;		
	private ArrayList<String> listItems;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        listItems = new ArrayList();
        
        setContentView(R.layout.activity_check_in);
        CheckForGPSEnabled();
        runOnUiThread(UpdateGPSOnUI);
        RefreshVenueInfo();  
        
       	ArrayAdapter<VenueInfo> adapter = new ArrayAdapter<VenueInfo>(this, android.R.layout.simple_list_item_1, VenueInformation );
        setListAdapter(adapter);
        
        ListView lv = getListView();
        lv.setOnItemLongClickListener(
        	new AdapterView.OnItemLongClickListener() {
        		public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
        			return onLongListItemClick(view, position, id) ;
        		}
        	}
        );          
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_check_in, menu);
        return true;
    }
    
    public void onProviderEnabled(String provider) 
    {
      Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    public void onProviderDisabled(String provider) 
    {
      Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
    }

	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		// TODO Auto-generated method stub		
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
    
    public void onLocationChanged(Location location) 
    {
    	latitude = location.getLatitude();
    	longitude = location.getLongitude();
    	Log.v("Latitude", "" + latitude);
    	Log.v("Longitude", "" + longitude);
    }     
    
    public void onRefreshClick(View view)
    {
        CheckForGPSEnabled();
        runOnUiThread(UpdateGPSOnUI);
        RefreshVenueInfo();     
    }
    
    private void UpdateGPS()
    {    	    	    
    	Criteria hdCrit = new Criteria();
    	hdCrit.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(hdCrit, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) 
        {
          Log.v("GPS", "Provider " + provider + " has been selected.");
          onLocationChanged(location);
        } 
        else 
        {
	    	Log.v("Latitude", "Location not available");
	    	Log.v("Longitude", "Location not available");        	
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
        
    private final Runnable UpdateGPSOnUI = new Runnable() 
    {        
    	public void run() 
    	{
    		UpdateGPS();
        }
    };            
    
    public void RefreshVenueInfo()
    {    	
    	
    	if(latitude == null || longitude == null)
    	{
    		Toast.makeText(this, "There are currently no GPS Results", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	try
    	{    		
    		vars = new String[2][2];
    		vars[0][0] = "latitude";
    		vars[0][1] = latitude.toString();
    		vars[1][0] = "longitude";
    		vars[1][1] = longitude.toString();
    		
    		//AsyncChanges
    		// mareks comment - this changes should be done outside of UI thread thats the whole idea of it
    		// 
    		runOnUiThread(SendAndRecieveFourSquareResponse); 
    		
    		if (results == null)
	    	{
	    		Toast.makeText(this, "Unable to pull back location results", Toast.LENGTH_SHORT).show();
	    	} 
	    	else 
	    	{
	    		JSONArray venues = results.getJSONArray("venues");
	    		VenueInformation = new ArrayList<VenueInfo>();
	    		for(int i = 0; i < venues.length(); i++)
	    		{
	    			VenueInfo singleVenue = new VenueInfo();
	    			JSONObject singleJSONVenue = venues.getJSONObject(i);
	    			singleVenue.setFourSquareCheckins(singleJSONVenue.getInt("nbrCheckins"));
	    			singleVenue.setFourSquareId(singleJSONVenue.getString("id"));
	    			singleVenue.setLatitude(singleJSONVenue.getDouble("lat"));
	    			singleVenue.setLongitude(singleJSONVenue.getDouble("lng"));
	    			singleVenue.setVenueAddress(singleJSONVenue.getString("address"));
	    			singleVenue.setVenueName(singleJSONVenue.getString("name"));
	    			VenueInformation.add(singleVenue);
	    			listItems.add(VenueInformation.toString());
	    		}
	    		Log.v("Information", "" + VenueInformation.size());  		
	    	}
    	}      	
	    catch (JSONException e)
	    {
	    	Toast.makeText(this, "Unable to pull back location results", Toast.LENGTH_SHORT).show();
			Log.v("Error", "JSONException " + e.getMessage());    			
			return;
	    }    	  
    } 
    
    @Override
    public void onListItemClick( ListView l, View v, int position, long id) {
    	VenueInfo venue = (VenueInfo)l.getItemAtPosition(position);
    	//db.setUserActive(user.getID());
    	String txt = "Venue " + venue.toString() + " selected";
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    	//finish();
    }  

    public boolean onLongListItemClick(View v, int position, long id) {
    	ListView l = getListView();
    	VenueInfo venue = (VenueInfo)l.getItemAtPosition(position);    	
    	refreshList();
    	String txt = "Venue " + venue.toString() + " deleted";
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    	
    	return true;
    }
    
    private void refreshList(){
        @SuppressWarnings("unchecked")      
        ArrayAdapter<VenueInfo> adapter = (ArrayAdapter<VenueInfo>) getListAdapter();
        RefreshVenueInfo();
        adapter.clear();        
        for(int i = 0; i < VenueInformation.size(); i++) {
        	adapter.add(VenueInformation.get(i));
        }
        
        adapter.notifyDataSetChanged();   	
    }      
    
    private final Runnable SendAndRecieveFourSquareResponse = new Runnable() 
    {        
    	public void run() 
    	{
    		ServerConnector sc = new ServerConnector(vars, "Test.php", false);
    		results = sc.ConvertStringToObject(null); 
        }
    };

}
