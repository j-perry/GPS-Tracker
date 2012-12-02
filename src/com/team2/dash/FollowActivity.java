package com.team2.dash;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.team2.dash.entity.VenueInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FollowActivity extends Activity {
	
	int followId;   //This is going to be for the user we have displayed
	int userId;		// this is actual server user ID	
	String userName;
	String firstName;
	String lastName;
	String followUserName = "Test User"; //TODO: On server fetch of user, set First Name and Surname here

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        
        Bundle bundle = getIntent().getExtras();
        followId = bundle.getInt("followId");        
        userId = bundle.getInt("userId");        
        
    	
    	String[][] vars = new String[1][2];
    	vars[0][1] = followId + "";  
    	

        String response;

    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, true, FollowActivity.this, "Contacting Dash Server ...");
    		response = sc.execute(new String[] {  getResources().getString(R.string.fetchUser) }).get(5, TimeUnit.SECONDS);    	
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.v("Error", "Fetch Exception " + e.getMessage());   
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

    		if( status.equals("true") ){
    			JSONObject userdata = results.getJSONObject("user");
    			int userID = userdata.getInt("userid");
    			userName = userdata.getString("username");
    			firstName = userdata.getString("firstname");
    			lastName = userdata.getString("lastname");
    			followUserName = firstName + " " + lastName;
    		}
    		else{
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
		
        TextView Tv = (TextView) findViewById(R.id.textView2);
        Tv.setText(followUserName);
        Tv = (TextView) findViewById(R.id.textView3);
        Tv.setText(userName);
        
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_follow, menu);
        return true;
    }
    
    public void onClickUserReviews(View view) 
    {
    	Intent intent = new Intent(this, UserReviews.class);
    	intent.putExtra("currentUserId", userId);
    	intent.putExtra("followId", followId);
    	intent.putExtra("followUserName", followUserName);
    	startActivity(intent);
    	
    }
    
    public void onClickUserCheckins(View view) 
    {
    	Intent intent = new Intent(this, UserCheckins.class);
    	intent.putExtra("currentUserId", userId);
    	intent.putExtra("followId", followId);
    	intent.putExtra("followUserName", followUserName);
    	startActivity(intent);
    }
    
    public void onClickUserFollowers(View view) 
    {
		Intent intent = new Intent(this, FetchFollowActivity.class);
		intent.putExtra("currentUserId", userId);
		intent.putExtra("followId", followId);
		intent.putExtra("followUserName", followUserName);
		intent.putExtra("userActivity", 2);
		startActivity(intent);
    }
    
    public void onClickFollowingUsers(View view) 
    {
		Intent intent = new Intent(this, FetchFollowActivity.class);
		intent.putExtra("currentUserId", userId);
		intent.putExtra("followId", followId);		
		intent.putExtra("followUserName", followUserName);
		intent.putExtra("userActivity", 1);				
		startActivity(intent);
    }    
    
    public void onClickFollowUser(View view) 
    {    	
       	String[][] vars = new String[2][2];
    	vars[0][0] = "userid"; 
    	vars[0][1] = userId + "";  
    	vars[1][0] = "followid";
    	vars[1][1] = followId + "";  
    	
    	String response;
    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, true, FollowActivity.this, "Contacting Dash Server ...");
    		response = sc.execute(new String[] {  getResources().getString(R.string.followUser) }).get(5, TimeUnit.SECONDS);    	
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

    		if( status.equals("true") ){
    			Toast.makeText(this, "You are following selected user", Toast.LENGTH_SHORT).show();
    		}
    		else{
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
