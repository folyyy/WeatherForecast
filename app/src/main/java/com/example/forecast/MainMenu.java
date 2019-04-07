package com.example.forecast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainMenu extends AppCompatActivity {
    private RequestQueue queue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        queue = Volley.newRequestQueue(this);
        getDay();
        getLocation();
        getWeather();
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
            Log.d("location error","Location permissions available, starting location");
        }
        LocationManager locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    // Parsing data from the API
    double[] main = new double[10];
    String[] description = new String[10];
    String[] dateTime = new String[10];
    public void getWeather() {
        String appId = "0d2f372a2ebf4f1aa0d88c504e9bb551";
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?lang=ru&days=10&lat=" + formatter.format(latitude) +
                "&lon=" + formatter.format(longitude) + "&key=" + appId;
        final ImageView descImage = findViewById(R.id.descImage);
        final TextView desc = findViewById(R.id.getDesc);
        final TextView currTemp = findViewById(R.id.getCelcius);
        final TextView dailyTemp = findViewById(R.id.parsedData);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                        // Creating JSON objects and arrays
                        JSONArray mainOb = response.getJSONArray("data");
                        JSONObject object = mainOb.getJSONObject(0);
                        JSONObject weather = object.getJSONObject("weather");

                        // Setting an icon based on weather
                        String iconId = weather.getString("icon");
                        String iconURL = "https://www.weatherbit.io/static/img/icons/" + iconId + ".png";
                    Glide
                            .with(descImage)
                            .load(iconURL)
                            .into(descImage);

                        // Getting temperature and description, putting it into the TextView's
                        main[0] = object.getDouble("temp");
                        description[0] = weather.getString("description");
                        dateTime[0] = object.getString("datetime");
                        String location = response.getString("city_name");
                        desc.setText(description[0]);
                        currTemp.setText(location + "\n" + main[0] + " \u2103");

                        for (int i = 1; i < mainOb.length(); i++) {
                            JSONObject object1 = mainOb.getJSONObject(i);
                            JSONObject weather1 = object1.getJSONObject("weather");

                            main[i] = object1.getDouble("temp");
                            description[i] = weather1.getString("description");
                            dateTime[i] = object1.getString("datetime");
                            dailyTemp.append(dateTime[i] + " : " + main[i] + "\u2103 : " + description[i] + "\n\n");

                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }); queue.add(request);
    }

    public void buildGraph() {
        Intent i = new Intent(MainMenu.this, Graph.class);
        i.putExtra("main", main);
        i.putExtra("dateTime", dateTime);
        startActivity(i);
    }

    // Finish the app on "back" key pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // Creating options menu at the top right of the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Processing selected item from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_graph: {
                Log.d("graphStuff", "graphStuff");
                buildGraph();
                return true;
            }
            case R.id.action_ShowLogs: {
                Log.d("showLogsStuff", "showLogsStuff");
                return true;
            }
            case R.id.action_saveLogs: {
                Log.d("saveLogsStuff", "saveLogsStuff");
                return true;
            }
            case R.id.action_preferredWeather: {
                Log.d("preferredWeatherStuff", "PreferredWeatherStuff");
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
