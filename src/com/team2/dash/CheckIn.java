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
	private int userId = 1;
	
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
	    			JSONObject singleJSONVenue = venues.getJSONObject(i);
	    			VenueInfo singleVenue = new VenueInfo();	    			
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
    	
    	String[][] vars = new String[7][2];
    	vars[0][0] = "vn";
    	vars[0][1] = venue.getVenueName();
    	vars[1][0] = "va";
    	vars[1][1] = venue.getVenueAddress();
    	vars[2][0] = "id";
    	vars[2][1] = venue.getId().toString();
    	vars[3][0] = "la";
    	vars[3][1] = venue.getLatitude() + "";
    	vars[4][0] = "ln";
    	vars[4][1] = venue.getLongitude() + "";    	
    	vars[5][0] = "ud";
    	vars[5][1] = userId + "";    	    	
    	vars[6][0] = "a";
    	vars[6][1] = "LocationAndCheckin";    	    
    	
    	String response;
    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, false, CheckIn.this, "Contacting Dash Server ...");
    		response = sc.execute(new String[] { "CheckIn.php"}).get(5, TimeUnit.SECONDS);    	
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
    	}
    	
    	try 
    	{       		
    		JSONObject singleVenue = ServerConnector.ConvertStringToObject(response);
			
			if (singleVenue.getString("success") == "true")
			{
				int result_location = singleVenue.getInt("Location");
				int result_checkin = singleVenue.getInt("Checkin");
				if(result_location > 0 && result_checkin > 0)
				{
					Intent intent = new Intent(this, VenueActivity.class);					
					intent.putExtra("dashId", result_location);
					intent.putExtra("userId", userId);
					intent.putExtra("venueData", venue);
					intent.putExtra("checkIn", true);
					startActivity(intent); 
					return;
				}
				else if(result_checkin == -25)
				{
					Log.e("Checkin Error", "Missing Data from Checkin");
				}
				else if(result_checkin == -26)
				{
					Log.e("Checkin Error", "Unable to add Checkin Data due to DB Error");
				}
				else if(result_location == 0)
				{
					Log.e("Location Error", "Missing Data from Location");
				}
				else if(result_location == -1)
				{
					Log.e("Location Error", "Unable to add Location Data due to DB Error");
				}
				else if(result_location == -2)
				{
					Log.e("Location Error", "API ID is already in use");
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
