package com.deerbrain.basesdcard

import android.util.Log

class AppLogger {
    companion object
    {
        fun errorMessage(tag:String,message:String){
            if(BuildConfig.DEBUG){
                Log.e(tag,message)
            }
        }
    }
}