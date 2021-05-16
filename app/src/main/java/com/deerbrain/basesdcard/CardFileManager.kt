package com.deerbrain.basesdcard

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import com.deerbrain.basesdcard.utilities.App
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "CardFileManager"

class CardFileManager {
    //this is the file that will communicate directly with the SD card


//    fun getFilesWithPath(path: String) : MutableList<FileItem> {
//
//        var itemsFromSDCard = mutableListOf<FileItem>()
//
//        //Do work to populate itemsFromSDCard
//
//        return itemsFromSDCard
//    }

/*
    var parentDirectory:
*/


    /*  fun getPicsAtPath(path: String) : MutableList<FileItem> {
          //goes through card and builds up an array of FileItems that are only pictures

      }*/

    /*  fun getMoviesAtPath(path: String) : MutableList<FileItem> {
          //goes through card and builds up an array of FileItems that are only Movies

      }*/


    /*  fun receivedDeviceStatusChanged(status: DeviceStatus) {
          //this may take a parameter

          when (status) {
              success -> {
                  //notify status is connected
              }
              errorStatus -> {
                  //reportouterr
              }
              disconnect -> {
                  //report disconnection
              }
          }

      }*/
    fun removeFilesFromPaths(paths: ArrayList<String>): Boolean {
        //remove multiple files from the SD card reader and return true if successful or return some value of success or error
        var result: Boolean = true
        for (i in 0 until paths.size) {
            result = File(paths.get(i)).delete()

        }
        return result
    }
    fun removeFileFromPath(path: String): Boolean {
        //remove single file from SD card Reader
        return File(path).delete()
    }
    /*


     fun removeFileFromPath(path: String) -> Bool {
         //remove single file from SD card Reader
     }*/

