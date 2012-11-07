package com.team2.dash;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;

import com.team2.dash.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.team2.dash.entity.LocationPoint;
import com.team2.dash.entity.Workout;

public class RouteMap extends MapActivity {
	
	Workout workout;
	MapView mapView;
	Paint paint ;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        configureMapPainter();
        Bundle bundle = getIntent().getExtras();
        workout =  (Workout)bundle.get("com.team2.dash.entity.Workout");
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setZoom(15);
        displayMarkers(mapView, workout);
        
        
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
	
	private void displayMarkers(MapView map, Workout workout){
		//TODO: Using workid, get list of locations from the database.
		Drawable startMarker = this.getResources().getDrawable(R.drawable.cycling);
		Drawable finishMarker = this.getResources().getDrawable(R.drawable.stop);		
		List<Overlay> mapOverlays = map.getOverlays();
		Projection projection = map.getProjection();
		
		//create start and end marker overlay
		MapItemizedOverlay startMarkerOverlay = new MapItemizedOverlay(startMarker,this);
		MapItemizedOverlay finishMarkerOverlay = new MapItemizedOverlay(finishMarker,this);
		
		
		//fetch locations from DB
		DatabaseHandler db = new DatabaseHandler(this);
		List<LocationPoint> pts = db.getAllLocations(workout.getID());		
		for(LocationPoint p : pts){
			/* get Coordinate and convert to integer */
			int x = (int) (p.getLatitude() * 1E6);
			int y = (int)(p.getLongtitude() * 1E6);	
			GeoPoint point = new GeoPoint(x,y);			
			if(pts.indexOf(p) == 0){				
				map.getController().setCenter(point);
				OverlayItem	startOverlay = new OverlayItem(point, "Start", "Start Point");
				startMarkerOverlay.addOverlay(startOverlay);
				mapOverlays.add(startMarkerOverlay);				
			}else if (pts.indexOf(p) == (pts.size() -1)){
				OverlayItem finishOverlay = new OverlayItem(point,"Finish","Finished Point");
				finishMarkerOverlay.addOverlay(finishOverlay);
				mapOverlays.add(finishMarkerOverlay);
			}else{
				Path path = new Path();
				int index = pts.indexOf(p);
				//get location details for previous location and setup GeoPoints
				LocationPoint tmpLP = pts.get(index-1);
				GeoPoint tmpGP = new GeoPoint((int)(tmpLP.getLatitude() * 1E6),(int)(tmpLP.getLongtitude() * 1E6) );
				Point point1 = new Point();
				
				//Get current location GeoPoint
				GeoPoint currentGP = new GeoPoint((int)(p.getLatitude()*1E6),(int)(p.getLongtitude()*1E6));
				Point point2 = new Point();
				
				//draw line
				projection.toPixels(tmpGP, point1);
				projection.toPixels(currentGP, point2);
				
				path.moveTo(point1.x,point1.y);
				path.lineTo(point2.x, point2.y);
				
				//get canvas
				Canvas canvas = startMarkerOverlay.getCanvas();
				canvas.drawPath(path, paint);			
			}
		}
	}
	
	private void configureMapPainter(){
		paint = new Paint();
		paint.setDither(true);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(4);
	}
	
	
}	
