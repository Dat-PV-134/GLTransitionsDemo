package com.rekoj134.gltransitionsdemo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.IOException
import java.io.InputStream

object AssetsUtil {
    fun getImageBitmap(context: Context, filePath: String) : Bitmap? {
        val assetManager = context.assets
        val inputStream: InputStream
        var bitmap: Bitmap? = null
        try {
            inputStream = assetManager.open(filePath)
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun getImageBitmap(context: Context, filePath: String, flipHorizontal: Boolean = false, flipVertical: Boolean = false): Bitmap? {
        val assetManager = context.assets
        val inputStream: InputStream
        var bitmap: Bitmap? = null
        try {
            inputStream = assetManager.open(filePath)
            bitmap = BitmapFactory.decodeStream(inputStream)

            val matrix = Matrix()

            if (flipHorizontal) {
                matrix.preScale(-1f, 1f)
            }
            if (flipVertical) {
                matrix.preScale(1f, -1f)
            }

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }
}