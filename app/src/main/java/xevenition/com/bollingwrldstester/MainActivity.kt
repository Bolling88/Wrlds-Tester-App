package xevenition.com.bollingwrldstester

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wrlds.sdk.Ball

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class MainActivity : AppCompatActivity() {

    private var deviceAddress: String? = null
    private var ball: Ball? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestUseBluetooth()
    }

    private fun createBall() {
        ball = Ball(this)
        setUpListeners()
    }

    override fun onStart() {
        super.onStart()
        ball?.onStart(true)
    }

    override fun onStop() {
        super.onStop()
        ball?.onStop(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        ball?.onDestroy()
    }

    private fun setUpListeners() {
        ball?.setOnBounceListener { bounceType, totalForce ->
            Log.d(TAG, "Bound type: $bounceType and total force: $totalForce")
        }
        ball?.setOnConnectionStateChangedListener { connectionState, stateMessage ->
            when (connectionState) {
                Ball.ConnectionState.CONNECTION_FAILED -> {
                    Toast.makeText(this, "CONNECTION_FAILED", Toast.LENGTH_LONG).show()
                }
            }
        }
        ball?.setOnShakeListener { }
        ball?.setOnFifoDataReceivedListener { }
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
            //Device have no bluetooth, exit app
            Toast.makeText(this, "No bluetooth no fun!", Toast.LENGTH_LONG).show()
            finish()
        } else if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            ActivityCompat.startActivityForResult(this, enableBtIntent, REQUEST_ENABLE_BT, null)
        } else {
            //bluetooth already running!
            createBall()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_BT -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //we have bluetooth permission! Let's turn it on!
                    activateBluetooth()
                } else {
                    //Permission not granted, exit app
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
                    //Exit app
                    Toast.makeText(this, "No bluetooth no fun!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_PERMISSION_BT = 24341
        const val REQUEST_ENABLE_BT = 24342
    }
}
