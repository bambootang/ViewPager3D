package com.bambootang.viewpager3d;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.view.View;

/**
 * 创建图片倒影的工具类
 *
 * @author kerwin
 */
public class ImageReflect {

    /**
     * View 生成Cache图片
     *
     * @param paramView
     * @param width     生成的图片的最大宽度
     * @param height    生成的图片的最大高度
     * @return paramView's drawingCache
     */
    public static Bitmap convertViewToBitmap(View paramView, int width, int height) {
        try {
            if (paramView.getWidth() == 0 || paramView.getHeight() == 0) {
                paramView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
                paramView.layout(0, 0, paramView.getMeasuredWidth(), paramView.getMeasuredHeight());
            }
            paramView.setDrawingCacheEnabled(true);
            paramView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            if (paramView.isDrawingCacheEnabled()) {
                paramView.buildDrawingCache();
                Bitmap bm = paramView.getDrawingCache();
                if (bm != null) {
                    System.out.println(bm.getWidth() + "," + bm.getHeight());
                    Bitmap tmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_4444);
                    new Canvas(tmp).drawBitmap(bm, 0, 0, new Paint());
                    paramView.setDrawingCacheEnabled(false);
                    paramView.destroyDrawingCache();
                    return tmp;
                }
            }
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据图片生成该图片的倒影
     *
     * @param originalImage 原图
     * @param percent       倒影的比例，0～1
     * @return 给定图片的倒影
     */
    public static Bitmap createReflectedImage(Bitmap originalImage, float percent) {
        if (originalImage == null) {
            return null;
        }

        percent = Math.abs(percent) > 1 ? 1 : Math.abs(percent);

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(1, -1);

        //翻转图片的下半部分
        final Bitmap bitmapWithReflection = Bitmap.createBitmap(originalImage, 0, (int) (height - percent * height), width, (int) (percent * height), matrix, true);

        Canvas canvas = new Canvas(bitmapWithReflection);

        Paint defaultPaint = new Paint();
        defaultPaint.setAntiAlias(true);


        Paint paint = new Paint();

        LinearGradient shader = new LinearGradient(0,
                0, 0, bitmapWithReflection.getHeight()
                , 0xFFffffff, 0x80ffffff,
                TileMode.MIRROR);

        paint.setShader(shader);

        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        canvas.drawRect(0, 0, width, bitmapWithReflection.getHeight()
                , paint);

//        originalImage.recycle();

        return bitmapWithReflection;
    }


    public static Bitmap createReflectedImage(Bitmap originalImage) {
        return createReflectedImage(originalImage, 1);
    }
}
