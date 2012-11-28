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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getInt("userId");
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
    	
    	String[][] vars = new String[4][2];
    	vars[0][0] = "userid";
    	vars[0][1] = userId + "";
    	vars[1][0] = "locationid";
    	vars[1][1] = venue.getDatabaseId() + "";
    	vars[2][0] = "reviewText";
    	vars[2][1] = et.getText().toString();
    	vars[3][0] = "fltRating";
    	vars[3][1] = mBar.getRating() + "";   	 	    	   	    
    	
    	String response;
    	try
    	{
    		ServerConnector sc = new ServerConnector(vars, true, AddReview.this, "Contacting Dash Server ...");
    		response = sc.execute(new String[] { getResources().getString(R.string.addReview)}).get(5, TimeUnit.SECONDS);    	
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
			if (singleVenue.getString("success").equals("true"))
			{
				Intent intent = new Intent(this, VenueActivity.class);					
				intent.putExtra("userId", userId);
				intent.putExtra("venueData", venue);
				startActivity(intent); 				
				return;
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
}