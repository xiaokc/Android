package com.google.xkc.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    protected static Context mContext;
    private static final String LOG_TAG = "WeatherDataHelper";


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
    public static String formatHighLows(double high, double low, String unitType) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundHigh = Math.round(high);
        long roundLow = Math.round(low);

        if (unitType.equals(mContext.getString(R.string.pref_temperature_units_imperial))) {
            roundHigh = (long) ((roundHigh * 1.8) + 32);
            roundLow = (long) ((roundLow * 1.8) + 32);
        } else if (!unitType.equals(mContext.getString(R.string.pref_temperature_units_metric))) {
            Log.d(LOG_TAG, "Unit type not found: " + unitType);
        }

        String highLowStr = roundHigh + "/" + roundLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forec  ast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public static String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        final String LOG_TAG = "getWeatherDataFromJson()";

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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String unitType = pref.getString(mContext.getString(R.string.pref_temperature_units_key),
                mContext.getString(R.string.pref_temperature_units_metric));

        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".

            // Cheating to convert this to UTC time, which is what we want anyhow
            long time = dayForecast.getLong(OWM_DT);
//            Log.i(LOG_TAG,"time="+time);
            day = getReadableDateStr(time * 1000);

            JSONObject weather = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weather.getString(OWM_DESCRIPTION);


            JSONObject temperature = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperature.getDouble(OWM_MAX);
            double low = temperature.getDouble(OWM_MIN);
            highAndLow = formatHighLows(high, low, unitType);

            resultStrs[i] = day + "-" + description + "-" + highAndLow;


        }

        return resultStrs;

    }


    /**
     * parse the geo Location : longitude and latitude
     * @param forecastJsonStr
     * @return
     */
    public double[] getGeoLocation(String forecastJsonStr) throws JSONException {
        double[] geoLocation = new double[2];
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONObject coord = forecastJson.getJSONObject("coord");
        double lon = coord.getDouble("lon");
        double lat = coord.getDouble("lat");

        geoLocation[0] = lon;
        geoLocation[1] = lat;

        return geoLocation;


    }


}
