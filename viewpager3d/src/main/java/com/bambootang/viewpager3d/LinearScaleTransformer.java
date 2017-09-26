package com.bambootang.viewpager3d;

/**
 * Created by tangshuai on 2017/9/17.
 */

public class LinearScaleTransformer implements ScaleTransformer {

    private float scaleFactor;

    public LinearScaleTransformer(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public float getScale(float position) {
        return (1 - scaleFactor) + scaleFactor * (1 - Math.abs(position));
    }
}
