package com.gerwalex.bitmaptransformer

import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import androidx.annotation.DrawableRes
import java.io.*
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * An image resizing library for Android, which allows you to scale an image file to a smaller or bigger one while keeping the aspect ratio.
 * Created by K.K. Ho on 1/9/2017.
 */
@Suppress("unused")
class BitmapTransformer {

    constructor(sourceImage: File) {
        inputStream = FileInputStream(sourceImage)
    }

    constructor(context: Context, sourceUri: Uri) {
        val inStream = context.contentResolver.openInputStream(sourceUri)
        require(inStream != null) { "No valid Uri for File" }
        inputStream = inStream
    }

    constructor(context: Context, @DrawableRes sourceDrawableRes: Int) {
        val uri = Uri
            .Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(context.resources.getResourcePackageName(sourceDrawableRes))
            .appendPath(context.resources.getResourceTypeName(sourceDrawableRes))
            .appendPath(context.resources.getResourceEntryName(sourceDrawableRes))
            .build()
        val inStream = context.contentResolver.openInputStream(uri)
        require(inStream != null) { "No valid Drawable" }
        inputStream = inStream
    }

    private var outFilename: String = java.lang.Long.toHexString(System.currentTimeMillis())
    private var outDir: String? = null
    private val inputStream: InputStream
    private val FULLOPACITY: Int = 255
    private val NOROTATION: Float = 0f
    private var compressFormat: CompressFormat = CompressFormat.PNG
    private var targetLength = 1080
    private var quality = 100
    private var opacity: Int = FULLOPACITY
    private var rotateDegrees: Float = NOROTATION
    private val transformations: ArrayList<Transformation> = ArrayList()
    fun addTransformation(transformation: Transformation): BitmapTransformer {
        transformations.add(transformation)

        return this
    }

    fun rotate(degrees: Float): BitmapTransformer {
        this.rotateDegrees = degrees
        return this
    }

    fun setOpacity(opacity: Int): BitmapTransformer {
        this.opacity = opacity
        return this
    }

    /**
     * Get the resized image bitmap.
     *
     * @return The resized image bitmap.
     * @throws IOException on IO-Error
     */
    fun scaleBitmap(): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        check(bitmap != null) { "Could not decode Inputstream $inputStream" }
        return scaleBitmap(targetLength, bitmap, options)
    }

    /**
     * @param opacity a value between 0 (completely transparent) and 255 (completely
     * opaque).
     * @return The opacity-adjusted bitmap.  If the source bitmap is mutable it will be
     * adjusted and returned, otherwise a new bitmap is created.
     */
    fun Bitmap.adjustOpacity(opacity: Int): Bitmap {
        val bmp = copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bmp)
        val colour = opacity and 0xFF shl 24
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN)
        recycle()
        return bmp
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        val bmp = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        recycle()
        return bmp
    }

    /**
     * Get the resized image file.
     *
     * @return The resized image file or null
     * @throws IOException on IO-Error
     */
    @Throws(IOException::class)
    fun scaleBitmapToFile(outFile: File): File {
        scaleBitmap()
            .let {
                writeBitmapToFile(it, compressFormat, quality, outFile)
                return outFile
            }
    }

    fun scaleBitmapToFile(): File {
        check(outDir != null)
        val outFileName = outFilename + "." + compressFormat.name.lowercase()
        val outFile = File(outDir, outFileName)
        return scaleBitmapToFile(outFile)
    }

    fun outDir(dir: File): BitmapTransformer {
        check(dir.exists() && dir.isDirectory) { "File must exists and be Directory" }
        outDir = dir.absolutePath
        return this
    }

    /**
     * Set the output file name. If you don't set it, the output file will have the same name as the source file.
     *
     * @param filename The name of the output file, without file extension.
     * @return This Resizer instance, for chained settings.
     */
    fun outFilename(filename: String): BitmapTransformer {
        check(
            !(filename
                .lowercase(Locale.US)
                .endsWith(".jpg") || filename
                .lowercase(Locale.US)
                .endsWith(".jpeg") || filename
                .lowercase(Locale.US)
                .endsWith(".png") || filename
                .lowercase(Locale.US)
                .endsWith(".webp"))
        ) { "Filename should be provided without extension." }
        outFilename = filename
        return this
    }

    /**
     * Set the image compression format by Bitmap.CompressFormat.
     *
     * @param compressFormat The compression format. The default format is JPEG.
     * @return This Resizer instance, for chained settings.
     */
    fun compressFormat(compressFormat: CompressFormat): BitmapTransformer {
        this.compressFormat = compressFormat
        return this
    }

    /**
     * Set the image quality. The higher value, the better image quality but larger file size. PNG, which is a lossless format, will ignore the quality setting.
     *
     * @param quality The image quality value, ranges from 0 to 100. The default value is 80.
     * @return This Resizer instance, for chained settings.
     */
    fun quality(quality: Int): BitmapTransformer {
        if (quality < 0) {
            this.quality = 0
        } else {
            this.quality = min(quality, 100)
        }
        return this
    }

    /**
     * Set the target length of the image. You only need to specify the target length of the longer side (or either side if it's a square). Resizer will calculate the rest automatically.
     *
     * @param targetLength The target image length in pixel. The default value is 1080.
     * @return This Resizer instance, for chained settings.
     */
    fun targetLength(targetLength: Int): BitmapTransformer {
        this.targetLength = max(targetLength, 0)
        return this
    }

    private fun scaleBitmap(targetLength: Int, bitmap: Bitmap, options: BitmapFactory.Options): Bitmap {
        // Get the dimensions of the original bitmap
        val originalWidth = options.outWidth
        val originalHeight = options.outHeight
        var aspectRatio = originalWidth.toFloat() / originalHeight
        // Calculate the target dimensions
        val targetWidth: Int
        val targetHeight: Int
        if (originalWidth > originalHeight) {
            targetWidth = targetLength
            targetHeight = (targetWidth / aspectRatio).roundToInt()
        } else {
            aspectRatio = 1 / aspectRatio
            targetHeight = targetLength
            targetWidth = (targetHeight / aspectRatio).roundToInt()
        }
        var bmp = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        if (rotateDegrees != NOROTATION) {
            bmp = bmp.rotate(rotateDegrees)
        }
        if (opacity != FULLOPACITY) {
            bmp = bmp.adjustOpacity(opacity)
        }
        transformations.forEach {
            bmp = it.transform(bmp)
        }
        return bmp
    }

    private fun getOutputFilePath(
        compressFormat: CompressFormat, outputDirPath: String,
        outFilename: String,
    ): String {
        val targetFileName: String
        val targetFileExtension = "." + compressFormat
            .name
            .lowercase(Locale.US)
            .replace("jpeg", "jpg")
        targetFileName = outFilename + targetFileExtension
        return outputDirPath + File.separator + targetFileName
    }

    @Throws(IOException::class)
    fun writeBitmapToFile(bitmap: Bitmap, compressFormat: CompressFormat, quality: Int, file: File) {
        val directory = file.parentFile
        if (directory != null && !directory.exists()) {
            directory.mkdirs()
        }
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(compressFormat, quality, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }
}
