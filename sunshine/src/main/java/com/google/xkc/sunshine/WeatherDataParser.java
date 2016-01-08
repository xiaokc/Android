package com.google.xkc.sunshine;

import android.text.format.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xkc on 1/8/16.
 */
public class WeatherDataParser {
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException{
        JSONObject weather = new JSONObject(weatherJsonStr);
        JSONArray list = weather.getJSONArray("list");
        JSONObject dayIndexJson = list.getJSONObject(dayIndex);
        JSONObject temp = dayIndexJson.getJSONObject("temp");
        double tempForDay = temp.getDouble("max");
        return tempForDay;
    }


}
