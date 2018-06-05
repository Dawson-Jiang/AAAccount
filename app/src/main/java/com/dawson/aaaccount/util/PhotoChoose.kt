package com.dawson.aaaccount.util

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.Toast
import com.dawson.aaaccount.BuildConfig
import com.dawson.aaaccount.R
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException

/**
 * 选择图片
 * Created by dawson on 2017/5/3.
 */
class PhotoChoose {
    private var mActivity: Activity
    private var mFragment: Fragment? = null

    var imageUri: Uri? = null
        private set

    // 头像宽度和高度
    private val Avatar_Width = 640
    private val Avatar_Height = 640
    private val out_putx = Avatar_Width
    private val out_puty = Avatar_Height

    constructor(activity: Activity) {
        mActivity = activity
    }

    constructor(fragment: Fragment) {
        mFragment = fragment
        mActivity = mFragment?.activity!!
    }

    private fun startActivityForResult(intent: Intent, requestCode: Int) {
        if (mFragment != null)
            mFragment?.startActivityForResult(intent, requestCode)
        else mActivity.startActivityForResult(intent, requestCode)
    }

    fun start() {
        AlertDialogHelper.showPhotoChooseDialog(mActivity) { _, which ->
            if (which == 1) {
                camera()
            } else if (which == 2) {
                picture()
            }
        }
    }

    /**
     * 选择图片
     */
    private fun picture() {
        if (!Common.checkPermissions(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) || !Common.checkPermissions(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialogHelper.showOKCancelAlertDialog(mActivity, R.string.permission_warning_storage,
                    { _, _ -> Common.startAppSettings(mActivity) }, { _, _ -> })
            return
        }
        imageUri = Uri.parse("file://"
                + FilePathConstants.avatarPhotosPath
                + File.separator + System.currentTimeMillis().toString()
                + ".jpg")
        try {
            val intent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, OperateCode.SELECT_PICTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(mActivity, "没有相册", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 拍照
     */
    private fun camera() {
        val fn = FilePathConstants.avatarPhotosPath + File.separator + System.currentTimeMillis().toString() + ".jpg"
        imageUri = Uri.parse("file://" + fn)

        if (Common.checkPermissions(mActivity, Manifest.permission.CAMERA)) {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val pof = File(fn)
                    val imguri = FileProvider.getUriForFile(mActivity,
                            mActivity.packageName + ".fileprovider", pof)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imguri)
                }else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                }
                startActivityForResult(intent, OperateCode.CAPTURE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(mActivity, "没有找到相机", Toast.LENGTH_LONG).show()
            }
        } else {
            AlertDialogHelper.showOKCancelAlertDialog(mActivity, R.string.permission_warning_camara,
                    { _, _ -> Common.startAppSettings(mActivity) }, { _, _ -> })
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Observable<Uri> {
        Log.i("dawson", "action.CROP result:" + resultCode)
        return Observable.create<Uri> { e ->
            if (resultCode != Activity.RESULT_OK) {
                e.onComplete()
                return@create
            }
            if (requestCode == OperateCode.SELECT_PICTURE)
                imageUri = data?.data
            if (imageUri == null) {
                e.onError(Exception("操作出错"))
                return@create
            }
            e.onNext(imageUri!!)
            e.onComplete()
        }.subscribeOn(Schedulers.computation())
    }


    @Throws(IOException::class)
    private fun revitionImageSize(context: Context, uri: Uri,
                                  sizeLimit: Int?): Bitmap? {
        var limit = 1024
        if (sizeLimit != null) {
            limit = sizeLimit.toInt()
        }
        var instream = BufferedInputStream(context
                .contentResolver.openInputStream(uri)!!)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(instream, null, options)
        instream.close()
        var i = 0
        val bitmap: Bitmap?
        while (true) {
            if (options.outWidth shr i <= limit && options.outHeight shr i <= limit) {
                instream = BufferedInputStream(context.contentResolver
                        .openInputStream(uri)!!)
                options.inSampleSize = Math.pow(2.0, i.toDouble()).toInt()
                options.inJustDecodeBounds = false
                options.inPreferredConfig = Bitmap.Config.RGB_565
                bitmap = BitmapFactory.decodeStream(instream, null, options)
                break
            }
            i += 1
        }
        return bitmap
    }
}
