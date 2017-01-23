package com.buzzer.strl.buzzercontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhoneBoot extends BroadcastReceiver {
    public PhoneBoot() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, BuzzerServer.class);
        context.startService(myIntent);
    }
}
