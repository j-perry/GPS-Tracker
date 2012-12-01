package com.team2.dash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team2.dash.entity.userCheckins;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserCheckins extends Activity{

//	private ArrayList<String> listItems = new ArrayList<String>();
	private String followUserName;
	private JSONObject results;
	private int currentUserId;
	private List<userCheckins> venues;
	private int followId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkins);
        Bundle bundle = getIntent().getExtras();
        followId = bundle.getInt("followId");
        currentUserId = bundle.getInt("currentUserId");
        followUserName = bundle.getString("followUserName");
        
		TextView textVenue = (TextView)findViewById(R.id.Following);
        textVenue.setText(followUserName);    
  
        ListView ourList = (ListView)findViewById(R.id.listViewCheckins);
        
        RefreshUserData();
        
        ourList.setClickable(true);
       	ourList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
       	{		       	  
       		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
       		{
/*            	ListView ourList = (ListView)findViewById(R.id.listViewCheckins);
       			User ur = (User)ourList.getItemAtPosition(position);   
       			FollowSelectedUser(ur.getServerUserID());
*/       		}
       	});
       	
       	ourList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
       	{

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
            {
/*            	ListView ourList = (ListView)findViewById(R.id.listViewCheckins);
       			User ur = (User)ourList.getItemAtPosition(position);
       			Intent intent = new Intent(UserCheckins.this, FollowActivity.class);					
       			intent.putExtra("followId", ur.getServerUserID());
       			startActivity(intent);        			
*/       			return true;
            }
        });
       	
       	ArrayAdapter<userCheckins> adapter = new ArrayAdapter<userCheckins>(this, android.R.layout.simple_list_item_1, venues);
       	ourList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_user_checkins, menu);
        return true;
    }
    
    private void RefreshUserData()
    {
    	String[][] vars = new String[1][2];
    	vars[0][1] = followId + ""; 	  
    	
    	String selectFile;
    	selectFile = getResources().getString(R.string.fetch_checkins);
    	TextView textVenue = (TextView)findViewById(R.id.User);
        textVenue.setText("has checked in to:");    		
    	
    	String response;
    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, true, UserCheckins.this, "Contacting Dash Server ...");
    		response = sc.execute(new String[] { selectFile }).get(5, TimeUnit.SECONDS);    	
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
    	}
    	
    	try 
    	{       		
    		results = ServerConnector.ConvertStringToObject(response);
			if (results.getString("success").equals("true"))
			{
				RefreshData();
			} 
			else 
			{
				Log.e("HTTP Result", "Something else went wrong server side");
				Toast.makeText(this, "Some error", Toast.LENGTH_SHORT).show();
			}	
			
		}     
	    catch(JSONException e)
	    {
			e.printStackTrace(); 
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
	    }	
    }
    
    private void RefreshData()
    {
    	try
    	{    		
    		if (results == null)
	    	{
	    		Toast.makeText(this, "Unable to pull back results", Toast.LENGTH_SHORT).show();
	    	} 
	    	else 
	    	{
	    		JSONArray checkins = results.getJSONArray("checkins");
	    		
	    		venues = new ArrayList<userCheckins>();
	    		for(int i = 0; i < checkins.length(); i++)
	    		{
	    			JSONObject singleJSONUser = checkins.getJSONObject(i);
	    			userCheckins ven = new userCheckins();	    
	    			ven.setCheckinId(singleJSONUser.getInt("checkInId"));
	    			ven.setLocationName(singleJSONUser.getString("LocationName"));
	    			ven.setDateTime(singleJSONUser.getLong("DateTime"));
	    			venues.add(ven);
//	    			listItems.add(singleUser.getFname() + " " + singleUser.getSname());
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
    
    private void FollowSelectedUser(int userIdToFollow)
    {
    	String[][] vars = new String[2][2];
    	vars[0][0] = "userid"; 
    	vars[0][1] = currentUserId + "";  
    	vars[1][0] = "followid";
    	vars[1][1] = userIdToFollow + "";  
    	
    	String response;
    	try
    	{
    		ServerConnector sc2 = new ServerConnector(vars, true, UserCheckins.this, "Contacting Dash Server ...");
    		response = sc2.execute(new String[] {  getResources().getString(R.string.followUser) }).get(5, TimeUnit.SECONDS);    	
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
    	}

		JSONObject results = ServerConnector.ConvertStringToObject(response);

		if (results == null) {
			Toast.makeText(this, "Unable to pull results", Toast.LENGTH_SHORT).show();
			return;
		}

		try
    	{    		
			String status = results.getString("status");
    		if(status.equals("true"))
    		{
    			Toast.makeText(this, "You are following selected user", Toast.LENGTH_SHORT).show();
    		}
    		else
    		{
    			String txt = "Error: " + status + "\n" + results.getString("error");
    			Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    			return;
    		}    			
    	}      	
	    catch (JSONException e)
	    {
	    	Toast.makeText(this, "Unable to pull results", Toast.LENGTH_SHORT).show();
			Log.v("Error", "JSONException " + e.getMessage());    			
			return;
	    }
    }
}