package com.team2.dash;

import com.google.android.maps.MapActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class VenueActivity extends MapActivity 
{

	private String uniqueId;
	private int dashId;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        Bundle bundle = getIntent().getExtras();
        dashId = bundle.getInt("dashId");
        uniqueId = bundle.getString("id");
        
        Toast.makeText(this, "Checked In", Toast.LENGTH_SHORT).show();
        
        //TODO: ServerConnector to get Venue Information
        
        
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
