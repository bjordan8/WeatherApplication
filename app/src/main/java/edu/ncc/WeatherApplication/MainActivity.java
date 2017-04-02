package edu.ncc.WeatherApplication;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity implements View.OnClickListener {
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_CITY = "name";
    private static final String TAG_TEMP = "temp";
    private static final String TAG_TEMP_MIN = "temp_min";
    private static final String TAG_TEMP_MAX = "temp_max";
    private static final String TAG_DESCRIPTION = "description";
    private Button zipbtn;
    private String zip;
    ArrayList<HashMap<String, String>> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zipbtn = (Button)findViewById(R.id.zipButton);
        zipbtn.setOnClickListener(this);

        locationList = new ArrayList<HashMap<String, String>>();
        new GetLocations("10001").execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.zipButton){
            Intent myIntent = new Intent(MainActivity.this, zip_code_entry.class);
            startActivityForResult(myIntent, 1);
            //new GetLocations(zip).execute();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                zip = result;
                Log.i("ZIP ", "Zip code = " + zip);
                new GetLocations(zip).execute();
                //result = getWeather.onPostExecute();
                //Log.i("Gestting Async", "The weather report = " + result);
            }
        }
    }

    //this AsyncTask allows to outsource current task by creating a second thread
    private class GetLocations extends AsyncTask<Void, Void, Void> {
        String result = "";
        String jsonStr = null; //used to keep track of JSON text
        String zipCode; // used to store zip code tp be searched
        public GetLocations(String zip){
            zipCode = zip;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpURLConnection urlConnection;
            BufferedReader reader;
            String format = "json";
            String units = "imperial";
            int numDays = 5;

            try {
                //Create URL for OpenWeatherMap Query
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast";
                final String QUERY_PARAM = "zip";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtURI = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, zipCode)
                        // .appendPath(",us")
                        .appendQueryParameter("us", "")
                        .appendQueryParameter(APPID_PARAM, "00f04f5d41e3286008fc982ae7e15852")
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();
                Log.v("Built URI", " = " + builtURI + "");
                // take url as a string, assign API key, and then apply string to convert into a url
                String website = "http://api.openweathermap.org/data/2.5/forecast?zip=11530,us&APPID=00f04f5d41e3286008fc982ae7e15852&units=imperial&cnt=7";
                String apiKey = "&APPID=" + "00f04f5d41e3286008fc982ae7e15852";
                //Create URL object
                URL url = new URL(builtURI.toString());
                //URL url = new URL(website.concat(apiKey));
                // Create the request to OpenWeatherMap, and open the urlConnection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String s;
                while ((s = reader.readLine()) != null) {
                    result += s;
                }
            } catch (Exception e) {
                Log.i("HttpAsyncTask", "EXCEPTION: " + e.getMessage());
            }

            Log.i("PARSING", "Returned string: " + result);
            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            super.onPostExecute(r);
            locationList.clear();
            for (int i = 0; i < 5; i++) {
                if (result != null) {
                    Log.i("Parsing", "about to start" + result);
                    try {
                        JSONObject jsonObj = new JSONObject(result);

                        JSONObject city = jsonObj.getJSONObject("city");
                        JSONObject coord = city.getJSONObject("coord");
                        String lat = coord.getString("lat");
                        Log.i("PARSING", " lat: " + lat);
                        String lon = coord.getString("lon");
                        Log.i("PARSING", " lat: " + lat + " lon: ");
                        String info = city.getString("name");
                        Log.i("PARSING", "name: " + info + " lat: " + lat + " lon: " + lon);
                        TextView textView = (TextView) findViewById(R.id.name);
                        textView.setText(info);
                        textView = (TextView) findViewById(R.id.latitude);
                        textView.setText(lat);
                        textView = (TextView) findViewById(R.id.longitude);
                        textView.setText(lon);

                        //get temperature data
                        JSONArray list = jsonObj.getJSONArray("list");

                        JSONObject tempInfo = list.getJSONObject(i);// gets element zero
                        JSONObject tempMain = tempInfo.getJSONObject("main");
                        String temp = tempMain.getString("temp");
                        String tempMin = tempMain.getString("temp_min");
                        String tempMax = tempMain.getString("temp_max");
                        //get description
                        JSONArray weather = tempInfo.getJSONArray("weather");
                        JSONObject descriptionInfo = weather.getJSONObject(0);
                        String tempDescription = descriptionInfo.getString("description");

                        HashMap<String, String> location = new HashMap<String, String>();


                            location.put(TAG_LATITUDE, lat);
                            location.put(TAG_LONGITUDE, lon);
                            location.put(TAG_CITY, info);

                        location.put(TAG_TEMP, temp);
                        location.put(TAG_TEMP_MIN, tempMin);
                        location.put(TAG_TEMP_MAX, tempMax);
                        location.put(TAG_DESCRIPTION, tempDescription);
                        Log.i("PARSING", "location: " + location.toString());
                        locationList.add(location);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
                    ListAdapter adapter = new SimpleAdapter(
                            MainActivity.this, locationList, //location list is the
                            R.layout.list_item, //layout for each item
                            new String[]{TAG_CITY, TAG_LATITUDE, TAG_LONGITUDE, TAG_TEMP, TAG_TEMP_MIN, TAG_TEMP_MAX, TAG_DESCRIPTION},
                            new int[]{R.id.name, R.id.latitude, R.id.longitude, R.id.temperature, R.id.min, R.id.max, R.id.description});

                    setListAdapter(adapter);

            }
        }

    }
}
