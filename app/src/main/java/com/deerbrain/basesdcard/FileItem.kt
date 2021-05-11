package com.deerbrain.basesdcard

import java.io.Serializable
import java.util.*

class FileItem(
    val fileName: String,

    val fileDateString: String,
    var fileTypes: FileTypes,
    var imageUrl: String?,
    var thumbnail:String?

) : Serializable