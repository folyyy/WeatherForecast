package com.example.forecast;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainMenu extends AppCompatActivity {

    TextView currTemp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        currTemp = (TextView) findViewById(R.id.getTemp);
        getDay();
        getLocation();
        getWeather();
//        parseData();
    }
    // Getting the current day
    public void getDay() {
        Locale ru = new Locale("ru");
        TextView currDate = findViewById(R.id.getDate);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL, ru).format(calendar.getTime());
        currDate.setText(currentDate);
    }

    // Getting the user's location
    double longitude;
    double latitude;
    NumberFormat formatter = new DecimalFormat("#0.000");
    private static final int REQUEST_LOCATION = 123;
    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_LOCATION);
        } else {
            System.out.println("Location permissions available, starting location");
        }
        TextView currLoc = findViewById(R.id.getLocation);
        LocationManager locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        currLoc.setText("Longitude = " + formatter.format(longitude) + "\n Latitude = " + formatter.format(latitude));
    }


    // Parse data using JSON
    Button updateData;
    public static TextView data;
    public void parseData() {
        updateData = (Button) findViewById(R.id.updateButton);
        data = (TextView) findViewById(R.id.parsedData);
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeather();
//                ParseData process = new ParseData();
//                process.execute();
            }
        });
    }

    public void getWeather() {
//        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + formatter.format(latitude) + "&lon=" +
//                formatter.format(longitude) + "&APPID=e1659ad479366bc09a1d0eadad4ce8b5"
        Log.d("MyAPP", "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasd");
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=37.422&lon=-122.084&APPID=e1659ad479366bc09a1d0eadad4ce8b5";
        JsonObjectRequest JO = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");
                    double temp_int = Double.parseDouble(temp);
                    double celsius = (temp_int - 32) / 1.8000;
                    celsius = Math.round(celsius);
                    int i = (int)celsius;
                    currTemp.setText(String.valueOf(i));
                    Log.d("MyAPP", "basbasbas");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("wtf", "wtf???");
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(JO);
    }

    // Finish the app on "back" key pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // Creating a menu on top right of the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        TextView infoTextView = (TextView) findViewById(R.id.textView);
//
//        switch(id) {
//            case R.id.action_graph:
//                infoTextView.setText("GRAPH");
//            case R.id.action_saveLogs:
//                infoTextView.setText("SAVED LOGS");
//            case R.id.action_preferredWeather:
//                infoTextView.setText("CHOSEN PREFERRED WEATHER");
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }




}
