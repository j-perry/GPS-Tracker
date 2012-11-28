package com.team2.dash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
	private User	activeUser = null;		// keep active user in here
	int				userId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        DatabaseHandler db = new DatabaseHandler(this);
        activeUser = db.getActiveUser();        
        userId = activeUser.getServerUserID();  
        
        setContentView(R.layout.activity_check_in);
        Bundle bundle = getIntent().getExtras();
        String venueJson = bundle.getString("locationJson");
        
        if(userId == -1)
        {
        	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Error");
			alertDialog.setMessage("You have not registered with Dash Servers.\nPlease go to User Maintenance");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int arg1) 
				{
					dialog.cancel();
			        Intent intent = new Intent(CheckIn.this, RouteMap.class);
			        Bundle bundle = getIntent().getExtras();
			        intent.putExtra("workoutID", bundle.getInt("workoutID"));        
			    	startActivity(intent);
					return;
	            }
			});						
			alertDialog.show();		
			
			return;
        }
        
        
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
    public void onListItemClick(ListView l, View v, int position, long id) 
    {
    	VenueInfo venue = (VenueInfo)l.getItemAtPosition(position);
    
    	String[][] vars = new String[6][2];
    	vars[0][0] = "user_id"; 
    	vars[0][1] = userId + "";  
    	vars[1][0] = "location_id";
    	vars[1][1] = venue.getId().toString();
    	vars[2][0] = "location_name";
    	vars[2][1] = venue.getVenueName();
    	vars[3][0] = "location_address";     			
    	vars[3][1] = venue.getVenueAddress();
    	vars[4][0] = "location_lat";
    	vars[4][1] = venue.getLatitude() + "";
    	vars[5][0] = "location_lng";
    	vars[5][1] = venue.getLongitude() + "";  	    
    	
    	String response;
    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, true, CheckIn.this, "Contacting Dash Server ...");
    		response = sc.execute(new String[] {  getResources().getString(R.string.checkinUser) }).get(5, TimeUnit.SECONDS);    	
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
    		String statusResponse = singleVenue.getString("status");
    		
			if (statusResponse.equals("true"))
			{				
				JSONObject checkInObject = singleVenue.getJSONObject("checkin");
				JSONObject locationObject = checkInObject.getJSONObject("location");
				String locationId = locationObject.getString("LocationId");
				if(!(locationId.equals("")) && !(locationId.isEmpty()))
				{
					Intent intent = new Intent(this, VenueActivity.class);	
					venue.setDatabaseId(locationObject.getInt("LocationId"));
					venue.setVenueCheckins(locationObject.getInt("LocationCheckins"));
					intent.putExtra("userId", userId);
					intent.putExtra("venueData", venue);					
					intent.putExtra("checkIn", true);
					startActivity(intent); 
					return;
				}	
				else
				{
					Log.e("HTTP Result", "No Location ID was returned");
				}
			} 
			else if(statusResponse.equals("NOT_USER"))
			{				
				Log.e("HTTP Result", "Invalid User ID");
			}
			else if(statusResponse.equals("MISSING_PARAMETER"))
			{
				Log.e("HTTP Result", "Data missing from call");
			}
			else if(statusResponse.equals("EXCEPTION_ERROR"))
			{
				Log.e("HTTP Result", "Unknown exception error");
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
