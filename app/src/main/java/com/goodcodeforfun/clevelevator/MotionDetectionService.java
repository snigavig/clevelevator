package com.goodcodeforfun.clevelevator;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import com.squareup.seismic.ShakeAndElevationDetector;

public class MotionDetectionService extends Service implements ShakeAndElevationDetector.Listener {
    static final String SHAKE_DETECTED_BROADCAST_ACTION = "ShakeDetected";
    static final String ELEVATION_DETECTED_BROADCAST_ACTION = "ElevationDetected";
    private static final String RESTART_MOTION_DETECTION_SERVICE_ACTION = "RestartService";
    ShakeAndElevationDetector mShakeAndElevationDetector;
    public MotionDetectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mShakeAndElevationDetector = new ShakeAndElevationDetector(this);
        mShakeAndElevationDetector.start(sensorManager);
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
