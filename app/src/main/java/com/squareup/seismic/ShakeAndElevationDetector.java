// Copyright 2010 Square, Inc.
package com.squareup.seismic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Detects phone shaking and continuous single axis movement. If more than 75% of the samples
 * taken in the past 1.0s are accelerating, the device is a) shaking, or b) free falling 1.84m (h =
 * 1/2*g*t^2*3/4). Same technique is used to detect movement in one direction(e.g. elevator up/down)
 *
 * @author Bob Lee (bob@squareup.com)
 * @author Eric Burke (eric@squareup.com)
 *         <p>
 *         Added continuous single axis movement detection
 * @contributor Dmitry Mina (dmitry.mina@gmail.com)
 */
public class ShakeAndElevationDetector implements SensorEventListener {

    private static final int SENSITIVITY_ELEVATOR = 5; //needs tweaking
    private static final int SENSITIVITY_SHAKE = 18;
    private static final int ACCELERATING_X_POSITIVE = 0;
    private static final int ACCELERATING_X_NEGATIVE = 1;
    private static final int ACCELERATING_Y_NEGATIVE = 2;
    private static final int ACCELERATING_Y_POSITIVE = 3;
    private static final int ACCELERATING_Z_NEGATIVE = 4;
    private static final int ACCELERATING_Z_POSITIVE = 5;
    private static final int ACCELERATING_DEFAULT = -1;
    private static final int DEFAULT_ACCELERATION_THRESHOLD = SENSITIVITY_SHAKE;
    private static final int DEFAULT_ACCELERATION_ELEVATION_THRESHOLD = SENSITIVITY_ELEVATOR;
    private final SampleQueue queue = new SampleQueue();
    private final Listener listener;
    /**
     * When the magnitude of total acceleration exceeds this
     * value, the phone is accelerating.
     */
    private final int accelerationThreshold = DEFAULT_ACCELERATION_THRESHOLD;
    private final int accelerationElevationThreshold = DEFAULT_ACCELERATION_ELEVATION_THRESHOLD;
    private final double accelerationMinimalNegativeThreshold = -0.2d;
    private final double accelerationMinimalPositiveThreshold = Math.abs(accelerationMinimalNegativeThreshold);

    private SensorManager sensorManager;
    private Sensor accelerometer;

    public ShakeAndElevationDetector(Listener listener) {
        this.listener = listener;
    }

    /**
     * Starts listening for shakes on devices with appropriate hardware.
     *
     * @return true if the device supports shake detection.
     */
    public boolean start(SensorManager sensorManager) {
        // Already started?
        if (accelerometer != null) {
            return true;
        }

        accelerometer = sensorManager.getDefaultSensor(
                Sensor.TYPE_LINEAR_ACCELERATION);

        // If this phone has an accelerometer, listen to it.
        if (accelerometer != null) {
            this.sensorManager = sensorManager;
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
        return accelerometer != null;
    }

    /**
     * Stops listening.  Safe to call when already stopped.  Ignored on devices
     * without appropriate hardware.
     */
    public void stop() {
        if (accelerometer != null) {
            queue.clear();
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager = null;
            accelerometer = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] < accelerationMinimalNegativeThreshold || event.values[0] > accelerationMinimalPositiveThreshold ||
                event.values[1] < accelerationMinimalNegativeThreshold || event.values[1] > accelerationMinimalPositiveThreshold ||
                event.values[2] < accelerationMinimalNegativeThreshold || event.values[2] > accelerationMinimalPositiveThreshold) {
            boolean accelerating = isAccelerating(event);
            int highestAccelerationAxis = getHighestAccelerationAxis(event);
            long timestamp = event.timestamp;
            queue.add(timestamp, accelerating, highestAccelerationAxis);
            if (queue.isShaking()) {
                queue.clear();
                listener.hearShake();
            }
            if (queue.isElevating()) {
                queue.clear();
                listener.hearElevation();
            }
        }
    }

    /**
     * Returns true if the device is currently accelerating.
     */
    private boolean isAccelerating(SensorEvent event) {
        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];

