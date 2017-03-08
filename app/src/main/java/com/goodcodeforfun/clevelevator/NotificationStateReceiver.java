package com.goodcodeforfun.clevelevator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationStateReceiver extends BroadcastReceiver {

    public static final String SET_NOTIFICATION_IS_SHOWING_ACTION = "IsShowingNotification";
    public static final String SET_NOTIFICATION_IS_NOT_SHOWING_ACTION = "IsNotShowingNotification";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SET_NOTIFICATION_IS_SHOWING_ACTION.equals(intent.getAction())) {
            SharedPreferencesUtils.getInstance(context).setIsShowingTask(true);
        } else {
            SharedPreferencesUtils.getInstance(context).setIsShowingTask(false);
        }
    }
}
