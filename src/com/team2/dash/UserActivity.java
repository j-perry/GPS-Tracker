package com.team2.dash;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.team2.dash.entity.User;
import com.team2.dash.entity.VenueInfo;

public class UserActivity extends ListActivity {

	/**
	 * Private members
	 */
    private List<User> 		values;		// 
    private DatabaseHandler db;			// 
    
    /**
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        
        db = new DatabaseHandler(this);
        values = db.getAllUsers();

        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this,
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
    	User user = (User)l.getItemAtPosition(position);
    	db.setUserActive(user.getID());
    	String txt = "User " + user + " selected";
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    	finish();
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
    	User user = (User)l.getItemAtPosition(position);
    	db.deleteUser(user.getID());
    	refreshList();
    	String txt = "User " + user + " deleted";
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    	
    	return true;
    }

    
    /**
     * Registers active user on the server
     * 
      * @return true if success and false in case of error
     */
   boolean registerUserOnServer()
    {
    	
    	User user = new User();
    	DatabaseHandler db = new DatabaseHandler(this);
    	user = db.getActiveUser();
    	String txt;
    	
		String vars[][] = new String[5][2];
		vars[0][0] = "firstname";
		vars[0][1] = user.getFname();
		vars[1][0] = "surname";
		vars[1][1] = user.getSname();
		vars[2][0] = "email";
		vars[2][1] = user.getEmail();					
		vars[3][0] = "username";
		vars[3][1] = user.getEmail();					
		vars[4][0] = "password";
		vars[4][1] = user.getPassword();					
		ServerConnector sc = new ServerConnector(vars, true, UserActivity.this, "Contacting Server ...");
    	String response;
    	try	{
    		response = sc.execute(new String[] { getResources().getString(R.string.registerUser) }).get();
    		Log.v("Response", response);
    	} 
    	catch (Exception e)	{
			Log.v("Error", " Registering User  " + e.getMessage());   
			return(false);
    	}

		JSONObject results = ServerConnector.ConvertStringToObject(response);

		if (results == null) {
			Toast.makeText(this, "Unable to pull results", Toast.LENGTH_SHORT).show();
			return(false);
		}

		try
    	{    		
			String status = results.getString("status");

    		if( status.equals("true") ){
    			JSONObject userdata = results.getJSONObject("userdata");
    			int userID = userdata.getInt("userid");
    			user.setServerUserID(userID);
    			db.updateUser(user);
    			txt = user + "registered on the server";
    			Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    		}
    		else{
    			txt = "Error: " + status + "\n" + results.getString("error");
    			Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    			return(false);
    		}
    			
    	}      	
	    catch (JSONException e)
	    {
	    	Toast.makeText(this, "Unable to pull results", Toast.LENGTH_SHORT).show();
			Log.v("Error", "JSONException " + e.getMessage());    			
			return( false );
	    }    	  

    	
    	return( true );
    }
    
    /**
     * 
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.add:
            Intent intent = new Intent(this, AddUserActivity.class);
            startActivityForResult(intent, 0);
            break;
        case R.id.add_on_server:
            registerUserOnServer();
        	break;
        default:
        	break;
        }
    }

    /**
     * 
     */
    private void refreshList(){
        @SuppressWarnings("unchecked")      
        ArrayAdapter<User> adapter = (ArrayAdapter<User>) getListAdapter();
        values = db.getAllUsers();
        adapter.clear();
        
        for(int i = 0; i < values.size(); i++) {
        	adapter.add(values.get(i));
        }
        
        adapter.notifyDataSetChanged();   	
    }    
}
