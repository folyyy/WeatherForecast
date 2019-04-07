package com.example.forecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

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
