package com.example.task_31

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.task_31.MainActivity.Companion
import kotlin.random.Random




class foreground:Service(){
    private val CHANNEL_ID = "weather_service_channel"
    private val handler = Handler()
    private val weatherupdating = arrayOf("sunny", "rainy", "cloudy", "windy","snowy","stormy")

    private val updateWeatherRunnable = object : Runnable {
        override fun run() {
            val weather=weatherupdating[Random.nextInt(weatherupdating.size)]
            updatenotification(weather)
            val intent = Intent("Wheather_Update")
            intent.putExtra("weather_condition", weather)
            sendBroadcast(intent)
            handler.postDelayed(this,5000)
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate:foreground ")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Service Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for weather service updates"
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Start the service as a foreground service
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Service Running")
            .setContentText("Fetching weather updates...")
            .setSmallIcon(android.R.drawable.btn_star)
            .build()
        startForeground(1,notification)

        handler.post(updateWeatherRunnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand: ")
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
        Log.d(TAG, "onBind: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        handler.removeCallbacks(updateWeatherRunnable)
        stopForeground(true)
    }

    private  fun updatenotification(weather: String) {

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Update")
            .setContentText("Current weather: $weather")
            .setSmallIcon(android.R.drawable.star_on)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notification)
    }
    companion object{
        val TAG="foreground"
    }
}

