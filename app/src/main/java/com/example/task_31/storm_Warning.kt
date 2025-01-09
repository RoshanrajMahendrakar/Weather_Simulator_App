package com.example.task_31

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class storm_Warning() :BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // accessing the data from main activity what store in put extra
        val message = intent.getStringExtra("alert_message") ?: "No message"

        Log.d(TAG, "Storm Alert")
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        if (context is MainActivity) {
            context.warningTextView.text = message
        }
    }
    companion object{
        val TAG="storm_warning"
    }
}