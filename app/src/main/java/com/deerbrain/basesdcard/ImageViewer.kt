package com.deerbrain.basesdcard

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.reflect.Type

class ImageViewer : AppCompatActivity(), View.OnClickListener {
    //var shownImage: Image?
    var imageIndex = 0
//    var imageIndex = 3 {
//        didSet {
//            updateDisplayedImage()
//        }
//    }  //not sure how to do this in kotlin but in swift when the imageIndex is chanaged then the 'didset' gets called

    var list = mutableListOf<FileItem>()
    lateinit var selectedItemUrl: String
    var gson = Gson()
    lateinit var forward: ImageView
    lateinit var rewind: ImageView
    lateinit var imageView: ImageView
    //this was probably made in the wrong manner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_viewer)
        val imagesList: Type = object : TypeToken<List<FileItem?>?>() {}.getType()
        val imgList: ArrayList<FileItem> =
            gson.fromJson(intent.getStringExtra("imageList"), imagesList)
        list.addAll(imgList)

        selectedItemUrl = intent.getStringExtra("selectedImage").toString()
        imageIndex = CardFileManager.getSelectedImageIndex(selectedItemUrl, list)
        forward = findViewById(R.id.forward)
        rewind = findViewById(R.id.rewind)
        imageView = findViewById(R.id.imageView)


//        nextButton.SetOnClickListener{
//            nextImageButtonPressed()
//        }
//
//        previousButton.SetOnClickListener{
//            previousImageButtonPressed()
//        }
        updateDisplayedImage()
        forward.setOnClickListener(this)
        rewind.setOnClickListener(this)
    }

    fun updateDisplayedImage() {
        //shownImage.image = getImageFrom(imageIndex)
        val photoUri: Uri = Uri.fromFile(File(list.get(imageIndex).imageUrl))

        Glide.with(this).load(photoUri).centerCrop().into(imageView);
        if (imageIndex == 0) {
            rewind.isEnabled = false
            rewind.isClickable = false
        } else {
            rewind.isEnabled = true
            rewind.isClickable = true
        }
        if (imageIndex == list.size - 1) {
            forward.isEnabled = false
            forward.isClickable = false
        } else {
            forward.isEnabled = true
            forward.isClickable = true
        }

    }

    fun nextImageButtonPressed() {

        imageIndex = imageIndex + 1
        updateDisplayedImage()
    }

    fun previousImageButtonPressed() {
        imageIndex = imageIndex - 1
        updateDisplayedImage()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.forward -> {
                nextImageButtonPressed()
            }
            R.id.rewind -> {
                previousImageButtonPressed()
            }
        }
    }


}