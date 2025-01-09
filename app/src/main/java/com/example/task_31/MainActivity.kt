package com.example.task_31

import android.annotation.SuppressLint
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.task_31.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var warningTextView: TextView
    private lateinit var binding:ActivityMainBinding
    private lateinit var connectivityReceiver: BroadcastReceiver
    private  lateinit var capabilities:BroadcastReceiver
    private lateinit var weatherUpdateReceiver: BroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var stormWarningButton: Button
    private lateinit var stormWarningReceiver: storm_Warning

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: foreground")

        // we are  getting SharedPreferences from weather worker class

        sharedPreferences = getSharedPreferences("Weather_Data", Context.MODE_PRIVATE)

        binding.btn1.setOnClickListener {
            checkInternetConnection()
            Log.d(TAG, "button clicked for started")
            //weatherupdate()
            //Toast.makeText(applicationContext, "weather service started", Toast.LENGTH_SHORT).show()
        }

        binding.btn2.setOnClickListener {
            Log.d(TAG, "button clicked for stoped")
            stopService(Intent(this, foreground::class.java))
            Toast.makeText(applicationContext, "weather service stop", Toast.LENGTH_SHORT).show()
        }

        // storing button and text id's in stormWarningButton and warningTextView
        stormWarningButton = binding.btn3
        warningTextView = binding.text4

        //storing data from storm_warnings class

        stormWarningReceiver = storm_Warning()
        val filter1 = IntentFilter("STORM_WARNING")

        registerReceiver(stormWarningReceiver, filter1)


        stormWarningButton.setOnClickListener() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                Log.d(TAG, "Network connected")
                Toast.makeText(applicationContext, "Network connected", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent("STORM_WARNING")
                intent.putExtra("alert_message", "Storm Warning: weather condition is bad ")
                sendBroadcast(intent)
            } else {
                Log.d(TAG, "No network connection")
                Toast.makeText(applicationContext, "NO Network connected", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        scheduleWeatherWorker()
    }
        private fun scheduleWeatherWorker() {

            val weatherWorkRequest = PeriodicWorkRequest.Builder(WeatherWorker::class.java, 15, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(applicationContext).enqueue(weatherWorkRequest)
        }



    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()

        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                // storing data from CONNECTIVITY_SERVICE(it means internet is connected or not

                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)

                if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    Log.d(TAG, "Network connected")
                    Toast.makeText(applicationContext, "Network connected", Toast.LENGTH_SHORT)
                        .show()


                } else {
                    Log.d(TAG, "No network connection")
                    Toast.makeText(applicationContext, "NO Network connected", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, intentFilter)
        var weatherTextView = binding.text3
        weatherUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val weatherCondition = intent?.getStringExtra("weather_condition")
                if (weatherCondition != null) {
                    if (weatherCondition.trim() == "cloudy") {
                        weatherTextView.background = ContextCompat.getDrawable(weatherTextView.context, R.drawable.cloudy)
                        weatherTextView.text = "Current Weather: $weatherCondition 🌥️"
                    } else if (weatherCondition.trim() == "rainy") {
                        weatherTextView.background = ContextCompat.getDrawable(weatherTextView.context, R.drawable.rainy)
                        weatherTextView.text = "Current Weather: $weatherCondition ⛈️"
                    } else if (weatherCondition.trim() == "sunny") {
                        weatherTextView.background = ContextCompat.getDrawable(weatherTextView.context, R.drawable.sunny)
                        weatherTextView.text = "Current Weather: $weatherCondition ☀️"
                    } else if (weatherCondition.trim() == "windy") {
                        weatherTextView.background = ContextCompat.getDrawable(weatherTextView.context, R.drawable.windy)
                        weatherTextView.text = "Current Weather: $weatherCondition 💨"
                    }
                        else if(weatherCondition.trim()=="snowy"){
                            weatherTextView.background=ContextCompat.getDrawable(weatherTextView.context,R.drawable.snowy)
                            weatherTextView.text = "Current Weather: $weatherCondition ❄\uFE0F"
                    }
                    else if(weatherCondition.trim()=="stormy"){
                        weatherTextView.background=ContextCompat.getDrawable(weatherTextView.context,R.drawable.stormy)
                        weatherTextView.text = "Current Weather: $weatherCondition ⛈\uFE0F"
                    }
                    else {
                        weatherTextView.background =
                            ContextCompat.getDrawable(weatherTextView.context, R.drawable.weather)
                        weatherTextView.text = "Current Weather: Unknown"
                    }
                }

            }
        }
        val weatherUpdateFilter = IntentFilter("Wheather_Update")
        registerReceiver(weatherUpdateReceiver, weatherUpdateFilter)


    }

    private fun checkInternetConnection() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            startService(Intent(this,foreground::class.java))
            Toast.makeText(applicationContext, "Internet is connected", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(applicationContext, "No internet connection. Please check your internet.", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityReceiver)
        unregisterReceiver(weatherUpdateReceiver)
        unregisterReceiver(stormWarningReceiver)
    }


        companion object {
            const val TAG = "main activity"

        }

}