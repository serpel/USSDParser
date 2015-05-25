package Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import Service.USSDService;

/**
 * Created by serpe_000 on 24/05/2015.
 */
public class BootComplete extends BroadcastReceiver {
    private String TAG = BootComplete.class.getSimpleName();
    public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    //public static final String ALARM_SERVICE = "com.intelisys.ALARM_SERVICE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Launching service");

        if( intent.getAction().equals(BOOT_COMPLETED)) {
            Intent srvIntent = new Intent(context, USSDService.class);
            context.startService(srvIntent);

            //start 3 seconds after first register.
            long firstTime = (SystemClock.elapsedRealtime() + 3) * 1000;

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            Intent intentService = new Intent(USSDService.GET_PHONE_NUMBER);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intentService, 0);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                    TimeUnit.MINUTES.toMillis(1), sender);
        }
    }
}