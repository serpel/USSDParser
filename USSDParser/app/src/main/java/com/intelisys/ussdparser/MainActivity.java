package com.intelisys.ussdparser;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.intelisys.ussdparser.Receiver.MyStartServiceReceiver;
import com.intelisys.ussdparser.Service.USSDService;
import com.intelisys.ussdparser.Util.Action;
import com.intelisys.ussdparser.Util.USSDParser;

import java.util.concurrent.TimeUnit;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivityUSSD";
    public final static String USSD_MESSAGE = "com.intelisys.ussdparser.USSD_MESSAGE";
    public final static String USSD_PHONE_NUMBER = "com.intelisys.ussdparser.USSD_PHONE_NUMBER";
    public final static String USSD_MAKE_CALL = "com.intelisys.ussdparser.USSD_MAKE_CALL";
    public final static String USSD_REGISTER_CALL = "com.intelisys.ussdparser.USSD_REGISTER_CALL";
    public final static String REQUEST_CODE = "com.intelisys.ussdparser.REQUEST_CODE";
    public final static String WEBSERVICE = "com.intelisys.ussdparser.webservice";


    private Button startServiceButton;
    private Button stopServiceButton;
    private EditText webserviceEditText;
    private EditText intervalEditText;
    private USSDService service;
    private IntentFilter filter;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Make a call");

            if(intent.getAction().equals(USSD_MAKE_CALL)){
                String phoneNumber = intent.getStringExtra(USSD_PHONE_NUMBER);
                if(phoneNumber != null) {
                    call(phoneNumber, Action.PUT_MESSAGE_IN_WEBSERVICE);
                }else
                {
                    Log.d(TAG, "Phone Number is Null");
                }
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            USSDService.MyBinder b = (USSDService.MyBinder) binder;
            service = b.getService();
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
                    .show();
        }

        public void onServiceDisconnected(ComponentName className) {
            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filter = new IntentFilter();
        filter.addAction(USSD_MAKE_CALL);
        filter.addAction(USSD_REGISTER_CALL);

        startServiceButton = (Button) findViewById(R.id.start_service);
        stopServiceButton = (Button) findViewById(R.id.stop_Service);
        intervalEditText = (EditText) findViewById(R.id.editTextExecuteInterval);
        intervalEditText.setText(R.string.webservice_interval);

        webserviceEditText = (EditText) findViewById(R.id.editTextLink);
        webserviceEditText.setText(R.string.webservice_link);

        if(USSDService.isRunning) {
            startServiceButton.setEnabled(false);
            stopServiceButton.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {

        Intent intent= new Intent(this, USSDService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        registerReceiver(broadcastReceiver, filter);

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy receiver");
        unregisterReceiver(broadcastReceiver);
        unbindService(mConnection);
        super.onDestroy();
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

            //if(!USSDService.isRunning){
                stopServiceButton.setEnabled(true);
                startServiceButton.setEnabled(true);
            //}
        }
    }

    public void onClickStartService(View view){

        Log.i(TAG, "onClickStartService() Run com.intelisys.ussdparser.service and active alarm");

        if(!USSDService.isRunning) {
            //Intent srvIntent = new Intent(this, USSDService.class);
            //startService(srvIntent);
            Intent intent = new Intent(this, MyStartServiceReceiver.class);
            sendBroadcast(intent);
        }

        try {
            Double timeInterval = Double.parseDouble(intervalEditText.getText().toString());
            int seconds =  (int)(timeInterval * 60);

            String webserviceLink = webserviceEditText.getText().toString();
            if (seconds > 0 && !webserviceLink.isEmpty()) {

                long firstTime = (SystemClock.elapsedRealtime() + 3) * 1000;
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intentService = new Intent(USSDService.GET_PHONE_NUMBER);
                intentService.putExtra(WEBSERVICE, webserviceLink);
                PendingIntent sender = PendingIntent.getBroadcast(this, 0, intentService, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstTime,
                        TimeUnit.SECONDS.toMillis(seconds), sender);
            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }

        startServiceButton.setEnabled(false);
    }

    private void call(String ussdCode, int requestCode) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL, USSDParser.ussdToCallableUri(ussdCode));
            callIntent.putExtra(USSD_PHONE_NUMBER, ussdCode);
            //setResult(RESULT_OK, callIntent);
            startActivityForResult(callIntent, requestCode);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        USSDParser ussdParser = new USSDParser(4000, 4000);
        String msg = ussdParser.getMsg();
        String phoneNumber;
        //= data.getStringExtra(USSD_PHONE_NUMBER);

        switch (requestCode){
            case 1:
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(USSD_MESSAGE, msg);
                startActivity(intent);
                break;
            case 2:
                //if(resultCode == RESULT_OK) {
                    Intent intent1 = new Intent(USSDService.SET_RESPONSE);
                    intent1.putExtra(USSD_PHONE_NUMBER, "#123#");
                    intent1.putExtra(USSD_MESSAGE, msg);
                    sendBroadcast(intent1);
               // }
                break;
            default:
        }

        //Toast.makeText(this, ussd.getMsg(), Toast.LENGTH_LONG).show();
        //textView.append("\n test: " + ussd.getMsg());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(!hasFocus){
            Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(intent);
        }
    }
}
