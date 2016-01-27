package com.google.xkc.sunshine;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by xkc on 1/24/16.
 */
public class TestActivity extends Activity {
    private TextView test;
    private static final String LOG_TAG = TestActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test = (TextView) findViewById(R.id.test);

        // Get the ContentResolver which will send a message to the ContentProvider
        ContentResolver resolver = getContentResolver();

        // Get a Cursor containing all of the rows in the Words table
        Cursor cursor = resolver.query(UserDictionary.Words.CONTENT_URI,null, null, null, null);

        Log.e(LOG_TAG,UserDictionary.Words.CONTENT_URI.toString());


        cursor.close();



    }
}
