package com.team2.dash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.team2.dash.entity.*;

/*
 * Class to handle the display of checkin locations. Displays Locations in a clickable list.
 */
public class CheckIn extends ListActivity
{

	private ArrayList<String> listItems = new ArrayList<String>();
	private List<VenueInfo> VenueInformation;	
	private JSONObject results;		
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Bundle bundle = getIntent().getExtras();
        String venueJson = bundle.getString("locationJson");
    	results = ServerConnector.ConvertStringToObject(venueJson);    	
        RefreshVenueInfo();  
        
       	ArrayAdapter<VenueInfo> adapter = new ArrayAdapter<VenueInfo>(this, android.R.layout.simple_list_item_1,VenueInformation);
       	      	
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_check_in, menu);
        return true;
    }

    public void RefreshVenueInfo()
    {    	    	
    	try
    	{    		
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
	    			singleVenue.setId(singleJSONVenue.getString("id"));
	    			singleVenue.setLatitude(singleJSONVenue.getDouble("lat"));
	    			singleVenue.setLongitude(singleJSONVenue.getDouble("lng"));
	    			singleVenue.setVenueAddress(singleJSONVenue.getString("address"));
	    			singleVenue.setVenueName(singleJSONVenue.getString("name"));
	    			VenueInformation.add(singleVenue);
	    			listItems.add(VenueInformation.toString());
	    		}
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
    public void onListItemClick( ListView l, View v, int position, long id) 
    {
    	VenueInfo venue = (VenueInfo)l.getItemAtPosition(position);
    	Toast.makeText(this, "Checking In at " + venue.toString(), Toast.LENGTH_SHORT).show();
    	
    	String[][] vars = new String[4][2];
    	vars[0][0] = "n";
    	vars[0][1] = venue.venueName;
    	vars[1][0] = "d";
    	vars[1][1] = venue.venueAddress;
    	vars[2][0] = "f";
    	vars[2][1] = venue.id;
    	vars[3][0] = "a";
    	vars[3][1] = "addLocation";    	    
    	
    	String response;
    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, false, CheckIn.this, "Contacting Dash Server ...");
    		response = sc.execute(new String[] { "AddLocation.php"}).get(5, TimeUnit.SECONDS);    	
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
    	}
    	
    	try 
    	{   
    		Log.v("Response", response);
    		JSONObject singleVenue = ServerConnector.ConvertStringToObject(response);
			
			if (singleVenue.getString("success") == "true")
			{
				int result_http = singleVenue.getInt("result");
				if(result_http > 0)
				{					
			    	String[][] new_vars = new String[4][2];
			    	new_vars[0][0] = "n";
			    	new_vars[0][1] = "" + result_http;
			    	new_vars[1][0] = "d";
			    	//new_vars[1][1] = 1; //TODO: Change to to use UserId
			    	new_vars[1][1] = "" + 1;
			    	new_vars[2][0] = "f";
			    	Time now = new Time();
			    	now.setToNow();
			    	new_vars[2][1] = now.toString();
			    	new_vars[3][0] = "a";
			    	new_vars[3][1] = "addCheckin";    	    
			    	
			    	String second_response;
			    	try
			    	{
			    		ServerConnector sc2 = new ServerConnector(new_vars, false, CheckIn.this, "Contacting Dash Server ...");
			    		second_response = sc2.execute(new String[] { "AddCheckIn.php"}).get(5, TimeUnit.SECONDS);    	
			    	} 
			    	catch (Exception e)
			    	{
			    		e.printStackTrace();
						Log.v("Error", "CheckIn Exception " + e.getMessage());   
						return;
			    	}	
			    	
			    	JSONObject singleCheckIn = ServerConnector.ConvertStringToObject(second_response);
					
					if (singleCheckIn.getString("success") == "true")
					{
						int result_http_checkin = singleCheckIn.getInt("result");
						if(result_http_checkin > 0)
						{
					    	Intent intent = new Intent(this, VenueActivity.class);
							intent.putExtra("id", venue.id);
							intent.putExtra("dashId", result_http);
							startActivity(intent); 
						}
						else 
						{
							Log.e("HTTP Result", "Checkin was not created due to DB Problem");
						}
					}	
					else 
					{
						Log.e("HTTP Result", "Invalid call to php page");
					}
				} 
				else if (result_http == -1)
				{
					Log.e("HTTP Result", "Location was not registered due to DB problem");					
				}
				else if (result_http == -2)
				{					
					Log.e("HTTP Result", "Four Square ID is already in use by other location (ID: " + venue.id +")");					
				}
			} 
			else 
			{
				Log.e("HTTP Result", "Invalid call to php page");
			}	
			
			Toast.makeText(this, "Failed to Check In", Toast.LENGTH_SHORT).show();
		}     
	    catch(JSONException e)
	    {
			e.printStackTrace(); 
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
	    }	  
    }      
}
