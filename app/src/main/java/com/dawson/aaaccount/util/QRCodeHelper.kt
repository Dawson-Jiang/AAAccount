package com.dawson.aaaccount.util

import java.util.AbstractMap
import java.util.HashMap

import android.graphics.Bitmap

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel


object QRCodeHelper {
    private val BLACK = -0x1000000

    fun getQRCode(uri: String, QR_WIDTH: Int, QR_HEIGHT: Int): Bitmap? {

        try {
            val hints = HashMap<EncodeHintType, Any>()
            hints.put(com.google.zxing.EncodeHintType.CHARACTER_SET, "utf-8")
            hints.put(com.google.zxing.EncodeHintType.ERROR_CORRECTION,
                    ErrorCorrectionLevel.H)
            val matrix = QRCodeWriter().encode(uri,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints)
            val w = matrix.width
            val h = matrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (matrix.get(x, y)) {
                        pixels[y * w + x] = BLACK
                    }
                }
            }
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bmp.setPixels(pixels, 0, w, 0, 0, w, h)
            return bmp
            // return null;
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }

    }
}
