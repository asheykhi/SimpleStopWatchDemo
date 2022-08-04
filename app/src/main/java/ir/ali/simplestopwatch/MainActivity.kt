package ir.ali.simplestopwatch

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ir.ali.simplestopwatch.databinding.ActivityMainBinding
import ir.ali.simplestopwatch.service.StopWatchService
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    var time = 0.0
    lateinit var binding: ActivityMainBinding
    lateinit var foregroundService: Intent
    var isStarted = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        foregroundService = Intent(applicationContext, StopWatchService::class.java)
        registerReceiver(receiver, IntentFilter(StopWatchService.ACTION))

        if (serviceExist()) {
            isStarted = true
            binding.btnStart.text = getString(R.string.stop)
        }

        binding.btnStart.setOnClickListener {

            if (isStarted) {
                isStarted = false
                binding.btnStart.text = getString(R.string.start)
                stopService(foregroundService)

            } else {
                if (!serviceExist()) {
                    foregroundService.putExtra(StopWatchService.TIME, time)
                    isStarted = true
                    binding.btnStart.text = getString(R.string.stop)
                    startForegroundService(foregroundService)
                }
            }
        }

        binding.btnReset.setOnClickListener {
            isStarted = false
            time = 0.0
            binding.btnStart.text = getString(R.string.start)
            binding.tvTimer.text = parseFormat(time)
            stopService(foregroundService)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun serviceExist(): Boolean {
        var flag = false
        val manager = getSystemService(ActivityManager::class.java) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (StopWatchService::class.java.name.equals(service.service.className)) {
                flag = true
                break
            }
        }
        return flag
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            time = intent.getDoubleExtra(StopWatchService.TIME, 0.0)
            binding.tvTimer.text = parseFormat(time)
        }
    }

    private fun parseFormat(time: Double): String {
        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}