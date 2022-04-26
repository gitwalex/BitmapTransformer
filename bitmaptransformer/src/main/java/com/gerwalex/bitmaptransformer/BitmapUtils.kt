package com.gerwalex.bitmaptransformer

import android.content.Context
import android.util.DisplayMetrics

/**
 * Copyright (C) 2020 Wasabeef
 * updated 2022 gerwalex
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object BitmapUtils {
    @JvmStatic
    fun toDp(context: Context, dp: Float): Int {
        val scale = context
            .resources
            .displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    @JvmStatic
    fun convertDpToPixel(context: Context, dp: Float): Float {
        return dp * (context
            .resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    @JvmStatic
    fun convertPixelsToDp(context: Context, px: Float): Float {
        return px / (context
            .resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}