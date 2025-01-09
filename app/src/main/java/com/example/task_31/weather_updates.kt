package com.example.task_31

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.util.Log

@SuppressLint("ParcelCreator")
class weather_updates() : BroadcastReceiver(), Parcelable {


    override fun onReceive(context: Context, intent: Intent) {

        if(intent.action == ("android.net.conn.CONNECTIVITY_CHANGE")){
            Log.d(TAG, "onReceive: ${intent.extras?.keySet()?.toList()}")
            var state=intent.extras?.getBoolean("state")
            Log.d(TAG, "onReceive: internetnet connection is $state")

        }

    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }



    companion object {
        const val TAG = "internet connection"
        var event = Intent("android.net.conn.CONNECTIVITY_CHANGE")
    }


    }
