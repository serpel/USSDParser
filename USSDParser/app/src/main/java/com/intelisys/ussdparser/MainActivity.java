package com.intelisys.ussdparser;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import Service.USSDService;
import Util.Action;
import Util.USSDParser;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivityUSSD";
    public final static String USSD_MESSAGE = "com.intelisys.ussdparser.USSD_MESSAGE";
    public final static String USSD_PHONE_NUMBER = "com.intelisys.ussdparser.USSD_PHONE_NUMBER";
    public final static String USSD_MAKE_CALL = "com.intelisys.ussdparser.USSD_MAKE_CALL";

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Make a call");

            if(intent.getAction().equals(USSD_MAKE_CALL)){
                String phoneNumber = intent.getStringExtra(USSD_PHONE_NUMBER);
                if(!phoneNumber.isEmpty()) {
                    call(phoneNumber, Action.PUT_MESSAGE_IN_WEBSERVICE);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(USSDService.isRunning) {
            Button startServiceButton = (Button) findViewById(R.id.start_service);
            startServiceButton.setEnabled(false);
        }else{
            Button stopServiceButton = (Button) findViewById(R.id.stop_Service);
            stopServiceButton.setEnabled(false);
        }
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

    public void onClickCall(View view){
        TextView phoneNumberView = (TextView) findViewById(R.id.ussdCode);
        String phoneNumber = phoneNumberView.getText().toString();

        if(!phoneNumber.isEmpty()) {
            call(phoneNumber, Action.PUT_MESSAGE_IN_ACTIVITY);
        }
    }

    public void onClickStopService(View view){
        if(USSDService.isRunning){
            Intent intent = new Intent(this, USSDService.class);
            stopService(intent);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intentService = new Intent(USSDService.GET_PHONE_NUMBER);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intentService, 0);
            alarmManager.cancel(sender);
        }
    }

    public void onClickStartService(View view){
        if(!USSDService.isRunning){
            Log.i(TAG, "onClickStartService() Run service and active alarm");
            Intent intent = new Intent(this, USSDService.class);
            startService(intent);

            long firstTime = (SystemClock.elapsedRealtime() + 3) * 1000;

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intentService = new Intent(USSDService.GET_PHONE_NUMBER);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intentService, 0);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                    TimeUnit.MINUTES.toMillis(1), sender);
        }
    }

    private void call(String ussdCode, int resultCode) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL, USSDParser.ussdToCallableUri(ussdCode));
            callIntent.putExtra(USSD_PHONE_NUMBER, ussdCode);
            startActivityForResult(callIntent, resultCode);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        USSDParser ussdParser = new USSDParser(4000, 4000);
        String msg = ussdParser.getMsg();

        switch (resultCode){
            case 1:
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(USSD_MESSAGE, msg);
                startActivity(intent);
                break;
            case 2:
                Intent intent1 = new Intent(USSDService.SET_RESPONSE);
                intent1.putExtra(USSD_PHONE_NUMBER, data.getStringExtra(USSD_PHONE_NUMBER));
                intent1.putExtra(USSD_MESSAGE, msg);
                sendBroadcast(intent1);
                break;
            default:
        }
        //Toast.makeText(this, ussd.getMsg(), Toast.LENGTH_LONG).show();
        //textView.append("\n test: " + ussd.getMsg());
    }
}
