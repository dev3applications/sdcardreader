package com.deerbrain.basesdcard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

private const val TAG = "SdCardBroadcastReceiver"

class SdCardBroadcastReceiver : BroadcastReceiver() {
    val MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED"
    val MEDIA_EJECT = "android.intent.action.MEDIA_EJECT"
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        AppLogger.errorMessage(TAG, action.toString())
        var intent = Intent("deviceStatus")
        if (action.equals(MEDIA_UNMOUNTED) || action.equals(MEDIA_EJECT)
        ) {
            intent.putExtra("isConnected", false)
        } else {
            intent.putExtra("isConnected", true)
        }
        context!!.sendBroadcast(intent)
    }
}