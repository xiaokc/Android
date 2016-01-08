package com.google.xkc.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);

        List<String> weekForecast = new ArrayList<>(Arrays.asList(forecastArray));

        mForecastAapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);


        listview_forecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        listview_forecast.setAdapter(mForecastAapter);

        getForecastStr();


        return rootView;
    }

    private String getForecastStr() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily" +
                    "?q=94043&mode=json&units=metric&cnt=7&APPID=b49b573ea5b0e5150417928348d05344");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream == null){
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                builder.append(line + "\n");
            }

            if (builder.length() == 0 ){
                // Stream was empty.  No point in parsing.
                return null;
            }

            forecastJsonStr = builder.toString();


        }  catch (IOException e) {
            Log.e("ForecastFragment","Error",e);
            return null;
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("MainActivityFramgment","Error closing stream",e);
                }
            }
        }

        return forecastJsonStr;

    }
}
