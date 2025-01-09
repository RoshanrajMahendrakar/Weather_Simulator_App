package com.example.task_31

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.content.SharedPreferences

class WeatherWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Get the weather data (this can be fetched from an API or use hardcoded data for now)
        val weatherCondition = "sunny"  // Example hardcoded weather

        // Log the weather data
        Log.d(TAG, "Weather update: $weatherCondition")

        // Store the weather data in SharedPreferences
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("Weather_Data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("weather_condition", weatherCondition)
        editor.apply()

        // Return success
        return Result.success()
    }

    companion object {
        const val TAG = "WeatherWorker"
    }
}
