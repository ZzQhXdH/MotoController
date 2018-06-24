package data

import android.bluetooth.le.ScanResult

class BluetoothDeviceInfo(info: ScanResult)
{
    val name = info.device?.name ?: "未知设备"
    val rssi = info.rssi
    val address = info.device.address

    override fun equals(other: Any?): Boolean
    {
        return (other as BluetoothDeviceInfo).address == address
    }

    override fun toString(): String
    {
        return name
    }
}
