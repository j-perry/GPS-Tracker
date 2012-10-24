package com.example.testthree;

import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.testthree.entity.User;

public class UserActivity extends ListActivity {

    List<User> values;
    DatabaseHandler db;
    
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
     
    }

    @Override
    public void onRestart() {
        super.onRestart();

        @SuppressWarnings("unchecked")      
        ArrayAdapter<User> adapter = (ArrayAdapter<User>) getListAdapter();
        values = db.getAllUsers();
        adapter.clear();
        for(int i=0; i<values.size(); i++)
        	adapter.add(values.get(i));
        adapter.notifyDataSetChanged();
/*        values = db.getAllUsers();

        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this,
            android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter); */
     
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	User user = (User)l.getItemAtPosition(position);
    	db.setUserActive(user.getID());
    	String txt = "User " + user + " selected";
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    	finish();
    }
    
    
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.add:
            Intent intent = new Intent(this, AddUserActivity.class);
            startActivityForResult(intent, 0);
          break;
        case R.id.cancel:
        	finish();
          break;
        }
    }


}
