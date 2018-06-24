package data

import activity.BluetoothController
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.widget.Toast
import app.App


class BluetoothDeviceInfoManager: ScanCallback
{
    companion object
    {
        const val ACTION_SEND_ADDRESS = "action.send.address"
        const val ACTION_CONNECTED = "action.connected"
        const val ACTION_DISCONNECTED = "action.disconnected"
        const val ACTION_READ = "action.read"

        val instance: BluetoothDeviceInfoManager by lazy { BluetoothDeviceInfoManager() }
    }

    private val mManager = App.context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val mAdapter = mManager.adapter
    private val mScanner = mAdapter.bluetoothLeScanner
    private val mInfoList = arrayListOf<BluetoothDeviceInfo>()
    private var mResultCallback = {}
    var Controller: BluetoothController? = null

    fun getSize() = mInfoList.size

    fun get(index: Int) = mInfoList[index]

    fun getEnable() = mAdapter.isEnabled

    fun startScan(callback: () -> Unit): Boolean
    {
        mInfoList.clear()
        callback()
        mResultCallback = callback
        mScanner.startScan(this)
        return true
    }

    fun stopScan()
    {
        mScanner.stopScan(this)
    }

    override fun onScanResult(callbackType: Int, result: ScanResult)
    {
        val info = BluetoothDeviceInfo(result)
        if (!mInfoList.contains(info)) {
            mInfoList.add(info)
            mResultCallback()
        }
    }

    private constructor()

}