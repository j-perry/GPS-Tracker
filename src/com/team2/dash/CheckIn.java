package com.team2.dash;

import java.util.ArrayList;
import java.util.List;

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

	private List<VenueInfo> VenueInformation;	
	private JSONObject results;	
	private ArrayList<String> listItems = new ArrayList<String>();
	//private VenueInfo testVenue;
	
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
        
        //Test data
//        testVenue = new VenueInfo();
//        testVenue.setFourSquareId("4c039b9af56c2d7fa3121d66");
//        testVenue.setVenueName("East Slope Bar");
//        testVenue.setVenueAddress("University of Sussex");
//        testVenue.setLatitude(50.869373);
//        testVenue.setLongitude(-0.088239);

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
	    			singleVenue.setFourSquareCheckins(singleJSONVenue.getInt("nbrCheckins"));
	    			singleVenue.setFourSquareId(singleJSONVenue.getString("id"));
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
    	//VenueInfo venue = testVenue;    	
    	Toast.makeText(this, "Checking In at " + venue.toString(), Toast.LENGTH_SHORT).show();
    	
    	String[][] vars = new String[4][2];
    	vars[0][0] = "n";
    	vars[0][1] = venue.VenueName;
    	vars[1][0] = "d";
    	vars[1][1] = venue.VenueAddress;
    	vars[2][0] = "f";
    	vars[2][1] = venue.FourSquareId;
    	vars[3][0] = "a";
    	vars[3][1] = "addLocation";    	    	
    	
    	ServerConnector sc = new ServerConnector(vars, false, CheckIn.this, "Contacting Dash Server ...");    	
    	String response;
    	try
    	{
    		response = sc.execute(new String[] { "AddLocation.php"}).get();    	
    	} 
    	catch (Exception e)
    	{
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
    	}
    	
    	try 
    	{   

    		JSONObject singleVenue = ServerConnector.ConvertStringToObject(response);
			
			if (singleVenue.getString("success") == "true")
			{
				int result_http = singleVenue.getInt("result");
				if(result_http > 0)
				{
					Toast.makeText(this, "Result: " + result_http, Toast.LENGTH_SHORT).show();
					//TODO: Check user in to location					
					
			    	Intent intent = new Intent(this,CheckIn.class);
					intent.putExtra("fourSquareId", venue.FourSquareId);
					intent.putExtra("dashId", result_http);
					startActivity(intent);  					
				} 
				else if (result_http == -1)
				{
					Log.e("HTTP Result", "Location was not registered due to DB problem");					
				}
				else if (result_http == -2)
				{					
					Log.e("HTTP Result", "Four Square ID is already in use by other location (ID: " + venue.FourSquareId +")");					
				}
			} 
			else 
			{
				Log.e("HTTP Result", "Incorrect Page Call");
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
