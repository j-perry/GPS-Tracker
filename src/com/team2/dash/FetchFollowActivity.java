package com.team2.dash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team2.dash.entity.User;
import com.team2.dash.entity.VenueInfo;
import com.team2.dash.entity.VenueReview;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FetchFollowActivity extends Activity 
{
	/**
	 * Private members
	 */
	private ArrayList<String> listItems = new ArrayList<String>();
	private User activeUser = null;		// keep active user in here
	private int userActivity = 1; //1 (fetch_following) or 2 (fetch_followers)	
	private JSONObject results;
	private int currentUserId;
	private List<User> users;
	private int displayId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_follow);
        Bundle bundle = getIntent().getExtras();
        displayId = bundle.getInt("userId");
        userActivity = bundle.getInt("userActivity");
        
        DatabaseHandler db = new DatabaseHandler(this);
        activeUser = db.getActiveUser();        
        currentUserId = activeUser.getServerUserID();  
        
        FetchDisplayUser();
        
        
        ListView ourList = (ListView)findViewById(R.id.listViewMain);
        
        RefreshUserData();
        
        ourList.setClickable(true);
       	ourList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
       	{		       	  
       		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
       		{
            	ListView ourList = (ListView)findViewById(R.id.listViewMain);
       			User ur = (User)ourList.getItemAtPosition(position);   
       			FollowSelectedUser(ur.getServerUserID());
       			//TODO: Follow the user
       		}
       	});
       	
       	ourList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
       	{

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
            {
            	ListView ourList = (ListView)findViewById(R.id.listViewMain);
       			User ur = (User)ourList.getItemAtPosition(position);
       			Intent intent = new Intent(FetchFollowActivity.this, FollowActivity.class);					
       			intent.putExtra("followId", ur.getServerUserID());
       			startActivity(intent);        			
       			return true;
            }
        });
       	
       	ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, users);
       	ourList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_user_follow, menu);
        return true;
    }
    
    private void RefreshUserData()
    {
    	String[][] vars = new String[1][2];
    	vars[0][1] = displayId + ""; 	  
    	
    	String selectFile;
    	if(userActivity == 2)
    	{
    		selectFile = getResources().getString(R.string.fetch_followers);
            TextView textVenue = (TextView)findViewById(R.id.User);
            textVenue.setText("Users following");
    	}
    	else
    	{    		
    		selectFile = getResources().getString(R.string.fetch_following);
    		TextView textVenue = (TextView)findViewById(R.id.User);
            textVenue.setText("Following users");    		
    	}
    	
    	String response;
    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, true, FetchFollowActivity.this, "Contacting Dash Server ...");
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
			}	
			
			Toast.makeText(this, "Failed to Add Review", Toast.LENGTH_SHORT).show();
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
	    		Toast.makeText(this, "Unable to pull back review results", Toast.LENGTH_SHORT).show();
	    	} 
	    	else 
	    	{
	    		JSONArray fullUsers = results.getJSONArray("followers");
	    		
	    		users = new ArrayList<User>();
	    		for(int i = 0; i < fullUsers.length(); i++)
	    		{
	    			JSONObject singleJSONUser = fullUsers.getJSONObject(i);
	    			User singleUser = new User();	    
	    			singleUser.setServerUserID(singleJSONUser.getInt("user_id"));
	    			singleUser.setFname(singleJSONUser.getString("Firstname"));
	    			singleUser.setSname(singleJSONUser.getString("Firstname"));
	    			users.add(singleUser);
	    			listItems.add(singleUser.getFname() + " " + singleUser.getSname());
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
    
    private void FetchDisplayUser()
    {
    	//TODO: This
    }
    
    private void FollowSelectedUser(int userIdToFollow)
    {
    	
    }
}
