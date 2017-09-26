package com.bambootang.viewpager3d;

/**
 * Created by tangshuai on 2017/9/17.
 */

public class LinearRotationTransformer implements RotationTransformer {

    private float rotation;

    private float maxRotation;

    public LinearRotationTransformer(float rotation, float maxRotation) {
        this.rotation = rotation;
        this.maxRotation = Math.abs(maxRotation);
    }

    @Override
    public float getRotation(float position) {
        float rotationY = -position * rotation;
        rotationY = rotationY >= 90 ? 89.9999f : rotationY;
        rotationY = rotationY <= -90 ? -89.9999f : rotationY;

        rotationY = rotationY >= maxRotation ? maxRotation : rotationY;
        rotationY = rotationY <= -maxRotation ? -maxRotation : rotationY;

        return rotationY;
    }
}
