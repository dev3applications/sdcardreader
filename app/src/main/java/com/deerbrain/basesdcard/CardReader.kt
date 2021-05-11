package com.deerbrain.basesdcard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.cardreader.*


private const val TAG = "CardReader"

class CardReader : AppCompatActivity() {

    var directory: String = ""
    private var itemsFromSDCard = mutableListOf<FileItem>()
    var innerList: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cardreader)
        Dexter.withActivity(this)
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted, open the camera
                    updateRecyclerView()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        // navigate user to app settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()


    }

    private fun updateRecyclerView() {

        //updateDisplayItems(directory)
        innerList = intent.getStringExtra("innerList").toString()
        if (innerList.equals("null")) {
            itemsFromSDCard =
                CardFileManager.getFilesWithPath(CardFileManager.SDPath()!!, this, true)
        } else {
            itemsFromSDCard = CardFileManager.getFilesWithPath(innerList, this, false)
        }
        Log.e(TAG, itemsFromSDCard.size.toString() + ".....file size")
        CardReaderItemList.layoutManager = LinearLayoutManager(this)
        val cardReaderInfo = CardReaderAdapter(this)
        CardReaderItemList.adapter = cardReaderInfo
        cardReaderInfo.update(itemsFromSDCard)

    }

    private fun updateDisplayItems(path: String) {
        val basePath = "baseCardPath/"
        var pathToShow = basePath + path

        /* itemsFromSDCard = CardFileManager().getFilesWithPath(pathToShow)*/
    }

}