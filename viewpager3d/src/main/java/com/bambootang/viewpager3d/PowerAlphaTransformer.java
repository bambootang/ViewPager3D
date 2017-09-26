package com.bambootang.viewpager3d;

/**
 * Created by tangshuai on 2017/9/17.
 */

public class PowerAlphaTransformer implements AlphaTransformer {

    private float alpha;

    public PowerAlphaTransformer(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public float getAlpha(float position) {
        return (float) Math.pow(alpha, Math.abs(position));
    }
}