    /* fun showImage(isBig: Boolean = false, fileName: String, atDirectory: String) : Image {
         // this will take a file name at a directory and return an image.

         //if isBIG is true return full size image
         //if isBig is false return a rezied Image to CGSize of 60x60

     }*/
    companion object {
        fun checkIsImage(context: Context, uri: Uri?): Boolean {
            val contentResolver = context.contentResolver
            val type = contentResolver.getType(uri!!)
            if (type != null) {
                return type.startsWith("image/")
            } else {
                // try to decode as image (bounds only)
                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeStream(inputStream, null, options)
                        return options.outWidth > 0 && options.outHeight > 0
                    }
                } catch (e: IOException) {
                    // ignore
                } finally {
                    FileUtils.closeQuietly(inputStream)
                }
            }
            // default outcome if image not confirmed
            return false
        }

        fun checkIsVideo(context: Context, uri: String): Boolean {
            if (uri == null || uri.isEmpty())
                return false
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, Uri.fromFile(File(uri)))
                val hasVideo =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)
                val isVideo = "yes" == hasVideo
                return isVideo
            } catch (e: Exception) {
                return false
            }

        }


        fun isDocumentFile(path: String): Boolean {
            val mimeType = URLConnection.guessContentTypeFromName(path)
            return mimeType != null && mimeType.startsWith("document")
        }

        fun getThumbnail(path: String): String? {
            var imageId: Long = -1

            val projection = arrayOf(MediaStore.MediaColumns._ID)
            val selection = MediaStore.MediaColumns.DATA + "=?"
            val selectionArgs = arrayOf<String>(path)
            var cursor: Cursor? = App.context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                imageId = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID)) as Long
                cursor.close()
            }

            var result: String? = null
            cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                App.context.contentResolver,
                imageId,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null
            )
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst()
                result =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA))
                cursor.close()
            }

            return result
        }

        fun getFileExtension(path: String): String {
            val file: Uri = Uri.fromFile(File(path))
            val fileExt = MimeTypeMap.getFileExtensionFromUrl(file.toString())
            return fileExt
        }

        fun getImagesFile(list: MutableList<FileItem>): MutableList<FileItem> {
            var imageList: MutableList<FileItem> = ArrayList()
            for (i in 0 until list.size) {
                if (list.get(i).fileTypes == FileTypes.IMAGE) {
                    imageList.add(list.get(i))
                }
            }
            return imageList
        }

        fun getAllVideosFile(list: MutableList<FileItem>): MutableList<FileItem> {
            var imageList: MutableList<FileItem> = ArrayList()
            for (i in 0 until list.size) {
                if (list.get(i).fileTypes == FileTypes.VIDEO) {
                    imageList.add(list.get(i))
                }
            }
            return imageList
        }

        fun getAllAudioFile(list: MutableList<FileItem>): MutableList<FileItem> {
            var imageList: MutableList<FileItem> = ArrayList()
            for (i in 0 until list.size) {
                if (list.get(i).fileTypes == FileTypes.AUDIO_FILE) {
                    imageList.add(list.get(i))
                }
            }
            return imageList
        }

        fun getSelectedImageIndex(path: String, list: MutableList<FileItem>): Int {
            var index: Int = -1
            for (i in 0 until list.size) {
                if (list.get(i).imageUrl.equals(path)) {
                    return i
                }
            }
            return index
        }


        fun hasSdCard(context: Context): Boolean {
            val dirs: Array<File?> = context.getExternalFilesDirs("")
            if (dirs.size >= 2 && dirs[1] != null) {
                if (Environment.isExternalStorageRemovable(dirs[1]!!)) { // Extra Check

                    return true
                }
            }
            return false
        }

        fun SDPath(): String? {
            var sdcardpath = ""

            //Datas
            if (File("/data/sdext4/").exists() && File("/data/sdext4/").canRead()) {
                sdcardpath = "/data/sdext4/"
            }
            if (File("/data/sdext3/").exists() && File("/data/sdext3/").canRead()) {
                sdcardpath = "/data/sdext3/"
            }
            if (File("/data/sdext2/").exists() && File("/data/sdext2/").canRead()) {
                sdcardpath = "/data/sdext2/"
            }
            if (File("/data/sdext1/").exists() && File("/data/sdext1/").canRead()) {
                sdcardpath = "/data/sdext1/"
            }
            if (File("/data/sdext/").exists() && File("/data/sdext/").canRead()) {
                sdcardpath = "/data/sdext/"
            }

            //MNTS
            if (File("mnt/sdcard/external_sd/").exists() && File("mnt/sdcard/external_sd/").canRead()) {
                sdcardpath = "mnt/sdcard/external_sd/"
            }
            if (File("mnt/extsdcard/").exists() && File("mnt/extsdcard/").canRead()) {
                sdcardpath = "mnt/extsdcard/"
            }
            if (File("mnt/external_sd/").exists() && File("mnt/external_sd/").canRead()) {
                sdcardpath = "mnt/external_sd/"
            }
            if (File("mnt/emmc/").exists() && File("mnt/emmc/").canRead()) {
                sdcardpath = "mnt/emmc/"
            }
            if (File("mnt/sdcard0/").exists() && File("mnt/sdcard0/").canRead()) {
                sdcardpath = "mnt/sdcard0/"
            }
            if (File("mnt/sdcard1/").exists() && File("mnt/sdcard1/").canRead()) {
                sdcardpath = "mnt/sdcard1/"
            }
            if (File("mnt/sdcard/").exists() && File("mnt/sdcard/").canRead()) {
                sdcardpath = "mnt/sdcard/"
            }

            //Storages
            if (File("/storage/removable/sdcard1/").exists() && File("/storage/removable/sdcard1/").canRead()) {
                sdcardpath = "/storage/removable/sdcard1/"
            }
            if (File("/storage/external_SD/").exists() && File("/storage/external_SD/").canRead()) {
                sdcardpath = "/storage/external_SD/"
            }
            if (File("/storage/ext_sd/").exists() && File("/storage/ext_sd/").canRead()) {
                sdcardpath = "/storage/ext_sd/"
            }
            if (File("/storage/sdcard1/").exists() && File("/storage/sdcard1/").canRead()) {
                sdcardpath = "/storage/sdcard1/"
            }
            if (File("/storage/sdcard0/").exists() && File("/storage/sdcard0/").canRead()) {
                sdcardpath = "/storage/sdcard0/"
            }
            if (File("/storage/sdcard/").exists() && File("/storage/sdcard/").canRead()) {
                sdcardpath = "/storage/sdcard/"
            }
            if (sdcardpath.contentEquals("")) {
                sdcardpath = Environment.getExternalStorageDirectory().absolutePath
            }
            Log.v("SDFinder", "Path: $sdcardpath")
            return sdcardpath
        }

        fun getFilesWithPath(
            path: String?,
            context: Context,
            isFirstTime: Boolean
        ): MutableList<FileItem> {
            var itemsFromSDCard = mutableListOf<FileItem>()
            var date = Date()
            var file: File
            val list: Array<File>
            val formatter = SimpleDateFormat("MMM dd yyyy")
            if (isFirstTime) {
                /*  val root_sd: String = Environment.getExternalStorageDirectory().path + "/"*/
                file = File(getSdCardPath(context))
                list = file.listFiles()
            } else {
                file = File(path + "/")
                list = file.listFiles()
            }
            Log.e(TAG, list.size.toString() + "files ize")

            for (i in 0 until list.size) {
                Log.e(TAG, list.get(i).absolutePath)
                var fileItem: FileItem = FileItem("", "", FileTypes.GENERAL_FILE, "", "")
                if (list.get(i).isFile) {
                    if (list.get(i).absolutePath != null && !list.get(i).absolutePath.isEmpty() && list.get(
                            i
                        ).absolutePath.trim().length > 0
                    ) {
                        if (checkIsImage(context, Uri.fromFile(File(list.get(i).absolutePath)))) {
                            fileItem = FileItem(
                                list.get(i).name,
                                getFileData(list.get(i).lastModified().toLong()),
                                FileTypes.IMAGE,
                                list.get(i).absolutePath, ""
                            )
                        } else if (checkIsVideo(
                                context,
                                list.get(i).absolutePath
                            )
                        ) {
                            fileItem = FileItem(
                                list.get(i).name,
                                getFileData(list.get(i).lastModified().toLong()),
                                FileTypes.VIDEO,
                                list.get(i).absolutePath,
                                getThumbnail(list.get(i).absolutePath)

                            )
                        } else if (isDocumentFile(list.get(i).absolutePath)) {
                            when (getFileExtension(list.get(i).absolutePath)) {
                                ".xls" -> {
                                    fileItem = FileItem(
                                        list.get(i).name,
                                        getFileData(list.get(i).lastModified().toLong()),
                                        FileTypes.DOCUMENT_EXCEL,
                                        "", ""
                                    )
                                }
                                ".xlsx" -> {
                                    fileItem = FileItem(
                                        list.get(i).name,
                                        getFileData(list.get(i).lastModified().toLong()),
                                        FileTypes.DOCUMENT_EXCEL,
                                        "", ""
                                    )
                                }
                                "doc" -> {
                                    fileItem = FileItem(
                                        list.get(i).name,
                                        getFileData(list.get(i).lastModified().toLong()),
                                        FileTypes.DOCUMENT_DOC,
                                        "", ""
                                    )
                                }
                                "docx" -> {
                                    fileItem = FileItem(
                                        list.get(i).name,
                                        getFileData(list.get(i).lastModified().toLong()),
                                        FileTypes.DOCUMENT_DOC,
                                        "", ""
                                    )
                                }
                                "ppt" -> {
                                    fileItem = FileItem(
                                        list.get(i).name,
                                        getFileData(list.get(i).lastModified().toLong()),
                                        FileTypes.DOCUMENT_POWERPOINT,
                                        "", ""
                                    )
                                }
                                "pptx" -> {
                                    fileItem = FileItem(
                                        list.get(i).name,
                                        getFileData(list.get(i).lastModified().toLong()),
                                        FileTypes.DOCUMENT_POWERPOINT,
                                        "", ""
                                    )
                                }
                                "pdf" -> {
                                    fileItem = FileItem(
                                        list.get(i).name,
                                        getFileData(list.get(i).lastModified().toLong()),
                                        FileTypes.DOCUMENT_PDF,
                                        "", ""
                                    )
                                }
                            }

                        } else if (isAudioFile(list.get(i).absolutePath)) {
                            fileItem = FileItem(
                                list.get(i).name,
                                getFileData(list.get(i).lastModified().toLong()),
                                FileTypes.AUDIO_FILE,
                                list.get(i).absolutePath, ""
                            )
                        } else {
                            fileItem = FileItem(
                                list.get(i).name,
                                getFileData(list.get(i).lastModified().toLong()),
                                FileTypes.GENERAL_FILE,
                                "", ""
                            )

                        }
                        itemsFromSDCard.add(fileItem)

                    }
                } else if (list.get(i).isDirectory) {
                    fileItem = FileItem(
                        list.get(i).name,
                        getFileData(list.get(i).lastModified().toLong()),
                        FileTypes.FOLDER,
                        list.get(i).absolutePath, ""
                    )
                    itemsFromSDCard.add(fileItem)
                }


            }
            return itemsFromSDCard
        }

        fun getSdCardPath(context: Context): String? {
            var path: String? = null
            val dirs: Array<File> = context.getExternalFilesDirs(null)
            if (dirs != null && Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                path = dirs[dirs.size - 1].absolutePath
                path = path.substring(0, path.indexOf("Android"))
            }
            return path
        }

        fun getFileData(date: Long): String {
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = date
            return DateFormat.format("dd-MM-yyyy ", calendar).toString()
        }

        fun isAudioFile(path: String): Boolean {
            if (path == null || path.isEmpty()) {
                return false
            }
            if (path.contains(".mp3")) {
                return true
            } else {
                return false
            }
        }

        fun getWidth(context: Context): Int {
            val displayMetrics = DisplayMetrics()
            val windowmanager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowmanager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    }


}