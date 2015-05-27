package com.intelisys.ussdparser.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by serpe_000 on 26/05/2015.
 */
public class MyScheduleReceiver extends BroadcastReceiver {
    // restart com.intelisys.ussdparser.service every 30 seconds
    private static final long REPEAT_TIME = 1000 * 30;
    private static final String TAG = MyScheduleReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Schedule Service");

        /*
        //intent.getStringExtra()
        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(USSDService.GET_PHONE_NUMBER);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        // start 30 seconds after boot completed
        cal.add(Calendar.SECOND, 30);
        // fetch every 30 seconds
        // InexactRepeating allows Android to optimize the energy consumption
        service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(), REPEAT_TIME, pending);

        */
        // com.intelisys.ussdparser.service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
        // REPEAT_TIME, pending);
    }
}
