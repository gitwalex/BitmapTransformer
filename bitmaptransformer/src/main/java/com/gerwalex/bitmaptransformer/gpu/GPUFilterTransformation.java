package com.gerwalex.bitmaptransformer.gpu;
/**
 * Copyright (C) 2020 Wasabeef
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUFilterTransformation implements Transformation {

    private final Context mContext;
    private final GPUImageFilter mFilter;

    public GPUFilterTransformation(Context context, GPUImageFilter filter) {
        mContext = context.getApplicationContext();
        mFilter = filter;
    }

    @SuppressWarnings("unchecked")
    public <T> T getFilter() {
        return (T) mFilter;
    }

    @Override
    public String key() {
        return getClass().getSimpleName();
    }

    @Override
    public Bitmap transform(Bitmap source) {
        GPUImage gpuImage = new GPUImage(mContext);
        gpuImage.setImage(source);
        gpuImage.setFilter(mFilter);
        Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
        source.recycle();
        return bitmap;
    }
}
