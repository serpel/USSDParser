package com.intelisys.ussdparser.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.intelisys.ussdparser.MainActivity;
import com.intelisys.ussdparser.Util.WebService;


public class USSDService extends Service {

    private static final String TAG = "USSDService";
    public static final String GET_PHONE_NUMBER = "com.intelisys.ussdparser.GET_PHONE_NUMBER";
    public static final String SET_RESPONSE = "com.intelisys.ussdparser.SET_RESPONSE";

    private final IBinder mBinder = new MyBinder();
    public static boolean isRunning;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(SET_RESPONSE)){
                Log.i(TAG, "SET USSD response in webservice.");

                String phoneNumber = intent.getStringExtra(MainActivity.USSD_PHONE_NUMBER);
                String ussdResponse = intent.getStringExtra(MainActivity.USSD_MESSAGE);
                String result = WebService.setUssdResponse("GuardaRespuesta", phoneNumber, ussdResponse);

                if(result.equals("1")){
                    Log.i(TAG, "Saved USSD response successful, number:"+phoneNumber+"text: "+ussdResponse);
                }
            }else if(intent.getAction().equals(GET_PHONE_NUMBER)){

                Log.i(TAG, "GET PhoneNumber from webservice.");

                WebService.URL = intent.getStringExtra(MainActivity.WEBSERVICE);
                String phoneNumber = WebService.getNextNumber("ObtenerSiguiente");
                Intent intent1 = new Intent(MainActivity.USSD_MAKE_CALL);
                intent1.putExtra(MainActivity.USSD_PHONE_NUMBER, phoneNumber);
                sendBroadcast(intent1);
            }
        }
    };

    public class MyBinder extends Binder {
        public USSDService getService() {
            return USSDService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        isRunning = true;

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service onBind");
        IntentFilter filter = new IntentFilter();
        filter.addAction(GET_PHONE_NUMBER);
        filter.addAction(SET_RESPONSE);
        //filter.addDataScheme(getBaseContext().getString(R.string.uri_scheme));
        //filter.addDataAuthority(getBaseContext().getString(R.string.uri_authority), null);
        //filter.addDataPath(getBaseContext().getString(R.string.uri_path), PatternMatcher.PATTERN_LITERAL);
        registerReceiver(broadcastReceiver, filter);

        return mBinder;
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "Service onDestroy");
        unregisterReceiver(broadcastReceiver);
        isRunning = false;

        super.onDestroy();
    }
}
