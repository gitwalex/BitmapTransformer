package com.gerwalex.bitmaptransformer.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gerwalex.bitmaptransformer.BitmapUtils;
import com.gerwalex.bitmaptransformer.BlurTransformation;
import com.gerwalex.bitmaptransformer.ColorFilterTransformation;
import com.gerwalex.bitmaptransformer.CropCircleTransformation;
import com.gerwalex.bitmaptransformer.CropSquareTransformation;
import com.gerwalex.bitmaptransformer.CropTransformation;
import com.gerwalex.bitmaptransformer.GrayscaleTransformation;
import com.gerwalex.bitmaptransformer.MaskTransformation;
import com.gerwalex.bitmaptransformer.RoundedCornersTransformation;
import com.gerwalex.bitmaptransformer.gpu.BrightnessFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.ContrastFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.InvertFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.KuwaharaFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.PixelationFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.SepiaFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.SketchFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.SwirlFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.ToonFilterTransformation;
import com.gerwalex.bitmaptransformer.gpu.VignetteFilterTransformation;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Wasabeef on 2015/01/11.
 */
public class PicassoAdapter extends RecyclerView.Adapter<PicassoAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Type> mDataSet;
    private File file;

    public PicassoAdapter(Context context, List<Type> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PicassoAdapter.ViewHolder holder, int position) {
        switch (mDataSet.get(position)) {
            case Mask: {
                int width = BitmapUtils.toDp(mContext, 266.66f);
                int height = BitmapUtils.toDp(mContext, 252.66f);
                Picasso
                        .get()
                        .load(file)
                        .resize(width, height)
                        .centerCrop()
                        .transform(new MaskTransformation(mContext, R.drawable.mask_starfish))
                        .into(holder.image);
                break;
            }
            case NinePatchMask: {
                int width = BitmapUtils.toDp(mContext, 300.0f);
                int height = BitmapUtils.toDp(mContext, 200.0f);
                Picasso
                        .get()
                        .load(file)
                        .resize(width, height)
                        .centerCrop()
                        .transform(new MaskTransformation(mContext, R.drawable.chat_me_mask))
                        .into(holder.image);
                break;
            }
            case CropLeftTop:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(300, 100, CropTransformation.GravityHorizontal.LEFT,
                                CropTransformation.GravityVertical.TOP))
                        .into(holder.image);
                break;
            case CropLeftCenter:
                Picasso
                        .get()
                        .load(file)
                        // 300, 100, CropTransformation.GravityHorizontal.LEFT, CropTransformation.GravityVertical.CENTER))
                        .transform(new CropTransformation(300, 100))
                        .into(holder.image);
                break;
            case CropLeftBottom:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(300, 100, CropTransformation.GravityHorizontal.LEFT,
                                CropTransformation.GravityVertical.BOTTOM))
                        .into(holder.image);
                break;
            case CropCenterTop:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(300, 100, CropTransformation.GravityHorizontal.CENTER,
                                CropTransformation.GravityVertical.TOP))
                        .into(holder.image);
                break;
            case CropCenterCenter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(300, 100))
                        .into(holder.image);
                break;
            case CropCenterBottom:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(300, 100, CropTransformation.GravityHorizontal.CENTER,
                                CropTransformation.GravityVertical.BOTTOM))
                        .into(holder.image);
                break;
            case CropRightTop:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(300, 100, CropTransformation.GravityHorizontal.RIGHT,
                                CropTransformation.GravityVertical.TOP))
                        .into(holder.image);
                break;
            case CropRightCenter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(300, 100, CropTransformation.GravityHorizontal.RIGHT,
                                CropTransformation.GravityVertical.CENTER))
                        .into(holder.image);
                break;
            case CropRightBottom:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(300, 100, CropTransformation.GravityHorizontal.RIGHT,
                                CropTransformation.GravityVertical.BOTTOM))
                        .into(holder.image);
                break;
            case Crop169CenterCenter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation((float) 16 / (float) 9,
                                CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.CENTER))
                        .into(holder.image);
                break;
            case Crop43CenterCenter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation((float) 4 / (float) 3,
                                CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.CENTER))
                        .into(holder.image);
                break;
            case Crop31CenterCenter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(3, CropTransformation.GravityHorizontal.CENTER,
                                CropTransformation.GravityVertical.CENTER))
                        .into(holder.image);
                break;
            case Crop31CenterTop:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(3, CropTransformation.GravityHorizontal.CENTER,
                                CropTransformation.GravityVertical.TOP))
                        .into(holder.image);
                break;
            case CropSquareCenterCenter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation(1, CropTransformation.GravityHorizontal.CENTER,
                                CropTransformation.GravityVertical.CENTER))
                        .into(holder.image);
                break;
            case CropQuarterCenterCenter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation((float) 0.5, (float) 0.5,
                                CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.CENTER))
                        .into(holder.image);
                break;
            case CropQuarterCenterTop:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation((float) 0.5, (float) 0.5,
                                CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.TOP))
                        .into(holder.image);
                break;
            case CropQuarterBottomRight:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation((float) 0.5, (float) 0.5,
                                CropTransformation.GravityHorizontal.RIGHT, CropTransformation.GravityVertical.BOTTOM))
                        .into(holder.image);
                break;
            case CropHalfWidth43CenterCenter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropTransformation((float) 0.5, 0, (float) 4 / (float) 3,
                                CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.CENTER))
                        .into(holder.image);
                break;
            case CropSquare:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropSquareTransformation())
                        .into(holder.image);
                break;
            case CropCircle:
                Picasso
                        .get()
                        .load(file)
                        .transform(new CropCircleTransformation())
                        .into(holder.image);
                break;
            case ColorFilter:
                Picasso
                        .get()
                        .load(file)
                        .transform(new ColorFilterTransformation(Color.argb(80, 255, 0, 0)))
                        .into(holder.image);
                break;
            case Grayscale:
                Picasso
                        .get()
                        .load(file)
                        .transform(new GrayscaleTransformation())
                        .into(holder.image);
                break;
            case RoundedCorners:
                Picasso
                        .get()
                        .load(file)
                        .transform(new RoundedCornersTransformation(120, 0,
                                RoundedCornersTransformation.CornerType.DIAGONAL_FROM_TOP_LEFT))
                        .into(holder.image);
                break;
            case Blur:
                Picasso
                        .get()
                        .load(file)
                        .transform(new BlurTransformation(mContext, 25, 1))
                        .into(holder.image);
                break;
            case Toon:
                Picasso
                        .get()
                        .load(file)
                        .transform(new ToonFilterTransformation(mContext))
                        .into(holder.image);
                break;
            case Sepia:
                Picasso
                        .get()
                        .load(file)
                        .transform(new SepiaFilterTransformation(mContext))
                        .into(holder.image);
                break;
            case Contrast:
                Picasso
                        .get()
                        .load(file)
                        .transform(new ContrastFilterTransformation(mContext, 2.0f))
                        .into(holder.image);
                break;
            case Invert:
                Picasso
                        .get()
                        .load(file)
                        .transform(new InvertFilterTransformation(mContext))
                        .into(holder.image);
                break;
            case Pixel:
                Picasso
                        .get()
                        .load(file)
                        .transform(new PixelationFilterTransformation(mContext, 20))
                        .into(holder.image);
                break;
            case Sketch:
                Picasso
                        .get()
                        .load(file)
                        .transform(new SketchFilterTransformation(mContext))
                        .into(holder.image);
                break;
            case Swirl:
                Picasso
                        .get()
                        .load(file)
                        .transform(new SwirlFilterTransformation(mContext, 0.5f, 1.0f, new PointF(0.5f, 0.5f)))
                        .into(holder.image);
                break;
            case Brightness:
                Picasso
                        .get()
                        .load(file)
                        .transform(new BrightnessFilterTransformation(mContext, 0.5f))
                        .into(holder.image);
                break;
            case Kuawahara:
                Picasso
                        .get()
                        .load(file)
                        .transform(new KuwaharaFilterTransformation(mContext, 25))
                        .into(holder.image);
                break;
            case Vignette:
                Picasso
                        .get()
                        .load(file)
                        .transform(new VignetteFilterTransformation(mContext, new PointF(0.5f, 0.5f),
                                new float[]{0.0f, 0.0f, 0.0f}, 0f, 0.75f))
                        .into(holder.image);
                break;
        }
        holder.title.setText(mDataSet
                .get(position)
                .name());
    }

    @NonNull
    @Override
    public PicassoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(mContext)
                .inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    public File setFile(File file) {
        File oldfile = this.file;
        this.file = file;
        notifyDataSetChanged();
        return oldfile;
    }

    public enum Type {
        Mask, NinePatchMask, CropLeftTop, CropLeftCenter, CropLeftBottom, CropCenterTop, CropCenterCenter, CropCenterBottom, CropRightTop, CropRightCenter, CropRightBottom, CropSquareCenterCenter, Crop169CenterCenter, Crop43CenterCenter, Crop31CenterCenter, Crop31CenterTop, CropQuarterCenterCenter, CropQuarterCenterTop, CropQuarterBottomRight, CropHalfWidth43CenterCenter, CropSquare, CropCircle, ColorFilter, Grayscale, RoundedCorners, Blur, Toon, Sepia, Contrast, Invert, Pixel, Sketch, Swirl, Brightness, Kuawahara, Vignette
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
        }
    }
}
