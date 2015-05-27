package com.intelisys.ussdparser.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.intelisys.ussdparser.Service.USSDService;

/**
 * Created by serpe_000 on 24/05/2015.
 */
public class MyStartServiceReceiver extends BroadcastReceiver {
    private String TAG = MyStartServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Launching USSD Service");

        Intent srvIntent = new Intent(context, USSDService.class);
        context.startService(srvIntent);
        Toast.makeText(context, "Launching USSD Service", Toast.LENGTH_LONG).show();
    }
}