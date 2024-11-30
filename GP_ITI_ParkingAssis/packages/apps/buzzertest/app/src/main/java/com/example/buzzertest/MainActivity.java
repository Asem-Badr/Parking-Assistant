package com.example.buzzertest;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luxoft.buzzer.IBuzzer;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BUZZER_TEST";
    private Button btn0, btn1, btn2, btn3, btn4;
    private IBuzzer iBuzzer;
    private int currentLevel = -1; // -1 indicates no active level
    private boolean isRunning = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btn0 = findViewById(R.id.btn_0);
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);

        handler = new Handler(Looper.getMainLooper());

        // Bind to the IBuzzer service
        try {
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
            Object service = getServiceMethod.invoke(null, "com.luxoft.buzzer.IBuzzer/default");

            if (service != null) {
                IBinder binder = (IBinder) service;
                iBuzzer = IBuzzer.Stub.asInterface(binder);
            } else {
                Toast.makeText(this, "Buzzer service not found", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Buzzer service not available.");
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to bind buzzer service: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error binding buzzer service", e);
            return;
        }

        // Set button click listeners
        btn0.setOnClickListener(v -> stopBuzzer());
        btn1.setOnClickListener(v -> startBuzzer(1));
        btn2.setOnClickListener(v -> startBuzzer(2));
        btn3.setOnClickListener(v -> startBuzzer(3));
        btn4.setOnClickListener(v -> startBuzzer(4));
    }

    private void startBuzzer(int level) {
        if (currentLevel != level) {
            currentLevel = level;
            isRunning = true;
            Toast.makeText(this, "Starting buzzer at level " + level, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Buzzer level " + level + " started.");
            runBuzzer();
        } else {
            Toast.makeText(this, "Buzzer already running at level " + level, Toast.LENGTH_SHORT).show();
        }
    }

    private void stopBuzzer() {
        isRunning = false;
        currentLevel = -1;
        try {
            if (iBuzzer != null) {
                boolean success = iBuzzer.setbuzzerLevel(0); // Stop the buzzer
                if (success) {
                    Toast.makeText(this, "Buzzer stopped", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Buzzer stopped.");
                } else {
                    Toast.makeText(this, "Failed to stop buzzer", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (RemoteException e) {
            Toast.makeText(this, "Error stopping buzzer", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "RemoteException while stopping buzzer", e);
        }
    }

    private void runBuzzer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isRunning && currentLevel != -1) {
                    try {
                        if (iBuzzer != null) {
                            boolean success = iBuzzer.setbuzzerLevel(currentLevel);
                            if (!success) {
                                Log.e(TAG, "Failed to set buzzer level " + currentLevel);
                            }
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "RemoteException while running buzzer", e);
                    }

                    // Repeat after a delay if still running
                    handler.postDelayed(this, 1000); // Adjust delay as necessary
                }
            }
        });
    }
}
