package com.bambootang.viewpager3d;

/**
 * Created by tangshuai on 2017/9/17.
 */

public class LinearLocationTransformer implements LocationTransformer {

    private float transLationX = 0;

    public LinearLocationTransformer(float transLationX) {
        this.transLationX = transLationX;
    }

    @Override
    public float getTransLation(float position) {
        return -position * transLationX;
    }
}
