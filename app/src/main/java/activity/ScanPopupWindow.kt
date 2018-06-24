package activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.App
import app.register
import app.unregister
import data.BluetoothDeviceInfoManager
import org.control.xudehua.moto_controler.R

class ScanDecoration : RecyclerView.ItemDecoration()
{
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView?)
    {
        super.getItemOffsets(outRect, itemPosition, parent)
        outRect.top = 25
    }
}

class ScanItem(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    private val mTextView = itemView.findViewById<TextView>(R.id.id_item_popup_text_view)
    private val mButton = itemView.findViewById<Button>(R.id.id_item_popup_button)

    fun set(position: Int)
    {
        val info = BluetoothDeviceInfoManager.instance.get(position)
        val address = info.address
        val name = info.name
        mTextView.text = "地址:$address,名称:$name"
        mButton.setOnClickListener {

            BluetoothDeviceInfoManager.instance.stopScan()
            BluetoothDeviceInfoManager.instance.Controller!!.connect(address)
            Toast.makeText(App.context, "开始连接:$address", Toast.LENGTH_SHORT).show()
        }
    }
}

class ScanPopupWindow : RecyclerView.Adapter<ScanItem>()
{
    companion object
    {
        val instance: ScanPopupWindow by lazy { ScanPopupWindow() }
    }

    private val mView = LayoutInflater.from(App.context).inflate(R.layout.popup_scan, null)
    private val mButtonCancel = mView.findViewById<Button>(R.id.id_popup_button_cancel)
    private val mRecyclerView = mView.findViewById<RecyclerView>(R.id.id_popup_recycler_view)
    private val mProgressBar = mView.findViewById<ProgressBar>(R.id.id_item_progress_bar)
    private var mPopupWindow: PopupWindow? = null

    override fun getItemCount() = BluetoothDeviceInfoManager.instance.getSize()

    init
    {
        mRecyclerView.adapter = this
        mRecyclerView.layoutManager = LinearLayoutManager(App.context, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.addItemDecoration(ScanDecoration())

        mButtonCancel.setOnClickListener {

            BluetoothDeviceInfoManager.instance.stopScan()
            mPopupWindow?.dismiss()
        }
    }

    fun show(view: View)
    {
        val group = mView.parent
        if (group != null) {
            (group as ViewGroup).removeAllViews()
        }
        mPopupWindow = PopupWindow(mView, 600, 1000, true)
        mPopupWindow!!.isOutsideTouchable = false
        mPopupWindow!!.showAtLocation(view, Gravity.CENTER, 0, 0)
        BluetoothDeviceInfoManager.instance.startScan(::onResult)
        mProgressBar.visibility = View.VISIBLE
        mReceiver.register(BluetoothDeviceInfoManager.ACTION_CONNECTED)
        mPopupWindow!!.setOnDismissListener {
            mReceiver.unregister()
        }
    }

    private fun onResult()
    {
        notifyDataSetChanged()
    }

    private val mReceiver = object: BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent)
        {
            Toast.makeText(App.context, "连接成功", Toast.LENGTH_SHORT).show()
            mPopupWindow?.dismiss()
        }
    }

    override fun onBindViewHolder(holder: ScanItem, position: Int)
    {
        holder.set(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanItem
    {
        val view = LayoutInflater.from(parent.context!!).inflate(R.layout.item_popup_recycler_view, parent, false)
        return ScanItem(view)
    }
}