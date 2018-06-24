package app

import android.os.Handler
import android.os.HandlerThread

class Task
{
    companion object
    {
        val instance: Task by lazy { Task() }
    }

    private val mAsyncThread = HandlerThread("Async")
    val AsyncHandler: Handler

    init
    {
        mAsyncThread.start()
        AsyncHandler = Handler(mAsyncThread.looper)
    }

    private constructor()
}