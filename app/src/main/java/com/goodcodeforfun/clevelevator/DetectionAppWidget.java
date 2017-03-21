package com.goodcodeforfun.clevelevator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import static com.goodcodeforfun.clevelevator.DetectionAppWidgetStateReceiver.SET_IS_DETECTION_OFF_ACTION;
import static com.goodcodeforfun.clevelevator.DetectionAppWidgetStateReceiver.SET_IS_DETECTION_ON_ACTION;

/**
 * Implementation of App Widget functionality.
 */
public class DetectionAppWidget extends AppWidgetProvider {

    private static final int DETECTION_STATE_REQUEST_CODE = 707;

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, DetectionAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, DetectionAppWidget.class));

        widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        int iconDetectionOn = R.drawable.ic_check_box_24dp;
        int iconDetectionOff = R.drawable.ic_check_box_out_24dp;

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.detection_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        Intent detectionIntent;
        if (SharedPreferencesUtils.getInstance(context.getApplicationContext()).isDetectionOn()) {
            views.setImageViewResource(R.id.detectionImageButton, iconDetectionOn);
            detectionIntent = new Intent(SET_IS_DETECTION_OFF_ACTION);
        } else {
            views.setImageViewResource(R.id.detectionImageButton, iconDetectionOff);
            detectionIntent = new Intent(SET_IS_DETECTION_ON_ACTION);
        }

        PendingIntent detectionPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), DETECTION_STATE_REQUEST_CODE, detectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.detectionImageButton, detectionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

