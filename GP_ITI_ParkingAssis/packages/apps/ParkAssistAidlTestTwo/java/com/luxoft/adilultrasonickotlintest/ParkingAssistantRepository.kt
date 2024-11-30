package com.luxoft.adilultrasonickotlintest

import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.luxoft.buzzer.IBuzzer
import com.luxoft.steeringwheel.ISteeringWheel
import com.luxoft.ultrasonic.IUltrasonic



class ParkingAssistantRepository : IParkingAssistantRepository {
    private var iUltrasonic: IUltrasonic? = null
    private var iBuzzer: IBuzzer? = null
    private var iSteeringWheel: ISteeringWheel? = null


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
                Log.e("ParkAssist", "Buzzer service not available.")
            }
        } catch (e: Exception) {
            Log.e("ParkAssist", "Error binding buzzer service", e)

        }


        try {
            // Reflectively access ServiceManager to get IGpio service
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val service = getServiceMethod.invoke(serviceManagerClass, "com.luxoft.steeringwheel.ISteeringWheel/default") as? IBinder

            if (service != null) {
                val binder = service as IBinder
                iSteeringWheel = ISteeringWheel.Stub.asInterface(binder)

            }
        } catch (e: Exception) {
            Log.e("ParkAssist", "Failed to initialize GPIO service: ${e.message}", e)
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
    override fun setBuzzerLevel(level: Int): Boolean {
        return try {
            iBuzzer?.setbuzzerLevel(level) ?: false
        } catch (e: RemoteException) {
            e.printStackTrace()
            false
        }
    }


    override fun getSteeringWheelAngle(): Int {
        return try {
            iSteeringWheel?.steeringWheelAngle ?: 0
        } catch (e: RemoteException) {
            Log.e("ParkAssist", "Error fetching steering wheel angle", e)
            0
        }
    }

}
