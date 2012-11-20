package com.team2.dash;

import java.util.List;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.team2.dash.R;
import com.team2.dash.entity.LocationP;

public class LocationActivity extends ListActivity {
	
	/**
	 * Private members
	 */
    List<LocationP> values;		//
    DatabaseHandler db;			//
    int				workoutID;	//
    
    /**
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_location);
        
        db = new DatabaseHandler(this);
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
        	workoutID = extras.getInt("workoutID");
        }
        else {
        	Toast.makeText(this, "No workout choosen...", Toast.LENGTH_SHORT).show();
        	finish();
        }
        	
        values = db.getAllLocations( workoutID );

        ArrayAdapter<LocationP> adapter = new ArrayAdapter<LocationP>(this,
            android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        
    }
    
    /**
     * 
     */
    @Override
    public void onRestart() {
        super.onRestart();

        refreshList();     
    }

    /**
     * 
     */
    @Override
    public void onListItemClick( ListView l, View v, int position, long id) {
    	LocationP Location = (LocationP)l.getItemAtPosition(position);
    	String txt = "Location " + Location + " selected";
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
//    	finish();
    }
    
    /**
     * 
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.add:
        	break;
        case R.id.cancel:
        	finish();
        	break;
        default:
        	break;
        }
    }

    /**
     * 
     */
    private void refreshList() {
        @SuppressWarnings("unchecked")      
        ArrayAdapter<LocationP> adapter = (ArrayAdapter<LocationP>) getListAdapter();
        values = db.getAllLocations(workoutID);
        adapter.clear();
        
        for(int i = 0; i < values.size(); i++) {
        	adapter.add(values.get(i));
        }
        
        adapter.notifyDataSetChanged();   	
    }
}
