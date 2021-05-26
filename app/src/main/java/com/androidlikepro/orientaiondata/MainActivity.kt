package com.androidlikepro.orientaiondata

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidlikepro.orientationsdk.ICallback
import com.androidlikepro.orientationsdk.IOrientationAidlInterface
import com.androidlikepro.orientationsdk.OrientaionService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mCallback : ICallback
    private var isBound: Boolean = false
    private var mService: IOrientationAidlInterface? = null

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = IOrientationAidlInterface.Stub.asInterface(service)
            Toast.makeText(
                this@MainActivity,
                "Remote service connected",
                Toast.LENGTH_SHORT
            ).show()
            mService?.registerCallback(mCallback)
            if (mService != null) {
                val pitch = mService?.orientaion()?.get(0)?.toString()
                val roll = mService?.orientaion()?.get(1)?.toString()
                orientation_data_tv.text = "pitch : $pitch  \n roll : $roll"
                    Toast.makeText(
                        this@MainActivity,
                        "Data loaded...",
                        Toast.LENGTH_SHORT
                    ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Service is not connected...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("Orientation_TAG", "Service has unexpectedly disconnected")
            mService = null
        }

    }

    private val mICallback = object : ICallback.Stub() {
        override fun orientaionData(data: FloatArray?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun bindService() {
        val intent = Intent(this, com.androidlikepro.orientationsdk.OrientaionService::class.java)
        intent.action = IOrientationAidlInterface::class.java.name
        intent.setPackage("com.androidaidl.androidaidlservice")
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        isBound = true

        orientation_data_tv.setOnClickListener {
            if (mService != null) {
                val pitch = mService?.orientaion()?.get(0)?.toString()
                val roll = mService?.orientaion()?.get(1)?.toString()
                orientation_data_tv.text = "pitch : $pitch  \n roll : $roll"
                Toast.makeText(
                    this@MainActivity,
                    "Data loaded...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindService()
    }

    override fun onStop() {
        super.onStop()
        unbindService(mConnection)
        isBound = false
    }
}