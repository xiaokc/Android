package com.google.xkc.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        switch (id){
            case R.id.action_refresh:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_map:
                openPreferredLocationInMap();
                break;
        }

        return true;
    }

    //open map
    private void openPreferredLocationInMap(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String location = pref.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default_value));

        Log.i(LOG_TAG,"location="+location);

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",location)
                .build();
        Log.i(LOG_TAG,"geoLocation="+geoLocation);

        // use common Intents to show a location on a map
        // https://developer.android.com/guide/components/intents-common.html#Now
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else {
            Log.e(LOG_TAG,"Activity can not be resolved, the location is "+ location);
        }

    }

}
