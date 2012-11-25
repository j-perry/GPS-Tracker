package com.team2.dash;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.team2.dash.entity.DirectionPathOverlay;
import com.team2.dash.entity.LocationP;
import com.team2.dash.entity.MapItemizedOverlay;

public class RouteMap extends MapActivity {
	
	/**
	 * Private members
	 */
	private List<LocationP> 	runPoints;		// 
	private MapView 			mapView;		// 
    private DatabaseHandler 	db;				// 
    private int					workoutID;		// 
    private double  			distance = 0;	//    
	
    /**
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        
        db = new DatabaseHandler(this);
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
        	workoutID = extras.getInt("workoutID");
        }
        else {
        	Toast.makeText(this, "No workout choosen...", Toast.LENGTH_SHORT).show();
        	finish();
        }
        	
        runPoints = db.getAllLocations( workoutID );
        
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        double  minLat = runPoints.get(0).getLatitude();
        double  maxLat = runPoints.get(0).getLatitude();
        double  minLong = runPoints.get(0).getLongtitude();
        double  maxLong = runPoints.get(0).getLongtitude();
        
        for( int i = 1; i < runPoints.size(); i++) {
			if( runPoints.get(i).getLatitude() < minLat ) {
				minLat = runPoints.get(i).getLatitude(); 
			}
			
			if( runPoints.get(i).getLatitude() > maxLat ) {
				maxLat = runPoints.get(i).getLatitude(); 
			}
			
			if( runPoints.get(i).getLongtitude() < minLong ) {
				minLong = runPoints.get(i).getLongtitude(); 
			}
			
			if( runPoints.get(i).getLongtitude() > maxLong ) {
				maxLong = runPoints.get(i).getLongtitude(); 
			}
		}

        
        mapView.getController().zoomToSpan( (int)((maxLat-minLat)*1E6), (int)((maxLong-minLong)*1E6) );

        mapView.getController().animateTo(new GeoPoint(  (int)((maxLat+minLat)*1E6/2), (int)((maxLong+minLong)*1E6/2) ));
       
        displayMarkers(mapView, runPoints);
              
		TextView t = (TextView)findViewById(R.id.lblDistanceCovered);
		t.setText(Integer.toString((int)distance) + " m");
		
		//TODO: Handle button click event.
		Button btnChekin = (Button)findViewById(R.id.btnCheckin);
		btnChekin.setOnClickListener(new CheckinHandler());
        
    }

    @Override
    protected void onStart() {
    	super.onStart();    	
    }
    /**
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_route_map, menu);
        return true;
    }   
    
    /**
     * 
     */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 
	 * @param map
	 * @param pts
	 */
	private void displayMarkers(MapView map, List<LocationP> pts) {
		Drawable startMarker = this.getResources().getDrawable(R.drawable.cycling);
		Drawable finishMarker = this.getResources().getDrawable(R.drawable.stop);		
		List<Overlay> mapOverlays = map.getOverlays();
		GeoPoint gp1, gp2;
		float[] dist = new float[3];    
		
		gp1 = null;
		
		//create start and end marker overlay
		MapItemizedOverlay startMarkerOverlay = new MapItemizedOverlay(startMarker,this);
		MapItemizedOverlay finishMarkerOverlay = new MapItemizedOverlay(finishMarker,this);		
					
		for( int i = 0; i < pts.size(); i++) {
			/* get Coordinate and convert to integer */
			int x = (int) (pts.get(i).getLatitude() * 1E6);
			int y = (int)(pts.get(i).getLongtitude() * 1E6);	
			gp2 = gp1;
			gp1 = new GeoPoint(x,y);	
			
			if( gp2 != null ) {
				Location.distanceBetween(gp1.getLatitudeE6()/1E6 , gp1.getLongitudeE6()/1E6, 
										gp2.getLatitudeE6()/1E6, gp2.getLongitudeE6()/1E6, dist);
				distance += dist[0];  
				mapView.getOverlays().add(new DirectionPathOverlay(gp1, gp2));
			}
			
			if( i==0 ) {				
				map.getController().setCenter(gp1);
				OverlayItem	startOverlay = new OverlayItem(gp1, "Start", "");
				startMarkerOverlay.addOverlay(startOverlay);
				mapOverlays.add(startMarkerOverlay);
			} 
			else if ( i == (pts.size()-1)) {
				OverlayItem finishOverlay = new OverlayItem(gp1,"Finish", "");
				finishMarkerOverlay.addOverlay(finishOverlay);
				mapOverlays.add(finishMarkerOverlay);
			} 
			else {				
				//TODO: Add points for intermediate locations here
			}
		}
	}
	
	public void handleResponse (String response)
	{
		Intent intent = new Intent(this, CheckIn.class);
		intent.putExtra("locationJson", response);
		intent.putExtra("workoutID", workoutID);
		startActivity(intent);
	}
	
	private class CheckinHandler implements OnClickListener
	{
		public void onClick(View v) {
			try
			{
				if(runPoints != null || runPoints.size() > 0)
				{
					LocationP checkinpoint = runPoints.get(runPoints.size() - 1);
					String vars[][] = new String[3][2];
					vars[0][0] = "";
					vars[0][1] = "" + checkinpoint.getLatitude();
					vars[1][0] = "";
					vars[1][1] = "" + checkinpoint.getLongtitude();
					vars[2][0] = "";
					vars[2][1] = "15";					
					ServerConnector sc = new ServerConnector(vars, true, RouteMap.this, "Contacting Server ...");
			    	String response;
			    	try
			    	{
			    		response = sc.execute(new String[] { getResources().getString(R.string.getVenues) }).get();
			    		handleResponse(response);
			    	} 
			    	catch (Exception e)
			    	{
						Log.v("Error", "Four Square Exception " + e.getMessage());   
						return;
			    	}
				}
				else
				{
					Toast.makeText(RouteMap.this,"No available run points", Toast.LENGTH_SHORT).show();
				}
			}
			catch(IndexOutOfBoundsException ex)
			{
				Toast.makeText(RouteMap.this,"No available workout data",Toast.LENGTH_SHORT).show();
			}
		}
	}	
}