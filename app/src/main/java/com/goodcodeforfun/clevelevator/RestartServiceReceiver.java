package com.goodcodeforfun.clevelevator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by snigavig on 03.03.17.
 */

public class RestartServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SharedPreferencesUtils.getInstance(context.getApplicationContext()).isDetectionOn()) {
            MotionDetectionService.startMotionDetection(context.getApplicationContext());
        }
    }
}