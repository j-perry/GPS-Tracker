package com.team2.dash;

import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.team2.dash.entity.User;
import com.team2.dash.entity.Workout;

public class WorkoutActivity extends ListActivity {

	/**
	 * Private members
	 */
    private List<Workout> 	values;			// displays list of venues visited during workout
    private DatabaseHandler db;				// handles database operations
    private int				activeUserID;	// stores the user's ID
    private User			activeUser;
    
    /**
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // display the Workout screen
        setContentView(R.layout.activity_workout);
        
        db = new DatabaseHandler(this);
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
        	activeUserID = extras.getInt("activeUserID");
        }        
        else {
        	User user = db.getActiveUser();
        	activeUserID = user.getID();
        }

        values = db.getAllWorkouts( activeUserID );

        ArrayAdapter<Workout> adapter = new ArrayAdapter<Workout>(this,
            android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        
        ListView lv = getListView();
        lv.setOnItemLongClickListener(
        	new AdapterView.OnItemLongClickListener() {
        		public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
        			return onLongListItemClick(view, position, id) ;
        		}
        	}
        );
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
    	Workout workout = (Workout)l.getItemAtPosition(position);
    	String txt = "Workout " + workout + " selected";
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, LocationActivity.class);
        Intent intent = new Intent(this, RouteMap.class);
        intent.putExtra("workoutID", workout.getID());        
    	startActivity(intent);
    }
    
    /**
     * 
     * @param v
     * @param position
     * @param id
     * @return
     */
    public boolean  onLongListItemClick(View v, int position, long id) {
    	ListView l = getListView();
    	Workout workout = (Workout)l.getItemAtPosition(position);
    	db.deleteWorkout(workout.getID());
    	refreshList();
    	String txt = "Workout " + workout + " deleted";
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    	return true;
    }
    
    /**
     * 
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.add:
//        	Workout workout = new Workout( 0, activeUserID, 0, 0, 0, DateFormat.format("MM/dd/yy h:mmaa", new java.util.Date()).toString() );
//        	db.addWorkout(workout);
//        	refreshList();
        	
        	// create a new workout launch object
            Intent intent = new Intent(this, TrackerActivity.class);
            
            // launch new workout
            startActivityForResult(intent, 0);
            
        	// used for testing: Toast.makeText(this, "New Workout Clicked", Toast.LENGTH_SHORT).show();
        	break;
        case R.id.cancel:
        	// return to the main menu
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
        ArrayAdapter<Workout> adapter = (ArrayAdapter<Workout>) getListAdapter();
        values = db.getAllWorkouts(activeUserID);
        adapter.clear();
        
        for(int i=0; i<values.size(); i++) {
        	adapter.add(values.get(i));
        }
        
        adapter.notifyDataSetChanged();   	
    }
}
