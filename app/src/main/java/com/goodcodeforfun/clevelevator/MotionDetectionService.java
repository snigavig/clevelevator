package com.goodcodeforfun.clevelevator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.squareup.seismic.ShakeAndElevationDetector;

public class MotionDetectionService extends Service implements ShakeAndElevationDetector.Listener {
    static final String SHAKE_DETECTED_BROADCAST_ACTION = "ShakeDetected";
    static final String ELEVATION_DETECTED_BROADCAST_ACTION = "ElevationDetected";
    private static final String START_MOTION_DETECTION_SERVICE_ACTION = "StartService";
    private static final String RESTART_MOTION_DETECTION_SERVICE_ACTION = "RestartService";
    private static final String STOP_MOTION_DETECTION_SERVICE_ACTION = "StopService";
    private ShakeAndElevationDetector mShakeAndElevationDetector;

    public MotionDetectionService() {
    }

    public static void startMotionDetection(Context context) {
        Intent intent = new Intent(context, MotionDetectionService.class);
        intent.setAction(START_MOTION_DETECTION_SERVICE_ACTION);
        context.startService(intent);
    }

    public static void stopMotionDetection(Context context) {
        Intent intent = new Intent(context, MotionDetectionService.class);
        intent.setAction(STOP_MOTION_DETECTION_SERVICE_ACTION);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Log.e("booo", "starting motion detection service");
        if (mShakeAndElevationDetector == null) {
            Log.e("booo", "detector is null, creating");
            mShakeAndElevationDetector = new ShakeAndElevationDetector(this);
        }

        if (null == intent || null == intent.getAction() || START_MOTION_DETECTION_SERVICE_ACTION.equals(intent.getAction())) {
            Log.e("booo", "i should start");
            mShakeAndElevationDetector.start(sensorManager);
        } else {
            Log.e("booo", "i should stop");
            mShakeAndElevationDetector.stop();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mShakeAndElevationDetector.stop();
        sendBroadcast(new Intent(RESTART_MOTION_DETECTION_SERVICE_ACTION));
    }

    public void hearShake() {
        sendBroadcast(new Intent(SHAKE_DETECTED_BROADCAST_ACTION));
    }

    @Override
    public void hearElevation() {
        sendBroadcast(new Intent(ELEVATION_DETECTED_BROADCAST_ACTION));
    }
}
