package com.team2.dash;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;

import com.team2.dash.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.team2.dash.entity.LocationPoint;

public class RouteMap extends MapActivity {
	
	ArrayList<LocationPoint> runPoints ;
	MapView mapView;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        Bundle bundle = getIntent().getExtras();
        runPoints =  bundle.getParcelableArrayList("com.team2.dash.entity.LocationPoint");
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setZoom(15);
        displayMarkers(mapView, runPoints);
        
        
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
	
	private void displayMarkers(MapView map, ArrayList<LocationPoint> pts){
		Drawable startMarker = this.getResources().getDrawable(R.drawable.cycling);
		Drawable finishMarker = this.getResources().getDrawable(R.drawable.stop);		
		List<Overlay> mapOverlays = map.getOverlays();	
		
		//create start and end marker overlay
		MapItemizedOverlay startMarkerOverlay = new MapItemizedOverlay(startMarker,this);
		MapItemizedOverlay finishMarkerOverlay = new MapItemizedOverlay(finishMarker,this);		
		
		for(LocationPoint p : pts){
			/* get Coordinate and convert to integer */
			int x = (int) (p.getLatitude() * 1E6);
			int y = (int)(p.getLongitude() * 1E6);	
			GeoPoint point = new GeoPoint(x,y);			
			if(p.isStartLocation()){				
				map.getController().setCenter(point);
				OverlayItem	startOverlay = new OverlayItem(point, "Start", p.getDescription());
				startMarkerOverlay.addOverlay(startOverlay);
				mapOverlays.add(startMarkerOverlay);
			}else if (p.isFinishLocation()){
				OverlayItem finishOverlay = new OverlayItem(point,"Finish",p.getDescription());
				finishMarkerOverlay.addOverlay(finishOverlay);
				mapOverlays.add(finishMarkerOverlay);
			}else{
				//TODO: Add points for intermediate locations here
			}
		}
	}

}
