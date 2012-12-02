package com.team2.dash;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.team2.dash.entity.User;
import com.team2.dash.entity.dao.DatabaseHandler;

public class AddUserActivity extends Activity {

	/**
	 * 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        
    }

    /**
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_user, menu);
        return true;
    }
    
    /**
     * 
     * @param view
     */
    public void onClick(View view) {
    	String txt;
    	User user = new User();
    	DatabaseHandler db;
    	
        switch (view.getId()) {        
        case R.id.add:
        	user.setID(0);
        	user.setServerUserID(-1);
        	user.setActive(DatabaseHandler.USER_NON_ACTIVE);

        	EditText editText = (EditText) findViewById(R.id.edit_fname);
        	txt = editText.getText().toString();       	
        	if(txt.length() == 0) {
            	Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
        		return;
        	}       	
        	user.setFname(txt);

        	editText = (EditText) findViewById(R.id.edit_sname);
        	txt = editText.getText().toString();
        	if(txt.length() == 0) {
            	Toast.makeText(this, "Please enter surname", Toast.LENGTH_SHORT).show();
        		return;
        	}       	
        	user.setSname(txt);

        	editText = (EditText) findViewById(R.id.edit_email);
        	txt = editText.getText().toString();
        	if(txt.length() == 0) {
            	Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
        		return;
        	}       	
        	user.setEmail(txt);

        	editText = (EditText) findViewById(R.id.edit_password);
        	txt = editText.getText().toString();
        	if(txt.length() == 0) {
            	Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        		return;
        	}       	
        	user.setPassword(txt);

        	editText = (EditText) findViewById(R.id.edit_age);
        	txt = editText.getText().toString();
        	
        	if( txt.length() == 0 ) {
        		txt = "0";
        	}
        	
        	user.setAge(Integer.parseInt(txt));

        	editText = (EditText) findViewById(R.id.edit_weight);
        	
        	if( txt.length() == 0 ) {
        		txt = "0";
        	}
        	
        	user.setWeight(Integer.parseInt(txt));

        	editText = (EditText) findViewById(R.id.edit_height);
        	
        	if( txt.length() == 0 ) {
        		txt = "0";
        	}
        	
        	user.setHeight(Integer.parseInt(txt));
        	db = new DatabaseHandler(this);
        	int id = db.addUser(user);  // trying to add user
        	if( id == -1 ) { 			// in case of error
        		User user2 = db.getUserByEmail(user.getEmail());
        		if( user2 != null )
                	Toast.makeText(this, "This email is already registered!", Toast.LENGTH_SHORT).show();
        		else
                	Toast.makeText(this, "Cannot add user to the database", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	db.setUserActive(id);
        	break;
        case R.id.cancel:
        	break;
        default:
        	break;
        }
        
    	finish();
    }    
}
