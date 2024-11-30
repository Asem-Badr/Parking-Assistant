package com.luxoft.testadil;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luxoft.steeringwheel.ISteeringWheel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    ISteeringWheel iSteeringWheel;
    private static final String TAG = "MainActivity";
    Button btnOn;
    Button btnOff;
    TextView txtValue;
    TextView txtRead;
    private volatile boolean isUpdating = true; // To control the live updates thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // // Initialize UI elements
        // btnOn = findViewById(R.id.btnOn);
        // btnOff = findViewById(R.id.btnOff);
        txtValue = findViewById(R.id.txtValue);
        txtRead = findViewById(R.id.textRead);

        try {
            // Reflectively access ServiceManager to get IGpio service
            Class<?> localClass = Class.forName("android.os.ServiceManager");
            Method getService = localClass.getMethod("getService", String.class);
            if (getService != null) {
                Object result = getService.invoke(localClass, "com.luxoft.steeringwheel.ISteeringWheel/default");
                if (result != null) {
                    IBinder binder = (IBinder) result;
                   iSteeringWheel = ISteeringWheel.Stub.asInterface(binder);

                    // Start live updates in a background thread
                    startLiveUpdates();

                    // // Button listeners
                    // btnOn.setOnClickListener(v -> {
                    //     try {
                    //         Boolean result1 = iGpio.setGpioValue(18, true);
                    //         txtValue.setText(Boolean.toString(result1));
                    //         Log.d(TAG, "Set GPIO 18 to ON: " + result1);
                    //         Toast.makeText(MainActivity.this, "GPIO 18 ON: " + result1, Toast.LENGTH_SHORT).show();
                    //     } catch (RemoteException e) {
                    //         Log.e(TAG, "Failed to set GPIO 18 to ON: " + e.getMessage(), e);
                    //         Toast.makeText(MainActivity.this, "Error turning GPIO 18 ON", Toast.LENGTH_SHORT).show();
                    //     }
                    // });

                    // btnOff.setOnClickListener(v -> {
                    //     try {
                    //         Boolean result1 = iGpio.setGpioValue(18, false);
                    //         txtValue.setText(Boolean.toString(result1));
                    //         Log.d(TAG, "Set GPIO 18 to OFF: " + result1);
                    //         Toast.makeText(MainActivity.this, "GPIO 18 OFF: " + result1, Toast.LENGTH_SHORT).show();
                    //     } catch (RemoteException e) {
                    //         Log.e(TAG, "Failed to set GPIO 18 to OFF: " + e.getMessage(), e);
                    //         Toast.makeText(MainActivity.this, "Error turning GPIO 18 OFF", Toast.LENGTH_SHORT).show();
                    //     }
                    // });
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(TAG, "Failed to initialize GPIO service: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing service: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startLiveUpdates() {
        new Thread(() -> {
            while (isUpdating) {
                try {
                    // Fetch the updated GPIO reading
                    int reading = iSteeringWheel.getSteeringWheelAngle();

                    // Update the UI on the main thread
                    runOnUiThread(() -> {
                        txtRead.setText(String.valueOf(reading));
                        Log.d(TAG, "Live reading update: " + reading);
                    });

                    // Sleep for a while before fetching again
                    Thread.sleep(200); // Update every 1 second
                } catch (RemoteException e) {
                    Log.e(TAG, "Error fetching live reading: " + e.getMessage(), e);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Live update thread interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the live updates thread
        isUpdating = false;
    }
}
