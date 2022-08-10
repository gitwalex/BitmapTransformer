@file:Suppress("unused")

package com.gerwalex.bitmaptransformer

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.WindowMetrics
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

//This class takes care of getting blur of images in android. The image can be a drawable, bitmap, or a id of the drawable,
//Helpful in changing saturation, brightness and contrast of bitmaps.
//Can convert drawable to bitmaps and vice-versa.
//some methods in this class are taken from Stack Overflow and combined.
object BitmapExt {

    @JvmStatic
    fun Bitmap.bitmapToDrawable(context: Context): Drawable {
        return BitmapDrawable(context.resources, this)
    }

    //hey there s a big and important difference between contrast and saturation
    @JvmStatic
    fun Bitmap.changeBitmapContrastBrightness(contrast: Float, brightness: Float): Bitmap {
        /*   @param bmp input bitmap
         * @param contrast 0..10 1 is default
         * @param brightness -255..255 0 is default
         * @return new bitmap  */
        val cm = ColorMatrix(floatArrayOf(contrast,
            0f,
            0f,
            0f,
            brightness,
            0f,
            contrast,
            0f,
            0f,
            brightness,
            0f,
            0f,
            contrast,
            0f,
            brightness,
            0f,
            0f,
            0f,
            1f,
            0f))
        val ret = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(this, 0f, 0f, paint)
        return ret
    }

    @JvmStatic
    fun Bitmap.changeSaturation(context: Context): Bitmap {
        //  http://www.steves-digicams.com/knowledge-center/brightness-contrast-saturation-and-sharpness.html#b
        // http://stackoverflow.com/questions/25453310/animate-images-saturation
        // http://stackoverflow.com/questions/4354939/understanding-the-use-of-colormatrix-and-colormatrixcolorfilter-to-modify-a-draw
        val cm = ColorMatrix()
        cm.setSaturation(0.6f) //checkitformanyvalueswhenfreechoosethebest
        val drawable = bitmapToDrawable(context)
        drawable.colorFilter = colorFilterFromColorMatrix(cm, 1.32f)
        return drawable.convertToBmpFromDrawable()
    }

    private fun colorFilterFromColorMatrix(cm: ColorMatrix, x: Float): ColorFilter {
        return ColorMatrixColorFilter(cm)
    }

    @JvmStatic
    fun Bitmap.convertBitmaptoFile(context: Context, fileName: String): File {
        val f = File(context.cacheDir, fileName)
        try {
            f.createNewFile()
            val bos = ByteArrayOutputStream()
            compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
            val bitmapdata = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            //handling io exceptions is must in android.
        } catch (e: IOException) {
            Log.d("ankit", e.message!!)
        }
        return f
    }

    fun convertToBmpfromResourceDrawable(context: Context, drawableId: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, drawableId)
    }

    @JvmStatic
    fun Drawable.convertToBmpFromDrawable(): Bitmap {
        if (this is BitmapDrawable) {
            val bitmapDrawable = this
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1,
                Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(intrinsicWidth, intrinsicHeight,
                Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }

    //  * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
    @JvmStatic
    fun Bitmap.fastblur(radius: Int): Bitmap {
        var myRadius = radius
        val bitmap = copy(config, true)
        if (myRadius < 1) {
            myRadius = 20
        }
        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = myRadius + myRadius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        val vmin = IntArray(Math.max(w, h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }
        yi = 0
        var yw: Int = yi
        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = myRadius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int
        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -myRadius
            while (i <= myRadius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + myRadius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = myRadius
            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - myRadius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (y == 0) {
                    vmin[x] = Math.min(x + myRadius + 1, wm)
                }
                p = pix[yw + vmin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -myRadius * w
            i = -myRadius
            while (i <= myRadius) {
                yi = Math.max(0, yp) + x
                sir = stack[i + myRadius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(i)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = myRadius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - myRadius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    @JvmStatic
    fun Bitmap.getResizedBitmap(newHeight: Int, newWidth: Int): Bitmap {
        return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    }

    @Suppress("DEPRECATION")
    fun getScreenWidth(activity: Activity): Int {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val metrics: WindowMetrics = activity.getSystemService(WindowManager::class.java).currentWindowMetrics
            metrics.bounds.width()
        } else {
            val outMetrics = DisplayMetrics()
            val display = activity.windowManager.defaultDisplay
            display.getMetrics(outMetrics)
            outMetrics.widthPixels
        }
    }

    @JvmStatic
    fun Bitmap.overlapBitmapOverItsBlurredBackgroundWithFitCenterScale(
        backgroundWidth: Int, backgroundHeight: Int,
    ): Bitmap {
        var source = this
        var sourceHeight = source.height
        var sourceWidth = source.width
        /*
          Let c be the multiplier which should be multiplied with sourceHeight and sourceWidth to make fitCenter with backround
          then sourceWidth*c<=backgroundWidth and sourceHeight*c <= backgroundHeight then only they can fit into background
          it means that c should be min of backgroundWidth/sourceWidth and backgroundHeight/sourceHeight;
         */
        val c = Math.min(backgroundHeight.toFloat() / sourceHeight, backgroundWidth.toFloat() / sourceWidth)
        sourceWidth = Math.round(sourceWidth * c)
        sourceHeight = Math.round(sourceHeight * c)
        source = this.getResizedBitmap(sourceHeight, sourceWidth)
        val marginTop = (backgroundHeight - sourceHeight) / 2
        val marginLeft = (backgroundWidth - sourceWidth) / 2
        val blurBackround = this
            .getResizedBitmap(backgroundHeight, backgroundWidth)
            .apply {
                fastblur(10)
                changeBitmapContrastBrightness(0.9f, -25f)
            }
        Log.d("ankit", "$sourceHeight in BlurClass $sourceWidth")
        val bitmapOverlay = Bitmap.createBitmap(backgroundWidth, backgroundHeight, blurBackround.config)
        val canvas = Canvas(bitmapOverlay)
        canvas.drawBitmap(blurBackround, Matrix(), null)
        canvas.drawBitmap(source, marginLeft.toFloat(), marginTop.toFloat(), null)
        return bitmapOverlay
    }

    @JvmStatic
    fun Bitmap.reduceBmpSize(): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 8
        //search how to resize bmp using inSampleSize
        return Bitmap.createScaledBitmap(this, width / 4, height / 4, true)
    }
}