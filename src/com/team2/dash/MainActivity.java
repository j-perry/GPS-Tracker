package com.team2.dash;

import com.team2.dash.entity.User;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	/**
	 * Private members
	 */
	private User				activeUser = null;		// keep active user in here
    private DatabaseHandler 	db;						// handles database operations 

    /**
     * 
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(this);
        
        // display the main menu
        setContentView(R.layout.activity_main);

        fastTost();   
   }

   /**
    * 
    */
   @Override
   public void onResume() {
	   super.onResume();
            
       activeUser = db.getActiveUser();
       if( activeUser == null) {
    	   String txt = "Please choose active user\n or create a new one!";
    	   Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
           Intent intent = new Intent(this, UserActivity.class); 
           startActivity(intent);
       }
       
       TextView tv = (TextView)findViewById(R.id.textView1);
       tv.setText( "Welcome " + activeUser );
   }
   
   @Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
}

   /**
    * 
    */
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.activity_main, menu);
       return true;
   }
   
   /**
    * Loads the splash screen
    */
   public void fastTost(){
       LinearLayout v = new LinearLayout(this);
       ImageView iv = new ImageView(this);
       
       // 
       iv.setImageResource(R.drawable.dash_logo);

       v.addView(iv);
       //populate layout with your image and text or whatever you want to put in here

       Toast toast = new Toast(getApplicationContext());
       toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
       toast.setDuration(Toast.LENGTH_SHORT);
       toast.setView(v);
       toast.show();    	
   }
    
   /**
    * 
    * @param view
    */
   public void onUserClick(View view) {
       Intent intent = new Intent(this, UserActivity.class); 
       startActivity(intent);
   }

   /**
    * 
    * @param view
    */
   public void onNewWorkoutClick(View view) {
	   try{
	   Intent intent = new Intent(this, TrackerActivity.class);
       intent.putExtra("activeUserID", activeUser.getID());        
       startActivity(intent);
	   }catch(NullPointerException exception){
		   Toast.makeText(this, "No active user selected", Toast.LENGTH_LONG).show();
		   Log.e(MainActivity.class.toString(), "Default user for runtime not selected-"+exception.getLocalizedMessage());
	   }catch(Exception ex){
		   Log.e(MainActivity.class.toString(),ex.getLocalizedMessage());
	   }
   
   }

   /**
    * 
    * @param view
    */
   public void onWorkoutClick(View view) {
       Intent intent = new Intent(this, WorkoutActivity.class);
       intent.putExtra("activeUserID", activeUser.getID());        
   	   startActivity(intent);
   }

   /**
    * 
    * @param view
    */
   public void onTestClick(View view) {
       Intent intent = new Intent(this, TestActivity.class);
       intent.putExtra("activeUserID", activeUser.getID());        
       startActivity(intent);
   }   
}
