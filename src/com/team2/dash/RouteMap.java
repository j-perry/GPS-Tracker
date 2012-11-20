package com.team2.dash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Int2;
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
    private static String serviceEndPoint = "";
	
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
    	serviceEndPoint = getResources().getString(R.string.webServiceEndPoint);
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
	
	private class CheckinHandler implements OnClickListener{

		public void onClick(View v) {
			try{
				if(runPoints != null || runPoints.size() > 0){
				LocationP checkinpoint = runPoints.get(runPoints.size() - 1);
				//Query webservice and fetch result and display in list. Then allow checkin
				String latLng =   checkinpoint.getLatitude() + "," + checkinpoint.getLongtitude();
				//get foursquare url 
				String webServiceEndPoint = getResources().getString(R.string.webServiceEndPoint);
				FourSquareThread fsq = new FourSquareThread(latLng, FourSquareThread.GET_PLACES,RouteMap.this, "contacting foursquare ...");
				fsq.execute(new String[]{webServiceEndPoint});				
				}else{
					Toast.makeText(RouteMap.this,"No available run points", Toast.LENGTH_SHORT).show();
				}
			}catch(IndexOutOfBoundsException ex){
				Toast.makeText(RouteMap.this,"No available workout data",Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void handleResponse (String response){
		Intent intent = new Intent(this,CheckIn.class);
		intent.putExtra("locationJson", response);
		startActivity(intent);
	}
	
	//TODO: Convert this to use the ServerConnector class
	
	private class FourSquareThread extends AsyncTask<String, Integer, String>{

		public static final int POST_CHECKIN = 1;
		public static final int GET_PLACES = 2;
		
		private static final  String TAG = "FourSquareThread";
		//connection time out
		private static final int CONN_TIMEOUT = 3000;
		
		//socket timeout 
		private static final int SOCKET_TIMEOUT = 5000;
		
		
		private ProgressDialog pDlg = null;
		private Context mContext = null;
		private String processMessage = "processing ...";
		private String latLng = "";
		private int taskType = GET_PLACES;
		
		
		
		
		
		public FourSquareThread(String latLng,int taskType, Context mContext,
				String processMessage) {
			super();
			this.latLng = latLng;
			this.mContext = mContext;
			this.processMessage = processMessage;
		}
		
		private void showProgressDialog(){
			pDlg = new ProgressDialog(mContext);
			pDlg.setMessage(processMessage);
			pDlg.setProgressDrawable(WallpaperManager.getInstance(mContext).getDrawable());
			pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDlg.setCancelable(false);
			pDlg.show();
		}




		@Override
		protected String doInBackground(String... params) {
			String result = "";
			String url = params[0];
			
			HttpResponse response = doResponse(url);
			if(response == null)
				return result;
			try{
				
				result = inputStreamToString(response.getEntity().getContent());
				handleResponse(result);
				
			}catch (IllegalStateException ex){
				Log.e(TAG, ex.getLocalizedMessage());
			}catch (IOException ex) {
				Log.e(TAG, ex.getLocalizedMessage());
			}
			
			
			return result;
		}
		
		 private String inputStreamToString(InputStream is) {
			 
	            String line = "";
	            StringBuilder total = new StringBuilder();
	 
	            // Wrap a BufferedReader around the InputStream
	            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	 
	            try {
	                // Read response until the end
	                while ((line = rd.readLine()) != null) {
	                    total.append(line);
	                }
	            } catch (IOException e) {
	                Log.e(TAG, e.getLocalizedMessage(), e);
	            }
	 
	            // Return full string
	            return total.toString();
	        }
	 
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			handleResponse(result);
			pDlg.dismiss();
		}
		
		private  HttpResponse doResponse (String url){
					
			
			//configure connection parameters ..connection timeouts
			HttpParams httpp = new BasicHttpParams();			
			HttpConnectionParams.setConnectionTimeout(httpp,CONN_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpp, SOCKET_TIMEOUT);
			
			HttpClient httpClient = AndroidHttpClient.newInstance("dashmobileapp");
			HttpResponse httpResponse = null;
			try{
			switch(taskType){
			//fetch locations by the ll
			case GET_PLACES:
				url += getResources().getString(R.string.getVenues);
				//set lat and lng 
				url += "/"+latLng.substring(0, latLng.lastIndexOf(','))+"/"+ latLng.substring(latLng.lastIndexOf(',')+1,latLng.length());
				url += "/"+15;
				HttpGet httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);				
				break;
			//post checkin data to server
			case POST_CHECKIN:
					
				break;
			}
			}catch(ClientProtocolException ex){
				Log.e(TAG, ex.getLocalizedMessage());
			}catch(IOException ex){
				Log.e(TAG, ex.getLocalizedMessage());
			}
			return httpResponse;
			
		}
		
	}
}
