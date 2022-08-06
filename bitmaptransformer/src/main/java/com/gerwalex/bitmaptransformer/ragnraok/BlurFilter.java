package com.gerwalex.bitmaptransformer.ragnraok;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class BlurFilter implements Transformation {
    static {
        System.loadLibrary("AndroidImageFilter");
    }

    private final int maskSize;

    public BlurFilter(int maskSize) {
        if (maskSize % 2 == 0) {
            throw new IllegalArgumentException(String.format("the maskSize must odd, but %d is an even", maskSize));
        }
        this.maskSize = maskSize;
    }

    @Override
    public String key() {
        return "BlurFilter, maskSize:" + maskSize;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] returnPixels = NativeFilterFunc.averageSmooth(pixels, width, height, maskSize);
        Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height, Bitmap.Config.ARGB_8888);
        return returnBitmap;
    }
}
