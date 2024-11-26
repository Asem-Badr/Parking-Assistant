
package com.luxoft.carspeeddemo;

import androidx.appcompat.app.AppCompatActivity;
import android.car.Car;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NINJA";
    private TextView[] ultrasonicTextViews = new TextView[6];
    private Car mCar;
    private CarPropertyManager mCarPropertyManager;
    private Thread[] ultrasonicMonitorThreads = new Thread[6];
    private volatile boolean isMonitoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCREATE ");
        // Initialize TextViews for ultrasonic readings
        ultrasonicTextViews[0] = findViewById(R.id.tv_ultrasonic_one);
        ultrasonicTextViews[1] = findViewById(R.id.tv_ultrasonic_two);
        ultrasonicTextViews[2] = findViewById(R.id.tv_ultrasonic_three);
        ultrasonicTextViews[3] = findViewById(R.id.tv_ultrasonic_four);
        ultrasonicTextViews[4] = findViewById(R.id.tv_ultrasonic_five);
        ultrasonicTextViews[5] = findViewById(R.id.tv_ultrasonic_six);

        try {
            // Initialize the Car API
            mCar = Car.createCar(this);
            mCarPropertyManager = (CarPropertyManager) mCar.getCarManager(Car.PROPERTY_SERVICE);
            Log.i(TAG, "Car object created successfully");

            // Start monitoring ultrasonic properties
            startUltrasonicMonitoring();

        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Car API", e);
        }
    }

    private void startUltrasonicMonitoring() {
        isMonitoring = true;
        int[] ultrasonicProperties = {
                VehiclePropertyIds.ULTRASONIC_ONE,
                VehiclePropertyIds.ULTRASONIC_TWO,
                VehiclePropertyIds.ULTRASONIC_THREE,
                VehiclePropertyIds.ULTRASONIC_FOUR,
                VehiclePropertyIds.ULTRASONIC_FIVE,
                VehiclePropertyIds.ULTRASONIC_SIX
        };

        for (int i = 0; i < ultrasonicProperties.length; i++) {
            final int index = i;
            ultrasonicMonitorThreads[i] = new Thread(() -> {
                while (isMonitoring) {
                    try {
                        // Fetch ultrasonic property value
                        CarPropertyValue<?> carPropertyValue = mCarPropertyManager.getProperty(
                                Integer.class, ultrasonicProperties[index], 0);
                        if (carPropertyValue != null) {
                            int ultrasonicReading = (Integer) carPropertyValue.getValue();
                            Log.i(TAG, "Ultrasonic " + (index + 1) + " value polled: " + ultrasonicReading);
                            runOnUiThread(() -> ultrasonicTextViews[index].setText("Ultrasonic " + (index + 1) + ": " + ultrasonicReading));
                        }
                        Thread.sleep(200); // Polling interval
                    } catch (Exception e) {
                        Log.e(TAG, "Error in Ultrasonic " + (index + 1) + " monitoring thread", e);
                    }
                }
            });
            ultrasonicMonitorThreads[i].start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the monitoring threads
        isMonitoring = false;
        for (Thread thread : ultrasonicMonitorThreads) {
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error stopping ultrasonic monitoring thread", e);
                }
            }
        }

        // Disconnect the Car API
        if (mCarPropertyManager != null) {
            Log.i(TAG, "CarPropertyManager released");
        }

        if (mCar != null) {
            mCar.disconnect();
            Log.i(TAG, "Car disconnected");
        }
    }
}
