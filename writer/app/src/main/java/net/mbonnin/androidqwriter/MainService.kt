package net.mbonnin.androidqwriter

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import kotlinx.coroutines.*

import net.mbonnin.androidqwriter.R
import java.io.File

class MainService : Service() {

    private var job: Job? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    class StopServiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stop(context)
        }
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("MainService", "onCreate")

        val intent = Intent()
        intent.action = ACTION
        val stopPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentText("Android Q writer is running")
            .addAction(R.drawable.ic_launcher_foreground, "quit", stopPendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        job = GlobalScope.launch(Dispatchers.Main) {
            val file = File(getExternalFilesDir(null), "log.txt")
            val os = file.outputStream().bufferedWriter()
            var i = 0
            while (true) {
                os.write("This is line: $i\n")
                os.flush()
                delay(500)
                i++
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job?.cancel()
        Log.d("MainService", "onDestroy")
    }

    companion object {
        private val NOTIFICATION_ID = 42
        var registered = false
        val ACTION = "net.mbonnin.androidq.StopServiceBroadcastReceiver"
        val NOTIFICATION_CHANNEL_ID = "channel_id"

        fun stop(context: Context) {
            val serviceIntent = Intent()
            serviceIntent.setClass(context, MainService::class.java)
            context.stopService(serviceIntent)
        }

        fun start(context: Context) {
            if (!registered) {
                val filter = IntentFilter()
                filter.addAction(ACTION)
                context.registerReceiver(StopServiceBroadcastReceiver(), filter)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val name = "AndroidQWriter"
                val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
                mNotificationManager.createNotificationChannel(mChannel)
            }

            val serviceIntent = Intent()
            serviceIntent.setClass(context, MainService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}

