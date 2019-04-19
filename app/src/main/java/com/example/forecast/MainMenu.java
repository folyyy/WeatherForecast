package com.example.forecast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainMenu extends AppCompatActivity {
    private RequestQueue queue;
    MyDatabase db = new MyDatabase(this);
    double[] main = new double[10];
    String[] description = new String[10];
    String[] dateTime = new String[10];
    String[] imageId = new String[10];
    ArrayList<HistoryForecast> nineDaysHistoryData;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String currDate;
    HistoryForecast historyForecast;
    HistoryPreferredW historyPreferredW;
    public static int minPreferredW = -100;
    public static int maxPreferredW = -100;
    TextView preferredWeatherText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        // If there is no internet connection, restore latest data from database
        if (!isNetworkAvailable()) {
            if (!db.isEmpty() && !db.preferredWisEmpty()) {
                Log.d("NETWORK NOT AVAILABLE", "NETWORK NOT AVAILABLE");
                historyForecast = new HistoryForecast();
                historyForecast = db.getLatestDay();
                historyPreferredW = new HistoryPreferredW();
                historyPreferredW = db.getPreferredW();
                minPreferredW = historyPreferredW.minPreferred;
                maxPreferredW = historyPreferredW.maxPreferred;
                preferredWeatherText = findViewById(R.id.getPreferredWeather);
                preferredWeatherText.setText("Радиус предпочитаемой погоды:\n (" + minPreferredW + "\u2103.." + maxPreferredW + "\u2103)");
                nineDaysHistoryData = db.get9Days();
                getLatestWeatherWithoutNetwork(historyForecast, nineDaysHistoryData);
                Log.d("MIN_MAX", "MinPreferred = " + minPreferredW + ", MaxPreferred = " + maxPreferredW);
            }
        } else {
            queue = Volley.newRequestQueue(this);
            getDay();
            getLocation();
            getWeather();
            currDate = simpleDateFormat.format(new Date());
//        getApplicationContext().deleteDatabase("HISTORY_FORECAST_DATABASE");

            // If preferred weather is in database, restore minPreferred and maxPreferred weather
            if (!db.preferredWisEmpty()) {
                historyPreferredW = new HistoryPreferredW();
                historyPreferredW = db.getPreferredW();
                minPreferredW = historyPreferredW.minPreferred;
                maxPreferredW = historyPreferredW.maxPreferred;
                preferredWeatherText = findViewById(R.id.getPreferredWeather);
                preferredWeatherText.setText("Радиус предпочитаемой погоды:\n (" + minPreferredW + "\u2103.." + maxPreferredW + "\u2103)");
            }
            Log.d("MIN_MAX", "MinPreferred = " + minPreferredW + ", MaxPreferred = " + maxPreferredW);
        }
    }

    // Checking if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
    public void getWeather() {
        String appId = "0d2f372a2ebf4f1aa0d88c504e9bb551";
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?lang=ru&days=10&lat=" + formatter.format(latitude) +
                "&lon=" + formatter.format(longitude) + "&key=" + appId;
        final ImageView descImage = findViewById(R.id.descImage);
        final TextView desc = findViewById(R.id.getDesc);
        final TextView currTemp = findViewById(R.id.getCelcius);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                        // Creating JSON objects and arrays
                        JSONArray mainOb = response.getJSONArray("data");
                        JSONObject object = mainOb.getJSONObject(0);
                        JSONObject weather = object.getJSONObject("weather");

                        // Setting an icon based on weather
                        imageId[0] = weather.getString("icon");
                        String iconURL = "https://www.weatherbit.io/static/img/icons/" + imageId[0] + ".png";
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
                        // If database does not have current day, adding a new day to the database
                        if (!db.hasDay(dateTime[0])) {
                            Log.d("Adding new day","Adding new day!");
                            historyForecast = new HistoryForecast(dateTime[0], main[0], description[0]);
                            db.addDay(historyForecast);
                            // If database has current day, updating the current day with new data
                        } else if (db.hasDay(dateTime[0])) {
                            Log.d("Updating curr day", "Updating current day with new data");
                            historyForecast = new HistoryForecast(dateTime[0], main[0], description[0]);
                            db.updateDay(historyForecast);
                        }



                        for (int i = 1; i < mainOb.length(); i++) {
                            JSONObject object1 = mainOb.getJSONObject(i);
                            JSONObject weather1 = object1.getJSONObject("weather");

                            main[i] = object1.getDouble("temp");
                            description[i] = weather1.getString("description");
                            dateTime[i] = object1.getString("datetime");
                            imageId[i] = weather1.getString("icon");
                            // If database does not have i day, adding a new day to the database
                            if (!db.hasDay(dateTime[i])) {
                                Log.d("Adding new day","Adding new day!");
                                historyForecast = new HistoryForecast(dateTime[i], main[i], description[i]);
                                db.addDay(historyForecast);
                                // If database has current day, updating i day with new data
                            } else if (db.hasDay(dateTime[i])) {
                                Log.d("Updating day with new", "Updating day with new data");
                                historyForecast = new HistoryForecast(dateTime[i], main[i], description[i]);
                                db.updateDay(historyForecast);
                            }
                        }
                    ListView dailyDataListView = findViewById(R.id.dailyDataListView);
                    CustomAdapter customAdapter = new CustomAdapter();
                    dailyDataListView.setAdapter(customAdapter);
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

    // If there is no internet connection, restore latest data from database
    public void getLatestWeatherWithoutNetwork(HistoryForecast historyForecast, ArrayList<HistoryForecast> nineDaysHistoryData) {
        final TextView latestDesc = findViewById(R.id.getDesc);
        final TextView latestTemp = findViewById(R.id.getCelcius);
        final TextView latestDate = findViewById(R.id.getDate);
        latestDesc.setText(historyForecast.dayDescription);
        latestTemp.setText(String.valueOf(historyForecast.dayTemp  + " \u2103"));
        latestDate.setText(historyForecast.dayDate);
        ListView dailyDataListView = findViewById(R.id.dailyDataListView);
        NoNetworkAdapter noNetworkAdapter = new NoNetworkAdapter();
        dailyDataListView.setAdapter(noNetworkAdapter);
    }

    // Setting minPreferred and maxPreferred weather
    public void setMinMaxPreferred(int min, int max) {
        minPreferredW = min;
        maxPreferredW = max;
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
            // Graph building option
            case R.id.action_graph: {
                // If network is available, build a graph
                if (isNetworkAvailable()) {
                    Intent i = new Intent(MainMenu.this, Graph.class);
                    i.putExtra("main", main);
                    i.putExtra("dateTime", dateTime);
                    startActivity(i);
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "График не работает без доступа к интернету",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            // Showing Forecast History option
            case R.id.action_ShowLogs: {
                Intent i = new Intent(MainMenu.this, ShowHistory.class);
                startActivity(i);
                return true;
            }
            // Saving Preferred Weather option
            case R.id.action_SavePreferredW: {
                // If database does not have preferred weather data, add preferred weather to database
                if (db.preferredWisEmpty()) {
                    historyPreferredW = new HistoryPreferredW();
                    historyPreferredW.minPreferred = minPreferredW;
                    historyPreferredW.maxPreferred = maxPreferredW;
                    db.addPreferredW(historyPreferredW);
                    preferredWeatherText = findViewById(R.id.getPreferredWeather);
                    preferredWeatherText.setText("Радиус предпочитаемой погоды:\n (" + minPreferredW + "\u2103.." + maxPreferredW + "\u2103)");
                    Toast.makeText(getApplicationContext(), "Данные успешно сохранены!", Toast.LENGTH_SHORT).show();
                    // If database has preferred weather data, update preferred weather in database
                } else if (!db.preferredWisEmpty()) {
                    historyPreferredW = db.getPreferredW();
                    historyPreferredW.minPreferred = minPreferredW;
                    historyPreferredW.maxPreferred = maxPreferredW;
                    preferredWeatherText = findViewById(R.id.getPreferredWeather);
                    preferredWeatherText.setText("Радиус предпочитаемой погоды:\n (" + minPreferredW + "\u2103.." + maxPreferredW + "\u2103)");
                    db.updatePreferredW(historyPreferredW);
                    Toast.makeText(getApplicationContext(), "Данные успешно сохранены!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            // Setting preferred weather option
            case R.id.action_preferredWeather: {
                Intent i = new Intent(MainMenu.this, PreferredWeather.class);
                startActivity(i);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Adapter for ListView when there is internet connection
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return main.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.listview_layout, null);
            ImageView imageView = view.findViewById(R.id.imageView);
            TextView textView_name = view.findViewById(R.id.textView_name);

            String iconURL = "https://www.weatherbit.io/static/img/icons/" + imageId[i] + ".png";
            Glide
                    .with(imageView)
                    .load(iconURL)
                    .into(imageView);
            textView_name.setText(dateTime[i] + " : " + main[i] + "\u2103 : " + description[i]);
            return view;
        }
    }

    // Adapter for ListView when there is no internet connection
    class NoNetworkAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return nineDaysHistoryData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.listview_layout, null);
            TextView textView_name = view.findViewById(R.id.textView_name);
            textView_name.setText(nineDaysHistoryData.get(i).toString());
            return view;
        }
    }
}
