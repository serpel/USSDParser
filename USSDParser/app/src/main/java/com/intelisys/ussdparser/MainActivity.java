package com.intelisys.ussdparser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import Util.USSD;
import Util.WebService;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivityUSSD";
    public final static String EXTRA_MESSAGE = "com.intelisys.USSDParser.MESSAGE";

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickCall(View view){
        TextView phoneNumberView = (TextView) findViewById(R.id.ussdCode);
        String phoneNumber = phoneNumberView.getText().toString();

        if(!phoneNumber.isEmpty()) {
            call(phoneNumber);
        }
    }

    public void onClickStopService(View view){

    }

    public void onClickStartService(View view){

        //String result = WebService.invokeGetNextWS("ObtenerSiguiente");
        String result = WebService.getNext();
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    private void call(String ussdCode) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL, USSD.ussdToCallableUri(ussdCode));
            startActivityForResult(callIntent, 1);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        USSD ussd = new USSD(4000, 4000);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, ussd.getMsg());
        startActivity(intent);

        //Toast.makeText(this, ussd.getMsg(), Toast.LENGTH_LONG).show();
        //textView.append("\n test: " + ussd.getMsg());
    }
}
