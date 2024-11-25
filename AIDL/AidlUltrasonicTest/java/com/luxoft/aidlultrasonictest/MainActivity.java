package com.luxoft.aidlultrasonictest;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luxoft.ultrasonic.IUltrasonic;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ULTRASONIC";

    private TextView txtUltrasonicOne, txtUltrasonicTwo, txtUltrasonicThree, txtUltrasonicFour, txtUltrasonicFive, txtUltrasonicSix;
    private IUltrasonic iUltrasonic;
    private Handler handler;
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUltrasonicOne = findViewById(R.id.txtUltrasonicOne);
        txtUltrasonicTwo = findViewById(R.id.txtUltrasonicTwo);
        txtUltrasonicThree = findViewById(R.id.txtUltrasonicThree);
        txtUltrasonicFour = findViewById(R.id.txtUltrasonicFour);
        txtUltrasonicFive = findViewById(R.id.txtUltrasonicFive);
        txtUltrasonicSix = findViewById(R.id.txtUltrasonicSix);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int sensorId = msg.what;
                int reading = msg.arg1;
                updateUI(sensorId, reading);
            }
        };

        try {
            // Reflectively access ServiceManager to get IUltrasonic service
            Class<?> localClass = Class.forName("android.os.ServiceManager");
            Method getService = localClass.getMethod("getService", String.class);
            Object result = getService.invoke(localClass, "com.luxoft.ultrasonic.IUltrasonic/default");
            if (result != null) {
                IBinder binder = (IBinder) result;
                iUltrasonic = IUltrasonic.Stub.asInterface(binder);
                UltrasonicThreadRunning();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize ultrasonic service", e);
            Toast.makeText(this, "Error initializing service: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void UltrasonicThreadRunning() {
        new Thread("UltrasonicReader") {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        for (int sensorId = 0; sensorId < 6; sensorId++) {
                            int reading;
                            reading = iUltrasonic.getUltrasonicReading(sensorId);
                            Message msg = handler.obtainMessage(sensorId, reading, 0);
                            handler.sendMessage(msg);
                            sleep(15); // Delay to prevent excessive polling
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "Failed to read ultrasonic value", e);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Thread interrupted", e);
                        break;
                    }
                }
            }
        }.start();
    }

    private void updateUI(int sensorId, int reading) {
        String readingText = reading + " cm";
        Log.e(TAG, "sensor ID: " + sensorId + "UI Updating");
        switch (sensorId) {
            case 0:
                txtUltrasonicOne.setText(readingText);
                break;
            case 1:
                txtUltrasonicTwo.setText(readingText);
                break;
            case 2:
                txtUltrasonicThree.setText(readingText);
                break;
            case 3:
                txtUltrasonicFour.setText(readingText);
                break;
            case 4:
                txtUltrasonicFive.setText(readingText);
                break;
            case 5:
                txtUltrasonicSix.setText(readingText);
                break;
            default:
                Log.e(TAG, "Invalid sensor ID: " + sensorId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        isRunning = false;
    }
}