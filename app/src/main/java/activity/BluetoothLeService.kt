package activity

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import app.Task
import app.log
import data.BluetoothDeviceInfoManager
import java.util.*

class BluetoothLeService : Service(), BluetoothController
{
    companion object
    {
        private const val SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb"
        private const val CHARA_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb"
        private const val DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"
    }

    private lateinit var mManager: BluetoothManager
    private lateinit var mAdapter: BluetoothAdapter
    private var mChara: BluetoothGattCharacteristic? = null
    private var mGatt: BluetoothGatt? = null
    private lateinit var mLocalBroadcast: LocalBroadcastManager

    override fun onCreate()
    {
        super.onCreate()
        mManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mAdapter = mManager.adapter
        mLocalBroadcast = LocalBroadcastManager.getInstance(this)
    }

    override fun write(byteArray: ByteArray)
    {
        Task.instance.AsyncHandler.postDelayed( {

            mChara!!.setValue(byteArray)
            mGatt!!.writeCharacteristic(mChara)
        }, 10)
    }

    override fun isConnected(): Boolean
    {
        return mGatt != null
    }

    override fun disConnect()
    {
        mGatt?.close()
        mGatt = null
    }

    override fun connect(address: String)
    {
        if (mGatt != null) {
            disConnect()
        }
        val device = mAdapter.getRemoteDevice(address)
        mGatt = device.connectGatt(this, false, mGattCallback)
    }

    private val mGattCallback = object: BluetoothGattCallback()
    {
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int)
        {
            super.onServicesDiscovered(gatt, status)
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return
            }
            val service = mGatt!!.getService(UUID.fromString(SERVICE_UUID))
            mChara = service.getCharacteristic(UUID.fromString(CHARA_UUID))
            val des = mChara!!.getDescriptor(UUID.fromString(DESCRIPTOR_UUID))
            mGatt!!.setCharacteristicNotification(mChara, true)
            des.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            mGatt!!.writeDescriptor(des)
            updateBroadcast(BluetoothDeviceInfoManager.ACTION_CONNECTED)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic)
        {
            updateBroadcast(BluetoothDeviceInfoManager.ACTION_READ, characteristic.value)
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int)
        {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return
            }

            when (newState)
            {
                BluetoothProfile.STATE_CONNECTED -> {
                    mGatt!!.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    updateBroadcast(BluetoothDeviceInfoManager.ACTION_DISCONNECTED)
                }
            }
        }
    }

    private inline fun updateBroadcast(action: String)
    {
        val intent = Intent(action)
        mLocalBroadcast.sendBroadcast(intent)
    }

    private inline fun updateBroadcast(action: String, byteArray: ByteArray)
    {
        val intent = Intent(action)
        intent.putExtra(action, byteArray)
        mLocalBroadcast.sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder
    {
        return BluetoothLeBinder()
    }

    inner class BluetoothLeBinder : Binder()
    {
        fun getController(): BluetoothController
        {
            return this@BluetoothLeService
        }
    }
}

interface BluetoothController
{
    fun write(byteArray: ByteArray)

    fun isConnected(): Boolean

    fun disConnect()

    fun connect(address: String)
}