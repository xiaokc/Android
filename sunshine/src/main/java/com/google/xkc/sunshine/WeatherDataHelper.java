package com.google.xkc.sunshine;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xkc on 1/8/16.
 */
public class WeatherDataHelper {
    /**
     * The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    public static String getReadableDateStr(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat format = new SimpleDateFormat("EEE, MM月dd日", Locale.CHINA);
        return format.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    public static String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundHigh = Math.round(high);
        long roundLow = Math.round(low);

        String highLowStr = roundHigh + "/" + roundLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public static String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_WEATHER = "weather";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";
        final String OWM_DT = "dt";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String[] resultStrs = new String[numDays];

        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".

            long time = dayForecast.getLong(OWM_DT);
            Log.i("xkc","time="+time);
            day = getReadableDateStr(time * 1000);
            // Cheating to convert this to UTC time, which is what we want anyhow


            JSONObject weather = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weather.getString(OWM_DESCRIPTION);


            JSONObject temperature = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperature.getDouble(OWM_MAX);
            double low = temperature.getDouble(OWM_MIN);
            highAndLow = formatHighLows(high, low);

            resultStrs[i] = day + "-" + description + "-" + highAndLow;


        }

        return resultStrs;

    }


}
