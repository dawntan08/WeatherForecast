package com.example.liway.weatherforecast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    private ProgressDialog pDialog;

    String name;

    //URL to get weather JSON
    private static String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=manila";

    //JSON Node names
    private static final String TAG_NAME = "name";
    private static final String TAG_COUNTRY = "country";
    private static final String TAG_DAY = "day";
    private static final String TAG_MIN = "min";
    private static final String TAG_MAX = "max";
    private static final String TAG_NIGHT = "night";
    private static final String TAG_EVE = "eve";
    private static final String TAG_MORN = "morn";
    private static final String TAG_LIST = "list";

    JSONArray wList = null;

    ArrayList<HashMap<String, String>> weatherList;

    ListView lv;
    TextView myView;


    //random comment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("onCreate");
        weatherList = new ArrayList<HashMap<String, String>>();
        lv = (ListView)findViewById(R.id.mylist);
        myView  = (TextView)findViewById(R.id.city);

        new GetWeather().execute();

    }

    private class GetWeather extends AsyncTask<Void, Void, Void>{

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            Log.d("Position: ", "onPreExecute");
        }

        protected Void doInBackground(Void... arg0){
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Position: ", "doInBackGround");
            Log.d("Response: ", "> " + jsonStr);

            if(jsonStr != null){
                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject city = jsonObj.getJSONObject("city");
                    name = city.getString(TAG_NAME);
                    String country = city.getString(TAG_COUNTRY);
                    wList = jsonObj.getJSONArray(TAG_LIST);

                    for(int i = 0; i < wList.length(); i++){
                        JSONObject b = wList.getJSONObject(i);
                        JSONObject c = b.getJSONObject("temp");
                        String day = c.getString(TAG_DAY);
                        String min = c.getString(TAG_MIN);
                        String max = c.getString(TAG_MAX);
                        String night = c.getString(TAG_NIGHT);
                        String eve = c.getString(TAG_EVE);
                        String morn = c.getString(TAG_MORN);

                        HashMap<String, String> pWeatherList = new HashMap<String, String>();
                        pWeatherList.put(TAG_DAY, day);
                        pWeatherList.put(TAG_MIN, min);
                        pWeatherList.put(TAG_MAX, max);
                        pWeatherList.put(TAG_NIGHT, night);
                        pWeatherList.put(TAG_EVE, eve);
                        pWeatherList.put(TAG_MORN, morn);

                        weatherList.add(pWeatherList);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, weatherList,R.layout.list_layout, new String[] {TAG_DAY, TAG_MIN, TAG_MAX, TAG_NIGHT, TAG_EVE, TAG_MORN}, new int[] {R.id.day, R.id.min, R.id.max, R.id.night, R.id.eve, R.id.morn});

            lv.setAdapter(adapter);
            myView.setText(name);

            Log.d("Position: ", "onPostExecute");
        }
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
}
