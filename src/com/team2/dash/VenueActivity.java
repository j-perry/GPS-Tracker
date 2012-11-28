package com.team2.dash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.team2.dash.entity.MapItemizedOverlay;
import com.team2.dash.entity.VenueInfo;
import com.team2.dash.entity.VenueReview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class VenueActivity extends MapActivity
{	
	private ArrayList<String> listItems = new ArrayList<String>();
	private List<VenueReview> reviews;
	private GeoPoint venueLocation;
	private JSONObject results;
	private MapView mapView;	
	private VenueInfo venue;	
	private int userId;
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		//This onCreate method is crazy.
		//I apologise for it now, but it's just got so much to do before page load
		//~Ben
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getInt("userId");
        venue = (VenueInfo)bundle.getParcelable("venueData");
        
        if(bundle.getBoolean("checkIn") == true)
        {
        	Toast.makeText(this, "Checked In", Toast.LENGTH_SHORT).show();
        }
        else
        {
        	Toast.makeText(this, "Review Added", Toast.LENGTH_SHORT).show();
        }
        
        TextView textVenue = (TextView)findViewById(R.id.textViewRealName);
        textVenue.setText(venue.getVenueName());
        TextView textAddress = (TextView)findViewById(R.id.textViewRealAddress);
        textAddress.setText(venue.getVenueAddress());
        TextView textCheckin = (TextView)findViewById(R.id.textViewRealCheckin);
        textCheckin.setText("" + venue.getVenueCheckins());
        
        String ratingText;
        if(venue.isVenueUseRating() == true)
        {
        	ratingText = "" + venue.getVenueRating();
        } 
        else
        {
        	ratingText = "No Ratings";
        }

        TextView textRating = (TextView)findViewById(R.id.textViewRealRating);
        textRating.setText(ratingText);
    	
        venueLocation = new GeoPoint((int)(venue.getLatitude() * 1e6), (int)(venue.getLongitude() * 1e6));
        
        mapView = (MapView) findViewById(R.id.mapview);
        
        //sets the zoom to see the location closer
        mapView.getController().setZoom(18);
 
        //this will let you to zoom in or out using the controllers
        mapView.setBuiltInZoomControls(true);

        List<Overlay> mapOverlays = mapView.getOverlays();        
        Drawable drawable = this.getResources().getDrawable(R.drawable.cycling);
         
        MapItemizedOverlay itemizedoverlay = new MapItemizedOverlay(drawable, this);
 
       //this will show you the map at the exact location you want (if you not set this you will see the map somewhere in America)
        mapView.getController().setCenter(venueLocation);
        OverlayItem overlayitem = new OverlayItem(venueLocation, venue.getVenueName(), venue.getVenueAddress());
 
        itemizedoverlay.addOverlay(overlayitem);        
        mapOverlays.add(itemizedoverlay);        
        
        ListView ourList = (ListView)findViewById(R.id.listViewMain);
       	
       	ourList.setClickable(true);
       	ourList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
       	{		       	  
       		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
       		{
       			final int pos = position;
       			AlertDialog alertDialog = new AlertDialog.Builder(VenueActivity.this).create();
    			alertDialog.setTitle("Rate Review");
    			alertDialog.setMessage("How would you like to rate this review?");
    			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "+1 Positive", new DialogInterface.OnClickListener() 
    			{
    				public void onClick(DialogInterface dialog, int arg1) 
    				{
		       			ListView ourList = (ListView)findViewById(R.id.listViewMain);
		       			VenueReview vr = (VenueReview)ourList.getItemAtPosition(pos);				       			
    					AddRatingToReview(vr, ourList, true);
    					dialog.cancel();	
    					return;
    	            }
    			});	
    			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "+1 Negative", new DialogInterface.OnClickListener() 
    			{
    				public void onClick(DialogInterface dialog, int arg1) 
    				{
		       			ListView ourList = (ListView)findViewById(R.id.listViewMain);
		       			VenueReview vr = (VenueReview)ourList.getItemAtPosition(pos);				       			
    					AddRatingToReview(vr, ourList, false);
    					dialog.cancel();
    					return;
    	            }
    			});				    			
    			alertDialog.show();	
       		}
       	});

       	ourList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
       	{

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
       			
            	ListView ourList = (ListView)findViewById(R.id.listViewMain);
       			VenueReview vr = (VenueReview)ourList.getItemAtPosition(position);
       			Intent intent = new Intent(VenueActivity.this, FollowActivity.class);					
       			intent.putExtra("followId", vr.DBUserId);
       			startActivity(intent); 
                return true;
            }
        }); 
    
        
        String[][] vars = new String[1][2];
        vars[0][1] = venue.getId();
                
        String response;
    	try
    	{
    		ServerConnector sc2 = new ServerConnector(vars, true, VenueActivity.this, "Contacting Dash Server ...");
    		response = sc2.execute(new String[] {  getResources().getString(R.string.fetchLocation) }).get(5, TimeUnit.SECONDS);    	
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.v("Error", "Venue Exception " + e.getMessage());   
			return;
    	}
    	    	
    	try 
    	{       		
    		results = ServerConnector.ConvertStringToObject(response);    		
    		String statusResponse = results.getString("status");
    		
			if (statusResponse.equals("true"))
			{							
		        RefreshReviewInfo();  		    	
		       	ArrayAdapter<VenueReview> adapter = new ArrayAdapter<VenueReview>(this, android.R.layout.simple_list_item_1, reviews);
		       	ourList.setAdapter(adapter);
			}
		}     
	    catch(JSONException e)
	    {
			e.printStackTrace(); 
			Log.v("Error", "CheckIn Exception " + e.getMessage());   
			return;
	    }
    	 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_venue, menu);
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
    
    public void RefreshReviewInfo()
    {    	    	
    	try
    	{    		
    		if (results == null)
	    	{
	    		Toast.makeText(this, "Unable to pull back review results", Toast.LENGTH_SHORT).show();
	    	} 
	    	else 
	    	{
	    		JSONArray venues = results.getJSONArray("reviews");
	    		
	    		reviews = new ArrayList<VenueReview>();
	    		for(int i = 0; i < venues.length(); i++)
	    		{
	    			JSONObject singleJSONReview = venues.getJSONObject(i);
	    			VenueReview singleReview = new VenueReview();	    
	    			singleReview.setDBLocationId(singleJSONReview.getInt("LocationId"));
	    			singleReview.setDBReviewId(singleJSONReview.getInt("ReviewId"));
	    			singleReview.setDBUserId(singleJSONReview.getInt("UserId"));
	    			singleReview.setReviewDateTime(singleJSONReview.getInt("DateTime"));
	    			singleReview.setReviewNegative(singleJSONReview.getInt("ReviewNegative"));
	    			singleReview.setReviewPositive(singleJSONReview.getInt("ReviewPositive"));
	    			singleReview.setReviewRating(singleJSONReview.getDouble("ReviewRating"));
	    			singleReview.setReviewText(singleJSONReview.getString("ReviewText"));
	    			reviews.add(singleReview);
	    			listItems.add(singleReview.toString());
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
	protected boolean isRouteDisplayed() 
	{
		return false;
	}	
	
	public void onAddClick(View view)
	{
		Intent intent = new Intent(this, AddReview.class);					
		intent.putExtra("userId", userId);
		intent.putExtra("venueData", venue);
		intent.putExtra("checkIn", false);
		startActivity(intent); 
		return;
	}	
	
	public void AddRatingToReview(VenueReview review, ListView list, boolean usePositive)
	{
		String reviewText;
		if(usePositive == true)
		{
			reviewText = "ReviewPositive";
		}
		else
		{
			reviewText = "ReviewNegative";
		}
		
        String[][] vars = new String[2][2];
        vars[0][1] = "ri";
        vars[0][1] = venue.getId();
        vars[1][1] = "rr";
        vars[1][1] = reviewText;
                
        String response;
    	try
    	{
    		ServerConnector sc3 = new ServerConnector(vars, false, VenueActivity.this, "Contacting Dash Server ...");
    		response = sc3.execute(new String[] {  getResources().getString(R.string.rateReview) }).get(5, TimeUnit.SECONDS);    	
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
			Log.v("Error", "Venue Exception " + e.getMessage());   
			return;
    	}
    	    	
    	try 
    	{       		
    		results = ServerConnector.ConvertStringToObject(response);    		
    		String statusResponse = results.getString("status");
    		
			if (statusResponse.equals("true"))
			{	
		        RefreshReviewInfo();  		    	
		       	ArrayAdapter<VenueReview> adapter = new ArrayAdapter<VenueReview>(this, android.R.layout.simple_list_item_1, reviews);
		       	ListView ourList = (ListView)findViewById(R.id.listViewMain);
		       	ourList.setAdapter(adapter);
			}
			else
			{			
				Toast.makeText(this, "Unable to update review with rating", Toast.LENGTH_SHORT).show();				
			}
		
    	}
    	catch (JSONException e)
    	{
    		e.printStackTrace();
			Log.v("Error", "JSON Exception " + e.getMessage());   
			return;
    	}

	}	
}