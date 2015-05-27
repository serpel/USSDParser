package com.intelisys.ussdparser.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by serpe_000 on 26/05/2015.
 */
public class SystemDialogReceiver extends BroadcastReceiver {
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String
            SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
            String dialogType = intent.
                    getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if(dialogType != null && dialogType.
                    equals(SYSTEM_DIALOG_REASON_RECENT_APPS)){
                Intent closeDialog =
                        new Intent(Intent.
                                ACTION_CLOSE_SYSTEM_DIALOGS);
                context.sendBroadcast(closeDialog);

            }
        }
    }
}
