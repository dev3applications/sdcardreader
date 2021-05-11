package com.deerbrain.basesdcard

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.Resource
import com.google.android.exoplayer2.PlaybackParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_movie_view.*
import kotlinx.android.synthetic.main.image_viewer.*
import java.lang.reflect.Type

private const val TAG = "MovieViewActivity"

class MovieViewActivity : AppCompatActivity(), View.OnClickListener {
    var list = mutableListOf<FileItem>()
    lateinit var selectedItemUrl: String
    var gson = Gson()
    var imageIndex: Int = 0
    lateinit var rewind: ImageView
    lateinit var next: ImageView
    lateinit var speed: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_view)
        val imagesList: Type = object : TypeToken<List<FileItem?>?>() {}.getType()
        val imgList: ArrayList<FileItem> =
            gson.fromJson(intent.getStringExtra("videoList"), imagesList)
        list.addAll(imgList)
        AppLogger.errorMessage(TAG, list.size.toString())
        rewind = findViewById(R.id.back)
        next = findViewById(R.id.next)
        speed = findViewById(R.id.speed)


        selectedItemUrl = intent.getStringExtra("selectedImage").toString()
        imageIndex = CardFileManager.getSelectedImageIndex(selectedItemUrl, list)
        MediaPlayer.initialize(applicationContext)

        MediaPlayer.exoPlayer?.preparePlayer(playerView, true)
        playVideoFile()
        rewind.setOnClickListener(this)
        next.setOnClickListener(this)
        speed.setOnClickListener(this)

        /* findViewById<Button>(R.id.playButton).setOnClickListener({
             var parama = PlaybackParameters(2f)
             MediaPlayer.exoPlayer?.setPlaybackParameters(parama)
         })*/
    }

    public override fun onPause() {
        super.onPause()
        MediaPlayer.pausePlayer()
    }

    public override fun onDestroy() {
        MediaPlayer.stopPlayer()
        super.onDestroy()
    }

    fun playVideoFile() {
        if (imageIndex == 0) {
            rewind.isEnabled = false
            rewind.isClickable = false
            rewind.alpha = 0.5f
        } else {
            rewind.isEnabled = true
            rewind.isClickable = true
            rewind.alpha = 1.0f
        }
        if (imageIndex == list.size - 1) {
            next.isEnabled = false
            next.isClickable = false
            next.alpha = 0.5f
        } else {
            next.isEnabled = true
            next.isClickable = true
            next.alpha = 1.0f
        }
        var url: String = list.get(imageIndex).imageUrl.toString()
        MediaPlayer.exoPlayer?.setSource(
            applicationContext,
            url
        )
        var parama = PlaybackParameters(1f)
        MediaPlayer.exoPlayer?.setPlaybackParameters(parama)
        MediaPlayer.startPlayer()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.next -> {
                imageIndex = imageIndex + 1
                playVideoFile()
            }
            R.id.back -> {
                imageIndex = imageIndex - 1
                playVideoFile()
            }
            R.id.speed -> {
                MediaPlayer.exoPlayer?.playWhenReady=false
                val dialog = Dialog(this)
                dialog.setCancelable(true)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                dialog.setContentView(R.layout.video_speed_dialog)
                dialog.window?.setLayout(
                    CardFileManager.getWidth(this) - 30,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                var speed0_5: TextView = dialog.findViewById<TextView>(R.id.speed0_5)
                var speed0_75: TextView = dialog.findViewById<TextView>(R.id.speed0_75)
                var speed1: TextView = dialog.findViewById<TextView>(R.id.speed1)
                var speed1_5: TextView = dialog.findViewById<TextView>(R.id.speed1_5)
                var speed2: TextView = dialog.findViewById<TextView>(R.id.speed2)
                dialog.setOnDismissListener({
                    MediaPlayer.exoPlayer?.playWhenReady=true
                })
                speed0_5.setOnClickListener({
                    dialog.dismiss()
                    var parama = PlaybackParameters(0.5f)
                    MediaPlayer.exoPlayer?.setPlaybackParameters(parama)
                })
                speed0_75.setOnClickListener({

                    dialog.dismiss()
                    var parama = PlaybackParameters(0.75f)
                    MediaPlayer.exoPlayer?.setPlaybackParameters(parama)

                })

                speed1.setOnClickListener({
                    dialog.dismiss()
                    var parama = PlaybackParameters(1f)
                    MediaPlayer.exoPlayer?.setPlaybackParameters(parama)

                })
                speed1_5.setOnClickListener({
                    dialog.dismiss()
                    var parama = PlaybackParameters(1.5f)
                    MediaPlayer.exoPlayer?.setPlaybackParameters(parama)
                })
                speed2.setOnClickListener({
                    dialog.dismiss()
                    var parama = PlaybackParameters(2f)
                    MediaPlayer.exoPlayer?.setPlaybackParameters(parama)

                })


                dialog.show()
            }
        }
    }
}