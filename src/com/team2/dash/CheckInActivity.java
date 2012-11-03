package com.team2.dash;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CheckInActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_check_in, menu);
        return true;
    }
}
