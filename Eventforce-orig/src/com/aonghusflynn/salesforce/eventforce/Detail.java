package com.aonghusflynn.salesforce.eventforce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class Detail extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        
        Intent intent = getIntent();
        
        String title = intent.getStringExtra(MainActivity.EVENT_TITLE);
        String description = intent.getStringExtra(MainActivity.EVENT_DESCRIPTION);
        Log.d("Detail Activity", title);
        Log.d("Detail Activity", description);
        TextView titleView = (TextView)findViewById(R.id.title);
        titleView.setText(title);
        TextView descView = (TextView)findViewById(R.id.description);
        descView.setText(description);
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail, menu);
        return true;
    }
}
