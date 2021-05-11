package com.deerbrain.basesdcard

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    var isSDCardReaderConnected: Boolean =
        false //This needs to detect when an SD card is connected and adjust value as needed. This would preferalbly be an enum rather than a boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        val isSDSupportedDevice: Boolean = Environment.isExternalStorageRemovable()

        if (CardFileManager.hasSdCard(this)) {
            // yes SD-card is present
            Log.e(TAG, "yes sd card is present")
            connectedButton.text = "SD Card Connected"
            isSDCardReaderConnected = true


        } else {
            // Sorry
            Log.e(TAG, "no sd card is present")
            connectedButton.text = "SD Card Not Connected"
            isSDCardReaderConnected = false


        }
        connectedButton.setOnClickListener {
            connectedButtonPressed()
        }

        fakeConnectedSDCard.setOnClickListener {
            fakeConnecedSDCardButtonPressed()
        }

        goToMap.setOnClickListener {
            val intent = Intent(this, CardReader::class.java)
            startActivity(intent)
        }


    }


    fun connectedButtonPressed() {
        if (isSDCardReaderConnected == true) {
            val intent = Intent(this, CardReader::class.java)
            startActivity(intent)
            //pass in the the base file directory
        } else {
            Toast.makeText(this, "No SD Card Reader Connected", Toast.LENGTH_SHORT).show()
        }
    }

    fun fakeConnecedSDCardButtonPressed() {
        if (isSDCardReaderConnected == true) {
            isSDCardReaderConnected = false
            connectedButton.text = "SD Card Not Connected"
        } else {
            isSDCardReaderConnected = true
            connectedButton.text = "SD Card Connected"
        }
    }


}