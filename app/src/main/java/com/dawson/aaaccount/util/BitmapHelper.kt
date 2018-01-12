package com.dawson.aaaccount.util

import java.io.ByteArrayOutputStream

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Bitmap.CompressFormat
import android.media.ThumbnailUtils
import java.io.FileOutputStream
import java.io.IOException

object BitmapHelper {
    /**
     * 压缩的图片
     */
    fun compressPiture(arg0: ByteArray?): ByteArray? {
        if (arg0 == null)
            return null
        var bmp: Bitmap? = BitmapFactory.decodeByteArray(arg0, 0, arg0.size)
        if (bmp != null) {
            // 旋转
            val max = Matrix()
            max.postRotate(90.0f)
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width,
                    bmp.height, max, false)
            // 压缩
            bmp = ThumbnailUtils.extractThumbnail(bmp, bmp!!.width / 2,
                    bmp.height / 2)
            val baos = ByteArrayOutputStream()
            bmp!!.compress(CompressFormat.JPEG, 80, baos)
            return baos.toByteArray()
        } else
            return null
    }


    /**
     * 质量压缩方法
     *
     * @param filename
     * @return
     */
    fun compressImage(filename: String): String {
        val image = BitmapFactory.decodeFile(filename)
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 90

        while (baos.toByteArray().size / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
            baos.reset() // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10// 每次都减少10
        }
        try {
            val fn = filename.substring(filename.lastIndexOf("/") + 1)
            val tmpPath = FilePathConstants.tmpPhotosPath + "/" + fn
            val fos = FileOutputStream(tmpPath)
            fos.write(baos.toByteArray())
            fos.close()
            baos.close()
            return tmpPath
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
    }

    /**
     * 图片按比例大小压缩 压缩好比例大小后再进行质量压缩
     *
     * @param filename （根据路径获取图片并压缩）
     * @return
     */
    fun compressImageTwo(filename: String): String {
        val newOpts = BitmapFactory.Options()
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
//        newOpts.inJustDecodeBounds = true
        var bitmap = BitmapFactory.decodeFile(filename, newOpts)// 此时返回bm为空
        val size = 2048
        if (newOpts.outWidth > size || newOpts.outHeight > size) {
            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            var be = (if (w > h) w else h) / size
            if (be <= 0) be = 1
            newOpts.inSampleSize = be// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(filename, newOpts)
        }

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 90

        while (baos.toByteArray().size / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
            baos.reset() // 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10// 每次都减少10
        }
        try {
            val fn = filename.substring(filename.lastIndexOf("/") + 1)
            val tmpPath = FilePathConstants.tmpPhotosPath + "/" + fn
            val fos = FileOutputStream(tmpPath)
            fos.write(baos.toByteArray())
            fos.close()
            baos.close()
            bitmap.recycle()
            return tmpPath
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
    }
}
