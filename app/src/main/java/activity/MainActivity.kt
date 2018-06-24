package activity

import android.Manifest
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import app.register
import app.unregister
import data.BluetoothDeviceInfoManager
import org.control.xudehua.moto_controler.R

class MainActivity : AppCompatActivity()
{
    private val mViewOTS: TextView by lazy { findViewById<TextView>(R.id.id_main_text_view_ots) }
    private val mViewAOCP: TextView by lazy { findViewById<TextView>(R.id.id_main_text_view_aocp) }
    private val mViewAPDF: TextView by lazy { findViewById<TextView>(R.id.id_main_text_view_apdf) }
    private val mViewBPDP: TextView by lazy { findViewById<TextView>(R.id.id_main_text_view_bpdf) }
    private val mViewULVO: TextView by lazy { findViewById<TextView>(R.id.id_main_text_view_ulvo) }

    private val mTextViewMoto1: TextView by lazy { findViewById<TextView>(R.id.id_main_text_view_moto1_speed) }
    private val mTextViewMoto2: TextView by lazy { findViewById<TextView>(R.id.id_main_text_view_moto2_speed) }
    private val mSeekBarMoto1: SeekBar by lazy { findViewById<SeekBar>(R.id.id_main_seek_bar_moto1_speed) }
    private val mSeekBarMoto2: SeekBar by lazy { findViewById<SeekBar>(R.id.id_main_seek_bar_moto2_speed) }

    private val mButton1CW: Button by lazy { findViewById<Button>(R.id.id_main_button_moto1_cw) }
    private val mButton1CCW: Button by lazy { findViewById<Button>(R.id.id_main_button_moto1_ccw) }
    private val mButton1Brake: Button by lazy { findViewById<Button>(R.id.id_main_button_moto1_brake) }
    private val mButton1Release: Button by lazy { findViewById<Button>(R.id.id_main_button_moto1_release) }

    private val mButton2CW: Button by lazy { findViewById<Button>(R.id.id_main_button_moto2_cw) }
    private val mButton2CCW: Button by lazy { findViewById<Button>(R.id.id_main_button_moto2_ccw) }
    private val mButton2Brake: Button by lazy { findViewById<Button>(R.id.id_main_button_moto2_brake) }
    private val mButton2Release: Button by lazy { findViewById<Button>(R.id.id_main_button_moto2_release) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
        initEvent()
        connectBluetoothService()
    }

    private fun requestPermission(): Boolean
    {
        val res = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (res != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return false
        }
        return true
    }

    private fun connectBluetoothService()
    {
        val intent = Intent(this, BluetoothLeService::class.java)
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE)

        mReceiver.register(BluetoothDeviceInfoManager.ACTION_CONNECTED, BluetoothDeviceInfoManager.ACTION_DISCONNECTED, BluetoothDeviceInfoManager.ACTION_READ)
    }

    private val mServiceConnection = object: ServiceConnection
    {
        override fun onServiceConnected(name: ComponentName?, service: IBinder)
        {
            BluetoothDeviceInfoManager.instance.Controller =  (service as BluetoothLeService.BluetoothLeBinder).getController()
        }

        override fun onServiceDisconnected(name: ComponentName?)
        {
        }
    }

    private val mReceiver = object: BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent)
        {
            when (intent.action)
            {
                BluetoothDeviceInfoManager.ACTION_READ -> {


                }

                BluetoothDeviceInfoManager.ACTION_CONNECTED -> {

                    setTitle("蓝牙连接成功")
                }

                BluetoothDeviceInfoManager.ACTION_DISCONNECTED -> {

                    Toast.makeText(this@MainActivity, "蓝牙已经断开", Toast.LENGTH_SHORT).show()
                    setTitle("蓝牙已经断开连接")
                }
            }
        }
    }

    override fun onDestroy()
    {
        BluetoothDeviceInfoManager.instance.Controller?.disConnect()
        mReceiver.unregister()
        unbindService(mServiceConnection)
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        if (keyCode == KeyEvent.KEYCODE_MENU && requestPermission())
        {
            if (BluetoothDeviceInfoManager.instance.getEnable())
            {
                ScanPopupWindow.instance.show(mTextViewMoto1)
            }
            else
            {
                Toast.makeText(this, "请打开蓝牙", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun onMoto1CW(view: View)
    {

    }

    private fun onMoto1CCW(view: View)
    {

    }

    private fun onMoto1Brake(view: View)
    {

    }

    private fun onMoto1Release(view: View)
    {

    }

    private fun onMoto2CW(view: View)
    {

    }

    private fun onMoto2CCW(view: View)
    {

    }

    private fun onMoto2Brake(view: View)
    {

    }

    private fun onMoto2Release(view: View)
    {

    }

    private fun initEvent()
    {
        mButton1CW.setOnClickListener(::onMoto1CW)
        mButton1CCW.setOnClickListener(::onMoto1CCW)
        mButton1Brake.setOnClickListener(::onMoto1Brake)
        mButton1Release.setOnClickListener(::onMoto1Release)

        mButton2CW.setOnClickListener(::onMoto2CW)
        mButton2CCW.setOnClickListener(::onMoto2CCW)
        mButton2Brake.setOnClickListener(::onMoto2Brake)
        mButton2Release.setOnClickListener(::onMoto2Release)
    }

    private fun initUi()
    {
        mSeekBarMoto1.max = 100
        mSeekBarMoto2.max = 100

        mSeekBarMoto1.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                mTextViewMoto1.text = "电机1速度:$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)
            {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?)
            {
            }
        })

        mSeekBarMoto2.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                mTextViewMoto2.text = "电机2速度:$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)
            {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?)
            {
            }
        })
    }

    private fun setStatus(status: Int)
    {
        if (0 != (status and 0x01)) {
            mViewOTS.setBackgroundResource(R.drawable.shape_error)
        } else {
            mViewOTS.setBackgroundResource(R.drawable.shape_normal)
        }

        if (0 != (status and 0x02)) {
            mViewAOCP.setBackgroundResource(R.drawable.shape_error)
        } else {
            mViewAOCP.setBackgroundResource(R.drawable.shape_normal)
        }

        if (0 != (status and 0x04)) {
            mViewAPDF.setBackgroundResource(R.drawable.shape_error)
        } else {
            mViewAPDF.setBackgroundResource(R.drawable.shape_normal)
        }

        if (0 != (status and 0x08)) {
            mViewBPDP.setBackgroundResource(R.drawable.shape_error)
        } else {
            mViewBPDP.setBackgroundResource(R.drawable.shape_normal)
        }

        if (0 != (status and 0x10)) {
            mViewULVO.setBackgroundResource(R.drawable.shape_error)
        } else {
            mViewULVO.setBackgroundResource(R.drawable.shape_normal)
        }
    }


}