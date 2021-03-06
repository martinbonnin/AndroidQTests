package net.mbonnin.androidqwriter

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import android.os.ParcelFileDescriptor.MODE_READ_WRITE
import android.util.Log
import java.io.File
import java.io.FileNotFoundException

class MainContentProvider: ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val file = File(context!!.getExternalFilesDir(null), uri.path)

        if (uri.path == "/log.config"){
            file.createNewFile()
            return ParcelFileDescriptor.open(file, MODE_READ_WRITE)
        } else {
            if (!file.exists()) {
                throw FileNotFoundException()
            }
            return ParcelFileDescriptor.open(file, MODE_READ_ONLY)
        }
    }
}