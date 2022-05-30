package com.gerwalex.bitmaptransformer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.*
import java.io.File

object ExifUtil {

    /**
     * @see http://sylvana.net/jpegcrop/exif_orientation.html
     */
    fun rotateBitmap(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        check(inputStream != null) {
        }
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL)
        return rotate(orientation, bitmap)
    }

    fun rotateBitmap(file: File, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(file)
        val orientation = exif.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL)
        return rotate(orientation, bitmap)
    }

    private fun rotate(orientation: Int, bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ORIENTATION_NORMAL -> {
                return bitmap
            }//ok
            ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            else -> return bitmap
        }
        return try {
            val oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            oriented
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap
        }
    }
}