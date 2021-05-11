package com.deerbrain.basesdcard

enum class FileTypes {
    FOLDER(0),
    IMAGE(1),
    VIDEO(2),
    DOCUMENT_EXCEL(3),
    DOCUMENT_DOC(4),
    DOCUMENT_POWERPOINT(5),
    DOCUMENT_PDF(6),
    GENERAL_FILE(7),
    AUDIO_FILE(8);

    var field: Int

    constructor(field: Int) {
        this.field = field
    }

}