package com.team2.dash;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FollowActivity extends Activity {
	
	int followId; //This is going to be for the user we have displayed

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        
        Bundle bundle = getIntent().getExtras();
        followId = bundle.getInt("followId");        
        
        //TODO: Go off and get data.
        
        //TODO: Show list with who this user is following
        
        //TODO: Follow button        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_follow, menu);
        return true;
    }
}
