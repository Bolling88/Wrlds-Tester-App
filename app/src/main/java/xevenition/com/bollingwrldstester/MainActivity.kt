package xevenition.com.bollingwrldstester

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.wrlds.sdk.Ball

class MainActivity : AppCompatActivity() {

    private var ball: Ball? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestUseBluetooth()
    }

    private fun requestUseBluetooth() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_PERMISSION_BT
            )
        } else {
            //we have bluetooth permission! Let's turn it on!
            activateBluetooth()
        }
    }

    private fun activateBluetooth() {
        val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            //Damn device have no bluetooth, exit app
            Toast.makeText(this, "No bluetooth no fun!", Toast.LENGTH_LONG).show()
            finish()
        } else if (!mBluetoothAdapter?.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            ActivityCompat.startActivityForResult(this, enableBtIntent, REQUEST_ENABLE_BT, null)
        } else {
            //bluetooth already running!
            createBall()
        }
    }

    private fun createBall() {
        Ball.create(this) {
            ball = it
            setUpListeners()
            ball?.scanForDevices()
        }
    }

    private fun setUpListeners() {
        ball?.setOnBounceListener { bounceType, totalForce ->
            Log.d(TAG, "Bounde type: $bounceType and total force: $totalForce")
        }
        ball?.setOnConnectionStateChangedListener { connectionState, stateMessage ->

        }
        ball?.setOnShakeListener { }
        ball?.setOnFifoDataRecievedListener { }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_BT -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //we have bluetooth permission! Let's turn it on!
                    activateBluetooth()
                } else {
                    //Damn, exit app
                    Toast.makeText(this, "No bluetooth no fun!", Toast.LENGTH_LONG).show()
                    finish()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == RESULT_OK) {
                    //Bluetooth is turned on! Lets do this!
                    createBall()
                } else {
                    //Damn, exit app
                    Toast.makeText(this, "No bluetooth no fun!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ball?.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_PERMISSION_BT = 24341
        const val REQUEST_ENABLE_BT = 24342
    }
}
