package com.deerbrain.basesdcard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

private const val TAG = "SdCardBroadcastReceiver"

class SdCardBroadcastReceiver : BroadcastReceiver() {
    val MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED"
    val MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL"

    //      final String MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
    val MEDIA_EJECT = "android.intent.action.MEDIA_EJECT"
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        Toast.makeText(context, "receiver call", Toast.LENGTH_SHORT).show()
        AppLogger.errorMessage(TAG, action.toString())
        var intent=Intent("deviceStatus")
        if (action.equals(MEDIA_REMOVED)|| action.equals(
                MEDIA_BAD_REMOVAL
            ) || action.equals(MEDIA_EJECT)
        ) {
        intent.putExtra("isConnected",false)
        } else {
            intent.putExtra("isConnected",true)
            AppLogger.errorMessage(TAG, " connected")
        }
        context!!.sendBroadcast(intent)
    }
}