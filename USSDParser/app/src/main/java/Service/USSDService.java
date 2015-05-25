package Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.intelisys.ussdparser.MainActivity;

import Util.WebService;

public class USSDService extends Service {

    private static final String TAG = "USSDService";
    public static final String GET_PHONE_NUMBER = "com.intelisys.ussdparser.GET_PHONE_NUMBER";
    public static final String SET_RESPONSE = "com.intelisys.ussdparser.SET_RESPONSE";
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
                    Log.i(TAG, "Saved USSD response successful");
                }
            }else if(intent.getAction().equals(GET_PHONE_NUMBER)){

                Log.i(TAG, "GET PhoneNumber from webservice.");

                String phoneNumber = WebService.getNextNumber("ObtenerSiguiente");
                Intent intent1 = new Intent(MainActivity.USSD_MAKE_CALL);
                intent1.putExtra(MainActivity.USSD_PHONE_NUMBER, phoneNumber);
                sendBroadcast(intent1);
            }
        }
    };

    @Override
    public void onCreate() {

        Log.i(TAG, "Service onCreate");
        isRunning = true;

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "Service onDestroy");
        isRunning = false;
        super.onDestroy();
    }
}
