package ir.ali.simplestopwatch.service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import java.util.*

class StopWatchService : Service() {

    companion object {
        const val TIME = "time"
        const val ACTION = "action"
    }

    var timer = Timer()

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Thread(Runnable {
            val time = intent.getDoubleExtra(TIME, 0.0)
            timer.scheduleAtFixedRate(StopWatchTimerTask(time), 0, 1000)

            val channelId = "foreground service channel Id"
            val channel = NotificationChannel(
                channelId, channelId,
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

            val builder = Notification.Builder(applicationContext, channelId)
                .setContentTitle("Timer Notification")
                .setContentText("Foreground Service Running ..")
                .setSmallIcon(android.R.drawable.stat_notify_chat)
            startForeground(100, builder.build())

        }).start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    private inner class StopWatchTimerTask(var time: Double) : TimerTask() {
        override fun run() {
            val intent = Intent(ACTION)
            time++
            intent.putExtra(TIME, time)
            sendBroadcast(intent)
        }

    }
}