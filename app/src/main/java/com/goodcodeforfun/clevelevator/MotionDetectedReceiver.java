package com.goodcodeforfun.clevelevator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.goodcodeforfun.clevelevator.MotionDetectionService.ELEVATION_DETECTED_BROADCAST_ACTION;
import static com.goodcodeforfun.clevelevator.MotionDetectionService.SHAKE_DETECTED_BROADCAST_ACTION;

/**
 * Created by snigavig on 03.03.17.
 */

public class MotionDetectedReceiver extends BroadcastReceiver {

    private static final String TAG = "MotionDetectedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        if (!SharedPreferencesUtils.getInstance(context.getApplicationContext()).isShowingTask()) {
            if (SHAKE_DETECTED_BROADCAST_ACTION.equals(intent.getAction()) || ELEVATION_DETECTED_BROADCAST_ACTION.equals(intent.getAction())) {
                NotificationService.startActionShowEquation(context.getApplicationContext());
            }
        }
    }
}