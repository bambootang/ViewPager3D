package com.bambootang.viewpager3d;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bamboo.viewpager3d.R;

/**
 * 倒影组件类
 * Created by tangshuai on 2017/9/14.
 */

public class BambooReflectView extends LinearLayout {

    private View view;

    private ImageView reflectView;

    private float reflectHeight;

    private int space;

    public BambooReflectView(Context context) {
        super(context);
        this.space = dip2px(2);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public BambooReflectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BambooReflectView);
        reflectHeight = a.getFloat(R.styleable.BambooReflectView_reflect, 0.5f);
        space = a.getDimensionPixelSize(R.styleable.BambooReflectView_reflectSpace, dip2px(2));
        a.recycle();

        reflectHeight = reflectHeight > 1 ? 1 : reflectHeight;
        reflectHeight = reflectHeight < 0 ? 0 : reflectHeight;
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            this.setWeightSum(10 + reflectHeight * 10);

            this.view = getChildAt(0);
            LayoutParams viewLayoutParams = (LayoutParams) this.view.getLayoutParams();
            if (viewLayoutParams.width != LayoutParams.MATCH_PARENT) {
                removeAllViews();
                LinearLayout layout = new LinearLayout(getContext());
                LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 10);
                layoutParams.weight = 10;
                this.view.setId(this.view.getId());
                layout.addView(this.view);
                layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                this.view = layout;
                addView(layout, layoutParams);
            } else {
                removeAllViews();
                viewLayoutParams.weight = 10;
                viewLayoutParams.height = 10;
                viewLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                addView(view, viewLayoutParams);
            }

            View spaceView = new View(getContext());
            addView(spaceView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, space));

            reflectView = new ImageView(getContext());
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 10);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.weight = reflectHeight * 10;
            reflectView.setLayoutParams(layoutParams);
            reflectView.setId(android.R.id.copy);
            addView(reflectView);

            this.view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    BambooReflectView.this.view.removeOnLayoutChangeListener(this);
                    updateReflect();
                    BambooReflectView.this.view.addOnLayoutChangeListener(this);
                }
            });
        }
    }

    public void setContentView(View view, float reflectHeight) {
        removeAllViews();
        this.view = view;
        this.reflectHeight = reflectHeight;
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams viewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10);
        viewLayoutParams.weight = 10;
        this.addView(view, viewLayoutParams);
        this.setWeightSum(10 + reflectHeight * 10);

        View spaceView = new View(getContext());
        addView(spaceView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, space));

        reflectView = new ImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10);
        layoutParams.weight = reflectHeight * 10;
        reflectView.setLayoutParams(layoutParams);
        reflectView.setId(android.R.id.copy);
        addView(reflectView);

        this.view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                BambooReflectView.this.view.removeOnLayoutChangeListener(this);
                updateReflect();
                BambooReflectView.this.view.addOnLayoutChangeListener(this);
            }
        });
    }

    public void setSpace(int space) {
        this.space = space;
        LayoutParams layoutParams = (LayoutParams) reflectView.getLayoutParams();
        layoutParams.topMargin = space;
        reflectView.setLayoutParams(layoutParams);
    }

    /**
     * 刷新倒影
     */
    public void updateReflect() {
        Bitmap bm = ImageReflect.convertViewToBitmap(view, view.getWidth(), view.getHeight());
        Bitmap reflectBitmap = ImageReflect.createReflectedImage(bm, reflectHeight);
        reflectView.setBackgroundDrawable(new BitmapDrawable(reflectBitmap));
    }


    int dip2px(float dpValue) {
        return (int) TypedValue.applyDimension(1, dpValue, getResources().getDisplayMetrics());
    }

}
