package com.example.testthree;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.os.Bundle;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Menu;

public class RouteMapActivity extends MapActivity  {

	MapView mapView ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setZoom(15);
        List<Overlay> mapOverLays = mapView.getOverlays();
        Drawable startmarker = this.getResources()	.getDrawable(R.drawable.cycling);
        
        MapItemizedOverlay startMarkerOverlay = new MapItemizedOverlay(startmarker,this);
        MapItemizedOverlay endMarkerOverlay = new MapItemizedOverlay(startmarker,this);
        
        //get coord data to display on map
        Bundle bundle = getIntent().getExtras();
        int x = bundle.getInt("startingLat");
        int y = bundle.getInt("startingLong");
        
        GeoPoint point1 = new GeoPoint(x,y);
        mapView.getController().setCenter(point1);
        OverlayItem raceStart = new OverlayItem(point1, "Starting point", "You started running here");
        
        //get finished data
        //Todo: get finished data from intent data
        int x1 = (int)(50.8192 * 1E6);
        int y1 =  (int)(-0.132869 * 1E6);
        GeoPoint point2 = new GeoPoint(x1,y1 );
        OverlayItem raceEnds = new OverlayItem(point2, "End point", "This is your finish line");        
        
        startMarkerOverlay.addOverlay(raceStart);
        endMarkerOverlay.addOverlay(raceEnds);
        
        //plot markers on mapview
        mapOverLays.add(startMarkerOverlay);
        mapOverLays.add(endMarkerOverlay);
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_route_map, menu);
        return true;
    }

    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
