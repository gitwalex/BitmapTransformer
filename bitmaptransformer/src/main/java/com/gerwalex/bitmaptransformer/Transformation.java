package com.gerwalex.bitmaptransformer;

import android.graphics.Bitmap;

public interface Transformation {
    String key();

    Bitmap transform(Bitmap source);
}
