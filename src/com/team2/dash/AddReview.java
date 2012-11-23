package com.team2.dash;

import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.team2.dash.entity.VenueInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class AddReview extends Activity {
	
	private VenueInfo venue;
	private int userId;
	private int dashId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getInt("userId");
        dashId = bundle.getInt("dashId");
        venue = (VenueInfo)bundle.getParcelable("venueData");
        TextView textVenue = (TextView)findViewById(R.id.textName);
        textVenue.setText(venue.getVenueName());
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_add_review, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	public void onAddClick(View view)
	{		    	
    	RatingBar mBar = (RatingBar) findViewById(R.id.ratingBar);
    	EditText et = (EditText)findViewById(R.id.editText);
    	
    	String[][] vars = new String[5][2];
    	vars[0][0] = "ud";
    	vars[0][1] = userId + "";
    	vars[1][0] = "ld";
    	vars[1][1] = dashId + "";
    	vars[2][0] = "rt";
    	vars[2][1] = et.getText().toString();
    	vars[3][0] = "rr";
    	vars[3][1] = mBar.getRating() + "";   	 	    	
    	vars[4][0] = "a";
    	vars[4][1] = "addReview";    	    
    	
    	String response;
    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, false, AddReview.this, "Contacting Dash Server ...");
    		response = sc.execute(new String[] { "AddReview.php"}).get(5, TimeUnit.SECONDS);    	
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
			String additionalText = "Failed to Add Review";
			if (singleVenue.getString("success") == "true")
			{
				int result = singleVenue.getInt("result");
				
				if(result == 1)
				{
					Intent intent = new Intent(this, VenueActivity.class);					
					intent.putExtra("dashId", dashId);
					intent.putExtra("userId", userId);
					intent.putExtra("venueData", venue);
					startActivity(intent); 				
					return;
				}
				else if (result == 0)
				{
					Log.e("HTTP Result", "Missing Data (LocationId or ");
				}
				else if(result == -1)
				{
					additionalText = "No review or rating provided";
				}
			} 
			else 
			{
				Log.e("HTTP Result", "Invalid call to php page");
			}	
			
			Toast.makeText(this, additionalText, Toast.LENGTH_SHORT).show();
		}     
	    catch(JSONException e)
	    {
			e.printStackTrace(); 
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
	    }	  		
	}	    
}