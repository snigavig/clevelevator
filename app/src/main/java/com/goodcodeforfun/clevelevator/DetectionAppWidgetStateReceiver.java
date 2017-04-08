package com.goodcodeforfun.clevelevator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DetectionAppWidgetStateReceiver extends BroadcastReceiver {

    public static final String SET_IS_DETECTION_ON_ACTION = "IsDetectionOn";
    public static final String SET_IS_DETECTION_OFF_ACTION = "IsDetectionOff";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SET_IS_DETECTION_ON_ACTION.equals(intent.getAction())) {
            SharedPreferencesUtils.getInstance(context).setIsDetectionOn(true);
            MotionDetectionService.startMotionDetection(context);
        } else {
            SharedPreferencesUtils.getInstance(context).setIsDetectionOn(false);
            MotionDetectionService.stopMotionDetection(context);
        }
        DetectionAppWidget.updateWidget(context.getApplicationContext());
    }
}
