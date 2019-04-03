package com.example.forecast;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread s = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                    Intent i = new Intent(MainActivity.this, MainMenu.class);
                    startActivity(i);
                    finish();
                }
                catch (Exception e) {}
            }
        };
        s.start();
    }
}