        // Instead of comparing magnitude to ACCELERATION_THRESHOLD,
        // compare their squares. This is equivalent and doesn't need the
        // actual magnitude, which would be computed using (expensive) Math.sqrt().
        final double magnitudeSquared = ax * ax + ay * ay + az * az;
        return magnitudeSquared > accelerationThreshold * accelerationThreshold;
    }

    /**
     * Returns true if the device is currently accelerating.
     */
    private
    @AccelerationDirection
    int getHighestAccelerationAxis(SensorEvent event) {
        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];

        final double magnitudeSquaredX = ax * ax;
        final double magnitudeSquaredY = ay * ay;
        final double magnitudeSquaredZ = az * az;
        final int threshold = accelerationElevationThreshold * accelerationElevationThreshold;
        double highestMagnitude = 0;
        int highestAccelerationAxisDirection = ACCELERATING_DEFAULT;

        if (magnitudeSquaredX > threshold && magnitudeSquaredX > highestMagnitude) {
            highestMagnitude = magnitudeSquaredX;
            if (ax > 0) {
                highestAccelerationAxisDirection = ACCELERATING_X_POSITIVE;
            } else {
                highestAccelerationAxisDirection = ACCELERATING_X_NEGATIVE;
            }
        }
        if (magnitudeSquaredY > highestMagnitude && magnitudeSquaredY > threshold) {
            highestMagnitude = magnitudeSquaredY;
            if (ay > 0) {
                highestAccelerationAxisDirection = ACCELERATING_Y_POSITIVE;
            } else {
                highestAccelerationAxisDirection = ACCELERATING_Y_NEGATIVE;
            }
        }
        if (magnitudeSquaredZ > highestMagnitude && magnitudeSquaredZ > threshold) {
            if (az > 0) {
                highestAccelerationAxisDirection = ACCELERATING_Z_POSITIVE;
            } else {
                highestAccelerationAxisDirection = ACCELERATING_Z_NEGATIVE;
            }
        }

        return highestAccelerationAxisDirection;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not relevant
    }

    @IntDef({ACCELERATING_X_POSITIVE, ACCELERATING_X_NEGATIVE, ACCELERATING_Y_NEGATIVE, ACCELERATING_Y_POSITIVE, ACCELERATING_Z_NEGATIVE, ACCELERATING_Z_POSITIVE, ACCELERATING_DEFAULT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface AccelerationDirection {
    }

    /**
     * Listens for shakes.
     */
    public interface Listener {
        /**
         * Called on the main thread when the device is shaken.
         */
        void hearShake();

        /**
         * Called on the main thread when the device is continuously elevating.
         */
        void hearElevation();
    }

    /**
     * Queue of samples. Keeps a running average.
     */
    private static class SampleQueue {

        /**
         * Window size in ns. Used to compute the average.
         */
        private static final long MAX_WINDOW_SIZE = 800000000; // 0.8s
        private static final long MIN_WINDOW_SIZE = 300000000; // 0.3s

        /**
         * Ensure the queue size never falls below this size, even if the device
         * fails to deliver this many events during the time window. The LG Ally
         * is one such device.
         */
        private static final int MIN_QUEUE_SIZE = 4;

        private final SamplePool pool = new SamplePool();

        private Sample oldest;
        private Sample newest;
        private int sampleCount;
        private int acceleratingCount;

        /**
         * Adds a sample.
         *
         * @param timestamp    in nanoseconds of sample
         * @param accelerating true if > {@link #accelerationThreshold}.
         */
        void add(long timestamp, boolean accelerating, @AccelerationDirection int accelerationDirection) {
            // Purge samples that proceed window.
            purge(timestamp - MAX_WINDOW_SIZE);

            // Add the sample to the queue.
            Sample added = pool.acquire();
            added.timestamp = timestamp;
            added.accelerating = accelerating;
            added.accelerationDirection = accelerationDirection;
            added.next = null;
            if (newest != null) {
                newest.next = added;
            }
            newest = added;
            if (oldest == null) {
                oldest = added;
            }

            // Update running average.
            sampleCount++;
            if (accelerating) {
                acceleratingCount++;
            }
        }

        /**
         * Removes all samples from this queue.
         */
        void clear() {
            while (oldest != null) {
                Sample removed = oldest;
                oldest = removed.next;
                pool.release(removed);
            }
            newest = null;
            sampleCount = 0;
            acceleratingCount = 0;
        }

        /**
         * Purges samples with timestamps older than cutoff.
         */
        void purge(long cutoff) {
            while (sampleCount >= MIN_QUEUE_SIZE
                    && oldest != null && cutoff - oldest.timestamp > 0) {
                // Remove sample.
                Sample removed = oldest;
                if (removed.accelerating) {
                    acceleratingCount--;
                }
                sampleCount--;

                oldest = removed.next;
                if (oldest == null) {
                    newest = null;
                }
                pool.release(removed);
            }
        }

        /**
         * Copies the samples into a list, with the oldest entry at index 0.
         */
        List<Sample> asList() {
            List<Sample> list = new ArrayList<>();
            Sample s = oldest;
            while (s != null) {
                list.add(s);
                s = s.next;
            }
            return list;
        }

        /**
         * Returns true if we have enough samples and more than 3/4 of those samples
         * are accelerating.
         */
        boolean isShaking() {
            return newest != null
                    && oldest != null
                    && newest.timestamp - oldest.timestamp >= MIN_WINDOW_SIZE
                    && acceleratingCount >= (sampleCount >> 1) + (sampleCount >> 2);
        }

        /**
         * Returns true if we have enough samples and more than 3/4 of those samples
         * are accelerating towards the single axis in the same direction.
         */
        boolean isElevating() {
            List<Sample> currentQueue = asList();
            int acceleratingSingleAxisCount = 0;
            int[] acceleratingCountArray = {0, 0, 0, 0, 0, 0};
            for (Sample sample : currentQueue) {
                if (sample.accelerationDirection != ACCELERATING_DEFAULT) {
                    acceleratingCountArray[sample.accelerationDirection]++;
                }
            }

            for (int anAcceleratingCountArray : acceleratingCountArray) {
                if (anAcceleratingCountArray > acceleratingSingleAxisCount) {
                    acceleratingSingleAxisCount = anAcceleratingCountArray;
                }
            }

            return newest != null
                    && oldest != null
                    && newest.timestamp - oldest.timestamp >= MIN_WINDOW_SIZE
                    && acceleratingSingleAxisCount >= sampleCount * 0.85f;
        }
    }

    /**
     * An accelerometer sample.
     */
    private static class Sample {
        /**
         * Time sample was taken.
         */
        long timestamp;

        /**
         * If acceleration > {@link #accelerationThreshold}.
         */
        boolean accelerating;

        /**
         * Acceleration highest value axis
         */
        @AccelerationDirection
        int accelerationDirection;

        /**
         * Next sample in the queue or pool.
         */
        Sample next;
    }

    /**
     * Pools samples. Avoids garbage collection.
     */
    private static class SamplePool {
        private Sample head;

        /**
         * Acquires a sample from the pool.
         */
        Sample acquire() {
            Sample acquired = head;
            if (acquired == null) {
                acquired = new Sample();
            } else {
                // Remove instance from pool.
                head = acquired.next;
            }
            return acquired;
        }

        /**
         * Returns a sample to the pool.
         */
        void release(Sample sample) {
            sample.next = head;
            head = sample;
        }
    }
}
