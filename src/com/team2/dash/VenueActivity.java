package com.team2.dash;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.team2.dash.entity.MapItemizedOverlay;
import com.team2.dash.entity.VenueInfo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class VenueActivity extends MapActivity 
{
	
	private GeoPoint venueLocation;
	private MapView mapView;
	private VenueInfo venue;	
	private int dashId;
	private int userId;
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        Bundle bundle = getIntent().getExtras();
        dashId = bundle.getInt("dashId");
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

        //TODO: Get Reviews and Checkins
        //TODO: On list view, show positive + negative review numbers 
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

	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
	
	public void onAddClick(View view)
	{
		Intent intent = new Intent(this, AddReview.class);					
		intent.putExtra("dashId", dashId);
		intent.putExtra("userId", userId);
		intent.putExtra("venueData", venue);
		intent.putExtra("checkIn", false);
		startActivity(intent); 
		return;
	}	
}