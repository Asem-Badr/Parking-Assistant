package com.luxoft.ultrasonicaidltest;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.luxoft.ultrasonic.IUltrasonic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView txtUltrasonicOne, txtUltrasonicTwo, txtUltrasonicThree, txtUltrasonicFour;
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

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "Updating UI with ultrasonic reading");
                txtUltrasonicOne.setText(msg.arg1 + " cm");
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
                UltrasonicOneThreadRunning();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize ultrasonic service", e);
            Toast.makeText(this, "Error initializing service: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void UltrasonicOneThreadRunning() {
        new Thread("UltrasonicReader") {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        int reading;
                        synchronized (iUltrasonic) {
                            reading = iUltrasonic.getUltrasonicReading(0);
                        }
                        Message msg = handler.obtainMessage();
                        msg.arg1 = reading;
                        handler.sendMessage(msg);
                        sleep(500);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
