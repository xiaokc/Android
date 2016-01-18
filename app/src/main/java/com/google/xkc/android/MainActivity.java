package com.google.xkc.android;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.petchat.senz.Senz;
import io.petchat.senz.interfaces.SenzCallback;
import io.petchat.senz.sdk.UserInfo;


public class MainActivity extends AppCompatActivity {
    private final String APPID = "569cc98660b2715708de7c4e";
    private TextView userInfo_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Senz.initialize(this, APPID);

        userInfo_tv = (TextView) findViewById(R.id.userInfo_tv);


        final UserInfo userInfo = new UserInfo(this);
        userInfo.getUserInfo(new SenzCallback() {
            @Override
            public void done(Exception e, Object object) {
                if (e == null) {
                    HashMap<String, Object> map = (HashMap<String, Object>) object;
                    if (map.containsKey("gender")) {
                        Log.i("xkc","has gender");
//                        HashMap<String, Double> consumption =
//                                (HashMap<String, Double>) map.get("consumption");

                        double gender = (double) map.get("gender");



//                        Iterator<Map.Entry<String,Double>> iterator = consumption.entrySet().iterator();
//                        StringBuilder builder = new StringBuilder();
//                        while (iterator.hasNext()){
//                            Map.Entry<String,Double> entry = iterator.next();
//                            String key = entry.getKey();
//                            Double value = entry.getValue();
//
//                            builder.append(key+":"+value);
//
//                            Log.i("xkc","key="+key+",value="+value);
//
//                        }

                        userInfo_tv.setText(String.valueOf(gender));
                    }else {
                        Log.i("xkc","has no gender");
                    }

                } else {
                    Log.e("xkc","error: "+e.getMessage());
                }
            }
        });

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
