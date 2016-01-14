package com.google.xkc.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private String[] forecastArray = {
            "Today-Sunny-88/63",
            "Tomorrow-Foggy-75/43",
            "Friday-Cloudy-80/55",
            "Saturday-Sunny-86/60",
            "Sunday-Snowy-60/48",
            "Monday-Foggy-77/56",
            "Tuesday-Sunny-78-66"
    };

    private ArrayAdapter<String> mForecastAapter;
    private ListView listview_forecast;
    private List<String> weekForecast;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to report this fragment has menu options
        //add this line in order for this fragment to handle options menu events
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);

        initView(rootView);

        initData();

        listview_forecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecastEntry = weekForecast.get(position);
//                Toast.makeText(getActivity(),position+"is clicked, forecastEntry is"+forecastEntry,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecastEntry);
                startActivity(intent);

            }
        });

        return rootView;
    }

    private void initView(View rootView) {
        listview_forecast = (ListView) rootView.findViewById(R.id.listview_forecast);
    }

    private void initData() {
        weekForecast = new ArrayList<>();
        mForecastAapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);

        listview_forecast.setAdapter(mForecastAapter);
    }


    class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String query = params[0];
            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, query)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, Const.OPEN_WEATHER_MAP_API_KEY)
                        .build();
//                Log.i(LOG_TAG,builtUri.toString());
//
//                StringBuilder urlBuilder = new StringBuilder(FORECAST_BASE_URL);
//                urlBuilder.append("?q="+params[0]);
//                urlBuilder.append("&mode=json&units=metric&cnt=7&APPID=b49b573ea5b0e5150417928348d05344");
//                Log.i(LOG_TAG,urlBuilder.toString());


                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(builtUri.toString());

                Log.i(LOG_TAG, "builtUri=" + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
//                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
//                urlConnection.setRequestProperty("Content-Type", "text/x-json");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    builder.append(line + "\n");
                }

                if (builder.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                forecastJsonStr = builder.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            Log.i(LOG_TAG, "forecast jsonStr=" + forecastJsonStr);
            try {
                WeatherDataHelper.mContext = getActivity();
                return WeatherDataHelper.getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "json exception:" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            //my code here
//            weekForecast = Arrays.asList(result);
//            mForecastAapter = new ArrayAdapter<>(getActivity(),
//                    R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);
//
//            mForecastAapter.notifyDataSetChanged();
//            listview_forecast.setAdapter(mForecastAapter);


            //tutorial code  --- good method
            if (result != null) {
                mForecastAapter.clear();
//                for (String s : result){
//                    mForecastAapter.add(s);
//                }

                //if the android target level is honeycomb(android 3.1) or above,
                //the adapter can addAll, like this line code
                mForecastAapter.addAll(result);
            }

            //new data is back from the server, Hooray~


        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forecast_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
//                Toast.makeText(getActivity(),"refresh",Toast.LENGTH_LONG).show();
                updateWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void updateWeather() {
        if (Service.hasNetwork(getActivity())) {
            FetchWeatherTask task = new FetchWeatherTask();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            String location = pref.getString(getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default_value));
            task.execute(location);
        }else {
            Toast.makeText(getActivity(),Const.NO_NETWORK,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }


}
