package com.luxoft.adilultrasonickotlintest

import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.luxoft.buzzer.IBuzzer
import com.luxoft.ultrasonic.IUltrasonic



class ParkingAssistantRepository : IParkingAssistantRepository {
    private var iUltrasonic: IUltrasonic? = null
    private var iBuzzer: IBuzzer? = null


    init {
        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, "com.luxoft.ultrasonic.IUltrasonic/default") as IBinder
            iUltrasonic = IUltrasonic.Stub.asInterface(binder)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to initialize ultrasonic service: ${e.message}")
        }

        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val service = getServiceMethod.invoke(null, "com.luxoft.buzzer.IBuzzer/default")

            if (service != null) {
                val binder = service as IBinder
                iBuzzer = IBuzzer.Stub.asInterface(binder)
            } else {
                Log.e("BUZZER", "Buzzer service not available.")
            }
        } catch (e: Exception) {
            Log.e("BUZZER", "Error binding buzzer service", e)

        }

    }

    override fun getUltrasonicReading(sensorId: Int): Int {
        return try {
            iUltrasonic?.getUltrasonicReading(sensorId) ?: 0
        } catch (e: RemoteException) {
            e.printStackTrace()
            0
        }
    }
}
