package com.bambootang.viewpager3d;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.bamboo.viewpager3d.R;

import java.lang.reflect.Field;

/**
 * Created by tangshuai on 2017/9/14.
 */

public class BambooFlowViewPager extends ViewPager {

    private PageTransformer transformer;

    private float translationFactor = 0.4f;

    private float scaleFactor = 0.15f;

    private float rotationYFactor = 10;

    private float reflectAlphaFactor = 0.9f;

    private float maxRotationY = 30;

    private boolean clipAble = true;

    private boolean rotationAble = true;

    private float pageRound = 0.5f;

    private int space;

    public BambooFlowViewPager(Context context) {
        super(context);
        init();
    }

    public BambooFlowViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BambooFlowViewPager);
        translationFactor = a.getFloat(R.styleable.BambooFlowViewPager_translationFactor, translationFactor);
        scaleFactor = a.getFloat(R.styleable.BambooFlowViewPager_scaleFactor, scaleFactor);
        rotationYFactor = a.getFloat(R.styleable.BambooFlowViewPager_rotationYFactor, rotationYFactor);
        reflectAlphaFactor = a.getFloat(R.styleable.BambooFlowViewPager_reflectAlphaFactor, reflectAlphaFactor);
        space = a.getDimensionPixelSize(R.styleable.BambooFlowViewPager_pageSpace, space);
        pageRound = a.getFloat(R.styleable.BambooFlowViewPager_pageRound, pageRound);
        clipAble = a.getBoolean(R.styleable.BambooFlowViewPager_clipAble, clipAble);
        rotationAble = a.getBoolean(R.styleable.BambooFlowViewPager_rotaionYAble, rotationAble);
        maxRotationY = a.getFloat(R.styleable.BambooFlowViewPager_maxRotationY, maxRotationY);
        a.recycle();
        init();

    }

    protected void init() {
        FlowTransformer transformer = new FlowTransformer(this);
        transformer.setDoClip(clipAble);
        transformer.setDoRotationY(rotationAble);
        transformer.setSpace(space);
        transformer.setPageRoundFactor(pageRound);

        ScaleTransformer scaleTransformer = new LinearScaleTransformer(scaleFactor);

        LocationTransformer locationTransformer = new RelativeLocationTransformer(scaleTransformer, translationFactor);

        RotationTransformer rotationTransformer = new LinearRotationTransformer(rotationYFactor, maxRotationY);

        AlphaTransformer alphaTransformer = new PowerAlphaTransformer(reflectAlphaFactor);

        transformer.setAlphaTransformer(alphaTransformer);
        transformer.setLocationTransformer(locationTransformer);
        transformer.setRotationTransformer(rotationTransformer);
        transformer.setScaleTransformer(scaleTransformer);

        this.setPageTransformer(true, transformer, LAYER_TYPE_HARDWARE);
    }

    @Override
    public final void setClipChildren(boolean clipChildren) {
        super.setClipChildren(false);
    }

    @Override
    public final void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(false);
    }

    /**
     * 重写setPageTransformer方法，强制使用DRAW_ORDER_REVERSE排序方法
     *
     * @param reverseDrawingOrder
     * @param transformer
     * @param pageLayerType
     */
    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer, int pageLayerType) {
        super.setPageTransformer(false, transformer, pageLayerType);
        this.transformer = transformer;
    }

    public PageTransformer getTransformer() {
        return this.transformer;
    }

    //    因为滑动过程中getCurrentItem不会变化，所以无法方便的在滑动过程中调整排序,在order为,这里重写，只是为了以防万一霸蛮要把mDrawingOrder设置为DRAW_ORDER_FORWARD
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        Field mDrawingOrder = null;
        try {
            mDrawingOrder = ViewPager.class.getDeclaredField("mDrawingOrder");
            mDrawingOrder.setAccessible(true);
            int order = (int) mDrawingOrder.get(this);
            if (order != 2) {
                int currentItem = getCurrentItem();
                int[] sorts = new int[childCount];
                for (int index = 0; index <= currentItem; index++) {
                    sorts[index] = index;
                }
                sorts[currentItem] = 6;
                for (int index = 0; index < childCount - currentItem; index++) {
                    sorts[childCount - 1 - index] = index + currentItem;
                }
                int sort = sorts[i];
                return sort;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return super.getChildDrawingOrder(childCount, i);

    }


}
