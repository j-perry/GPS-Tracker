package com.team2.dash;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.team2.dash.entity.MapItemizedOverlay;
import com.team2.dash.entity.VenueInfo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class VenueActivity extends MapActivity 
{
	
	private MapView mapView;
	private VenueInfo venue;
	private GeoPoint venueLocation;
	private int dashId;
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        Bundle bundle = getIntent().getExtras();
        dashId = bundle.getInt("dashId");
        venue = (VenueInfo)bundle.getParcelable("venueData");
        
        Toast.makeText(this, "Checked In", Toast.LENGTH_SHORT).show();
        
        TextView textVenue = (TextView)findViewById(R.id.textViewRealName);
        textVenue.setText(venue.getVenueName());
        TextView textAddress = (TextView)findViewById(R.id.textViewRealAddress);
        textAddress.setText(venue.getVenueAddress());

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_venue, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
