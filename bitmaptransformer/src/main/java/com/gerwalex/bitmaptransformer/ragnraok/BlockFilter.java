package com.gerwalex.bitmaptransformer.ragnraok;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class BlockFilter implements Transformation {

    static {
        System.loadLibrary("AndroidImageFilter");
    }

    @Override
    public String key() {
        return "BlockFilter";
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] returnPixels = NativeFilterFunc.blockFilter(pixels, width, height);
        return Bitmap.createBitmap(returnPixels, width, height, Bitmap.Config.ARGB_8888);
    }
}
