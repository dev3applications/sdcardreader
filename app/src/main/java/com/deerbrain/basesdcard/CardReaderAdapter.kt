package com.deerbrain.basesdcard

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import java.io.File


private const val TAG = "CardReaderAdapter"

class CardReaderAdapter(val context: Context) : RecyclerView.Adapter<FileCell>() {

    var displayItems = mutableListOf<FileItem>()

    var onClickListener: View.OnClickListener? = null
    var gson = Gson()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileCell {
        val termView =
            LayoutInflater.from(parent.context).inflate(R.layout.file_tableviewcell, parent, false)
        return FileCell(termView)
    }

    override fun getItemCount(): Int {
        return displayItems.size
    }

    override fun onBindViewHolder(holder: FileCell, position: Int) {
        val item = displayItems[position]
        holder.fileLabel.text = item.fileName
        holder.fileDate.text = item.fileDateString
        when (item.fileTypes) {
            FileTypes.FOLDER -> {
                holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(R.drawable.ic_folder))
            }
            FileTypes.IMAGE -> {
                val photoUri: Uri = Uri.fromFile(File(item.imageUrl))
                Glide.with(holder.imageView.context).load(photoUri).centerCrop()
                    .into(holder.imageView);
            }
            FileTypes.VIDEO -> {
                if (item.imageUrl != null) {
                    val photoUri: Uri = Uri.fromFile(File(item.imageUrl))
                    Glide.with(holder.imageView.context).load(photoUri)
                        .into(holder.imageView);
                }
            }
            FileTypes.AUDIO_FILE -> {
                holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(R.drawable.audio_file))
            }
            FileTypes.DOCUMENT_PDF -> {
                holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(R.drawable.ic_pdf))

            }
            FileTypes.DOCUMENT_DOC -> {
                holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(R.drawable.ic_word))

            }
            FileTypes.DOCUMENT_POWERPOINT -> {
                holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(R.drawable.ic_ppt))

            }
            FileTypes.DOCUMENT_EXCEL -> {
                holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(R.drawable.ic_excel))

            }
            FileTypes.GENERAL_FILE -> {
                holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(R.drawable.ic_folder))
            }
        }

        //holder.fileImage.image = item.fileImage


        holder.root.setOnClickListener {
            didSelectItem(item)
        }
    }


    fun didSelectItem(item: FileItem) {
        Log.i("Realm", "didSelectItem")
        val fileExtensionType = item.fileName //gets extension of file name like jpg/mov etc
        when (item.fileTypes) {
            FileTypes.FOLDER -> {
                var intent = Intent(context, CardReader::class.java)
                AppLogger.errorMessage(TAG, item.imageUrl.toString())
                intent.putExtra("innerList", item.imageUrl)
                context.startActivity(intent)
            }
            FileTypes.DOCUMENT_EXCEL -> {
                openFile(item.imageUrl,"application/vnd.ms-excel")
            }
            FileTypes.DOCUMENT_DOC->{
                openFile(item.imageUrl,"application/msword")
            }
            FileTypes.DOCUMENT_POWERPOINT->{
                openFile(item.imageUrl,"application/vnd.ms-powerpoint")
            }
            FileTypes.DOCUMENT_PDF->{
                openFile(item.imageUrl,"application/pdf")
            }

            FileTypes.IMAGE -> {
                val intent = Intent(context, ImageViewer::class.java)
                intent.putExtra(
                    "imageList",
                    gson.toJson(CardFileManager.getImagesFile(displayItems))
                )
                intent.putExtra("selectedImage", item.imageUrl)
                context.startActivity(intent)
            }
            FileTypes.VIDEO -> {
                val intent = Intent(context, MovieViewActivity::class.java)
                intent.putExtra(
                    "videoList",
                    gson.toJson(CardFileManager.getAllVideosFile(displayItems))
                )
                intent.putExtra("selectedImage", item.imageUrl)
                context.startActivity(intent)
            }
            FileTypes.GENERAL_FILE -> {
                Toast.makeText(context, "not compatible file", Toast.LENGTH_SHORT).show()
            }
            FileTypes.AUDIO_FILE -> {
                val intent = Intent(context, MovieViewActivity::class.java)
                intent.putExtra(
                    "videoList",
                    gson.toJson(CardFileManager.getAllAudioFile(displayItems))
                )
                intent.putExtra("selectedImage", item.imageUrl)
                context.startActivity(intent)
            }
        }

//        when (fileExtensionType) {
//            "JPG" -> {
//                val intent = Intent(this, ImageViewer::class.java)
//                startActivity(intent)
//            }
//            "No File Extension" -> {
//                //if this is the case then user is opening a folder in the explorer
//                val intent = Intent(this, CardReader::class.java)
//                //need to pass in the path of this folder that you opening
//                startActivity(intent)
//            }
//            "MOV" -> {
////                val intent = Intent(this, MovieViewer::class.java)
////                startActivity(intent)
//            }
//            "anything Else" -> {
//               // Toast.makeText(this,"Currently not available", Toast.LENGTH_SHORT).show()
//            }
//
//        }
    }

    fun update(displayFileItems: MutableList<FileItem>) {
        this.displayItems.clear()
        this.displayItems = displayFileItems
        notifyDataSetChanged()
    }
    fun openFile(path:String?,filetype:String){
        val path = Uri.fromFile(File(path))
        val excelIntent = Intent(Intent.ACTION_VIEW)
        excelIntent.setDataAndType(path, "application/vnd.ms-excel")
        excelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            context.startActivity(excelIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No Application available to viewExcel",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}