package com.bambootang.viewpager3d;

/**
 * Created by tangshuai on 2017/9/17.
 */

public class RelativeLocationTransformer implements LocationTransformer {

    private float relative;
    private ScaleTransformer scaleTransformer;

    public RelativeLocationTransformer(ScaleTransformer scaleTransformer, float relative) {
        this.relative = relative > 1 ? 1 : relative;
        this.scaleTransformer = scaleTransformer;
    }

    @Override
    public float getTransLation(float position) {
        int direction = position > 0 ? -1 : 1;
        if (position <= 0 && position > -1) {
            float scaleX = scaleTransformer.getScale(position);
            return position * (scaleX * (1 - relative) - 1);
        } else if (position >= 0 && position < 1) {
            float scaleX = scaleTransformer.getScale(position);
            return position * (scaleX * (1 - relative) - 1);
        } else {
            float scaleX = scaleTransformer.getScale(position);
            return -direction * (scaleX * (1 - relative) - 1) + getTransLation(position + direction);
        }
    }
}
