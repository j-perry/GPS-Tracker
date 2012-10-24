package com.example.testthree;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testthree.entity.User;

public class AddUserActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_user, menu);
        return true;
    }
    
    
    public void onClick(View view) {
    	String 	txt;
        switch (view.getId()) {
        case R.id.add:
        	DatabaseHandler db = new DatabaseHandler(this);
        	User user = new User();
        	user.setID(0);
        	user.setActive(DatabaseHandler.USER_NON_ACTIVE);

        	EditText editText = (EditText) findViewById(R.id.edit_fname);
        	txt = editText.getText().toString();
        	if( txt.length() == 0 )
        	{
            	Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	user.setFname(txt);

        	editText = (EditText) findViewById(R.id.edit_sname);
        	txt = editText.getText().toString();
        	user.setSname(txt);

        	editText = (EditText) findViewById(R.id.edit_email);
        	txt = editText.getText().toString();
        	user.setEmail(txt);

        	editText = (EditText) findViewById(R.id.edit_age);
        	txt = editText.getText().toString();
        	if( txt.length() == 0 )
        		txt = "0";
        	user.setAge(Integer.parseInt(txt));

        	editText = (EditText) findViewById(R.id.edit_weight);
        	if( txt.length() == 0 )
        		txt = "0";
        	user.setWeight(Integer.parseInt(txt));

        	editText = (EditText) findViewById(R.id.edit_height);
        	if( txt.length() == 0 )
        		txt = "0";
        	user.setHeight(Integer.parseInt(txt));

        	db.addUser(user);
        	break;
        case R.id.cancel:
        	break;
        }
    	finish();
    }
    
}
