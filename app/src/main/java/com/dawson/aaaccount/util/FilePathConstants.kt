package com.dawson.aaaccount.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.dawson.aaaccount.AAAccountApplication
import java.io.File
import java.io.FileWriter
import java.io.IOException

object FilePathConstants {

    private var AA_ACCOUNT_PATH: String? = null
    private var PHOTO_IMG_PATH: String? = null
    private var VIDEO_PATH: String? = null
    private var USER_ACTION_PATH: String? = null
    private var EXCEPTION_PATH: String? = null
    private var CACHE_PATH: String? = null
    private var hasInit: Boolean = false

    private fun initPath() {
        if (hasInit) return
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            AA_ACCOUNT_PATH = AAAccountApplication.aplication.getExternalFilesDir(null)!!.absolutePath
            CACHE_PATH = AAAccountApplication.aplication.externalCacheDir!!.absolutePath
        } else {
            AA_ACCOUNT_PATH = AAAccountApplication.aplication.filesDir.absolutePath
            CACHE_PATH = AAAccountApplication.aplication.cacheDir.absolutePath
        }
        PHOTO_IMG_PATH = AA_ACCOUNT_PATH + File.separator + Environment.DIRECTORY_PICTURES
        VIDEO_PATH = AA_ACCOUNT_PATH + File.separator + Environment.DIRECTORY_MOVIES
        USER_ACTION_PATH = AA_ACCOUNT_PATH + File.separator + "actions"
        EXCEPTION_PATH = AA_ACCOUNT_PATH + File.separator + "exception"
        hasInit = true
    }

    private fun getPath(pathStr: String?): String {
        val path = File(pathStr)
        if (!path.exists()) {
            path.mkdirs()
        }
        return path.absolutePath
    }

    val avatarPhotosPath: String
        get() {
            initPath()
            return getPath(PHOTO_IMG_PATH + File.separator + "avater")
        }


    val userActionLogPath: String
        get() {
            initPath()
            return getPath(USER_ACTION_PATH)
        }

    val exceptionLogPath: String
        get() {
            initPath()
            return getPath(EXCEPTION_PATH)
        }

    // 获取临时图片的路径
    val tmpPhotosPath: String
        get() {
            initPath()
            return getPath(CACHE_PATH)
        }


    /**
     * Try to return the absolute file path from the given Uri

     * @param context
     * *
     * @param uri
     * *
     * @return the file path or null
     */
    fun getRealFilePath(context: Context, uri: Uri): String? {
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null)
            data = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }
}
