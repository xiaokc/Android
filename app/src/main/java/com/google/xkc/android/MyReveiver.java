package com.google.xkc.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

import io.petchat.senz.model.SenzEvent;
import io.petchat.senz.model.SenzMotion;
import io.petchat.senz.model.SenzOHStatus;

/**
 * Created by xkc on 1/18/16.
 */
public class MyReveiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase("senz.intent.action.MOTION_CHANGED")) {
            Bundle bundle = intent.getExtras();
            Serializable data = bundle.getSerializable("motion_changed");
            SenzMotion motion = (SenzMotion) data;

            Log.i("xkc","motion type="+motion.motionType);
        }
        if (action.equalsIgnoreCase("senz.intent.action.HOME_OFFICE_STATUS")){
            Bundle bundle = intent.getExtras();
            Serializable data = bundle.getSerializable("home_office_status");
            SenzOHStatus status = (SenzOHStatus) data;

            Log.i("xkc","status type="+status.stautsType);

        }
        if (action.equalsIgnoreCase("senz.intent.action.EVENT")){
            Bundle bundle = intent.getExtras();
            Serializable data = bundle.getSerializable("event");
            SenzEvent event = (SenzEvent) data;

            Log.i("xkc","event type="+event.eventType);
        }
    }
}
