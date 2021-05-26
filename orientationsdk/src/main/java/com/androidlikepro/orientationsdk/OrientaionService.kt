package com.androidlikepro.orientationsdk

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.RemoteCallbackList
import android.widget.Toast


open class OrientaionService : Service(), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var mRotationSensor: Sensor? = null
    val mCallback = RemoteCallbackList<ICallback>()

    private val SENSOR_DELAY = 8 * 1000 // 8ms
    private val FROM_RADS_TO_DEGS = -57
    val data: FloatArray = FloatArray(2)

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private val binder = object : IOrientationAidlInterface.Stub() {
        override fun orientaion(): FloatArray {
            try {
                mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
                mRotationSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
                mSensorManager?.registerListener(
                    this@OrientaionService,
                    mRotationSensor,
                    SENSOR_DELAY
                )

            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "Hardware compatibility issue",
                    Toast.LENGTH_LONG
                ).show()
            }
            return data
        }

        override fun registerCallback(callback: ICallback?) {
            callback ?: mCallback.register(callback)
        }

        override fun unregisterCallback(callback: ICallback?) {
            callback ?: mCallback.unregister(callback)
        }

    }

    val mCallback = object: ICallback.Stub(){
        override fun orientaionData(data: FloatArray?) {

        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor === mRotationSensor) {
            if (event?.values!!.size > 4) {
                val truncatedRotationVector = FloatArray(4)
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4)
                updateOrientation(truncatedRotationVector)
            } else {
                updateOrientation(event.values)
                binder.orientaion()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun updateOrientation(vectors: FloatArray) {
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors)
        val worldAxisX = SensorManager.AXIS_X
        val worldAxisZ = SensorManager.AXIS_Z
        val adjustedRotationMatrix = FloatArray(9)
        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            worldAxisX,
            worldAxisZ,
            adjustedRotationMatrix
        )
        val orientation = FloatArray(3)
        SensorManager.getOrientation(adjustedRotationMatrix, orientation)
        val pitch = orientation[1] * FROM_RADS_TO_DEGS
        val roll = orientation[2] * FROM_RADS_TO_DEGS
        data[0] = pitch
        data[1] = roll
    }


}