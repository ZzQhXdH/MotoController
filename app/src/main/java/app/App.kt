package app

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

class App : Application()
{
    companion object
    {
        lateinit var context: Context
            private set
    }

    override fun onCreate()
    {
        super.onCreate()
        context = applicationContext
    }
}

fun BroadcastReceiver.register(vararg actions: String)
{
    val filter = IntentFilter()
    for (action in actions) {
        filter.addAction(action)
    }
    LocalBroadcastManager.getInstance(App.context).registerReceiver(this, filter)
}

fun BroadcastReceiver.unregister()
{
    LocalBroadcastManager.getInstance(App.context).unregisterReceiver(this)
}

fun LocalBroadcastManager.sendAction(action: String)
{
    val intent = Intent(action)
    this.sendBroadcast(intent)
}

inline fun log(msg: String, tag: String = "调试")
{
    Log.d(tag, msg)
}



