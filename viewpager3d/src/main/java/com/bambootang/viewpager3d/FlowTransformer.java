package com.bambootang.viewpager3d;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * ViewPager3D 的核心算法类，
 */
public class FlowTransformer implements ViewPager.PageTransformer {

    /**
     * View的默认透视视距
     */
    private static final double DEFAULT_DEEP_DISTANCE = 576;

    protected double AO = DEFAULT_DEEP_DISTANCE;

    protected ViewPager viewPager;

    /**
     * 是否将重叠区域切除
     */
    protected boolean doClip = true;

    /**
     * 是否旋转
     */
    protected boolean doRotationY = true;

    private int pagerOrder = 0;

    /**
     * 这是根据给定宽度、角度，计算出以Y轴旋转后中心点左边以及右边的最终宽度的公式。ao为左边、bo为右边，AO为视距、XO为变换之前X轴离中心点的距离
     * 1、ao = (AO * Math.cos(S) * XO) / (AO + Math.sin(S) * XO);
     * 2、bo = AO * Math.sin(0.5 * Math.PI - S) * XO / (AO - Math.cos(0.5 * Math.PI - S) * XO);
     * 根据第一个公式反推出来的根据ao、角度获取XO的公式。
     * 3、XO = Math.round(ao * AO / (AO * Math.cos(S) - ao * Math.sin(S)));
     *
     * 公式是立体几何计算来的。
     */

    /**
     * page之间的间隔，只有在doClip为true的情况下有效
     */
    protected int space = 0;

    /**
     * 分页因子
     */
    protected float pageRoundFactor = 0.5f;

//    protected LocationTransformer locationTransformer = new LinearLocationTransformer(0.5f);

    protected ScaleTransformer scaleTransformer = new LinearScaleTransformer(0.15f);

    protected LocationTransformer locationTransformer = new RelativeLocationTransformer(scaleTransformer, 0.4f);

    protected RotationTransformer rotationTransformer = new LinearRotationTransformer(10, 30);

    protected AlphaTransformer alphaTransformer = new PowerAlphaTransformer(0.9f);

    protected TransformIntercepter transformIntercepter;

    //重叠区域的开始和结束位置
    protected double clipRight = 0, clipLeft = 0;

    public FlowTransformer(ViewPager viewPager) {
        initViewPager(viewPager);
    }

    public void bindWidthViewPager(ViewPager viewPager) {
        initViewPager(viewPager);
    }

    private void initViewPager(final ViewPager viewPager) {
        this.viewPager = viewPager;
        this.viewPager.setPageTransformer(true, this);
        this.viewPager.setClipChildren(false);
        this.viewPager.setClipToPadding(false);
        viewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                viewPager.setClipChildren(false);
                viewPager.setClipToPadding(false);
            }
        });
    }

    public void transformPage(View page, float position) {
        checkViewPagerDrawOrder();
        float paddingLeft = viewPager.getPaddingLeft();
        int pageWidth = page.getWidth();
        if (pageWidth == 0) {
            return;
        }
        float correctionValue = paddingLeft * 1f / pageWidth;
        position -= correctionValue;

        //执行缩放
        float scaleFactor = getPageScale(position);
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);

        //执行移动
        transLationX(page, position);
        setPivotXY(page, position);

        AO = DEFAULT_DEEP_DISTANCE / scaleFactor;
        //华为手机系统存在bug，不支持y轴的旋转，调用setRotationY后，整个Pager都会消失
        if (!Build.BRAND.toLowerCase().equals("honor") && doRotationY) {
            float rotateY = getRotation(position);
            page.setRotationY(rotateY);
        }
        if (doClip) {
            doClip(page, position);
        }


        correctPageOrder(page, position);

        //设置倒影的透明度
        View reflectView = page.findViewById(android.R.id.copy);
        if (reflectView != null) {
            reflectView.setAlpha(getAlpha(position));
        }

        //如果有拦截器注入，把transform流程交给拦截器继续
        if (transformIntercepter != null) {
            transformIntercepter.transformPage(page, position);
        }
    }

    /**
     * 检查ViewPager的drawOrder是否为DRAW_ORDER_REVERSE，如果不是，就重新设置为DRAW_ORDER_REVERSE。
     * 为了性能，只会检测一次
     */
    protected void checkViewPagerDrawOrder() {
        if (pagerOrder != 2) {
            pagerOrder = 2;
            Field mDrawingOrder = null;
            try {
                mDrawingOrder = ViewPager.class.getDeclaredField("mDrawingOrder");
                mDrawingOrder.setAccessible(true);
                int order = (int) mDrawingOrder.get(viewPager);
                if (order != 2) {
                    mDrawingOrder.set(viewPager, 2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void doClip(View page, float position) {
        //转化之后非重叠区域的宽度
        double keepWidth = 0;
        int pageWidth = page.getWidth();
        float scaleFactor = getPageScale(position);
        if (!doRotationY || Build.BRAND.toLowerCase().equals("honor")) {
            if (position > 0) {
                keepWidth = 1 - (scaleFactor - (1 - getPreTransLationX(position))) / scaleFactor;
                keepWidth *= pageWidth;
                if (pageRoundFactor < position && position < 1) {//1、
                    double difWidth = (1 - getPageScale(position - 1)) * pageWidth;
                    keepWidth += difWidth / scaleFactor;
                } else if (position <= pageRoundFactor) {//2、
                    keepWidth = pageWidth;
                } else {//3、
                    keepWidth += 0.0f;
                }
                clipRight = pageWidth;
                clipLeft = (pageWidth - keepWidth);
            } else {
                keepWidth = 1 - (scaleFactor - (1 - getNextTransLationX(position))) / scaleFactor;
                keepWidth *= pageWidth;
                if (pageRoundFactor - 1 > position && position > -1) {//2、
                    double difWidth = (1 - getPageScale(position + 1)) * pageWidth;
                    keepWidth += difWidth / scaleFactor;
                } else if (position > pageRoundFactor - 1) {//1、
                    keepWidth = pageWidth;
                } else {//3、
                    keepWidth += 0.0f;
                }
                clipRight = keepWidth;
                clipLeft = 0;
            }
        } else {

            float rotateY = getRotation(position);
            double angle = Math.abs(rotateY / 180 * Math.PI);
            if (position > 0) {
                keepWidth = 1 - (scaleFactor - (1 - getPreTransLationX(position))) / scaleFactor;
                keepWidth *= pageWidth;
                if (pageRoundFactor < position && position < 1) {//position为0～1的时候，要考虑position为-1～0的item的右边斜进去的宽度
                    double difWidth = computeWidthAfterRotationY(page, position - 1);
                    difWidth = (difWidth * AO / (AO * Math.cos(angle) - difWidth * Math.sin(angle)));
                    keepWidth += difWidth / scaleFactor + 0.25f;
                    keepWidth = correctWidthAfterRotationY(keepWidth, angle, position);
                    //进行旋转
                    //调整重叠区域
                    clipLeft = pageWidth - keepWidth;
                } else if (position <= pageRoundFactor) {//2、
                    keepWidth = pageWidth;
                } else {//3、
                    keepWidth = correctWidthAfterRotationY(keepWidth, angle, position);
                }
                clipRight = pageWidth;
                clipLeft = pageWidth - keepWidth;

            } else {
                keepWidth = 1 - (scaleFactor - (1 - getNextTransLationX(position))) / scaleFactor;
                keepWidth *= pageWidth;
                if (-1 < position && position <= pageRoundFactor - 1) {//2、position为0～1的时候，要考虑position为-1～0的item的右边斜进去的宽度
                    double difWidth = computeWidthAfterRotationY(page, position + 1);
                    difWidth = (difWidth * AO / (AO * Math.cos(angle) - difWidth * Math.sin(angle)));
                    keepWidth += difWidth / scaleFactor + 0.25f;
                    keepWidth = correctWidthAfterRotationY(keepWidth, angle, position);
                } else if (position > pageRoundFactor - 1) {//1、
                    keepWidth = pageWidth;
                } else {//3
                    keepWidth = correctWidthAfterRotationY(keepWidth, angle, position);
                }
                clipRight = keepWidth;
                clipLeft = 0;
            }
        }
        clipPage(page, position);
    }


    /**
     * 计算View在进行Y轴旋转后，宽度的变化
     *
     * @param view
     * @param position
     * @return
     */
    protected double computeWidthAfterRotationY(View view, float position) {
        float scale = getPageScale(position);
        float rotationY = getRotation(position);
        double angle = Math.abs(rotationY / 180 * Math.PI);
        double AO = DEFAULT_DEEP_DISTANCE / scale;
        double ao = ((AO * Math.cos(angle) * view.getWidth() * scale) / (AO + Math.sin(angle) * view.getWidth() * scale));
        return (view.getWidth() - ao);
    }

    /**
     * 修正在旋转后会因为绘制时丢失像素导致边界分离的问题
     *
     * @return 修正后的宽度
     */
    protected double correctWidthAfterRotationY(double width, double angle, float position) {
        double AO = DEFAULT_DEEP_DISTANCE / getPageScale(position);
        //通过最终需要留下的宽度，反向计算出该宽度旋转之前的宽度
        width = width * AO / (AO * Math.cos(angle) - width * Math.sin(angle));
        double ao = ((AO * Math.cos(angle) * width) / (AO + Math.sin(angle) * width));
        //计算出最终值
        width = (ao * AO / (AO * Math.cos(angle) - ao * Math.sin(angle)));
        return width;
    }


    /**
     * 将重叠区域切掉，避免如果page有透明的地方会透视出来
     *
     * @param page
     * @param position
     */
    protected void clipPage(View page, float position) {
        if (Build.VERSION.SDK_INT >= 18) {
            if (position <= pageRoundFactor - 1) {
                page.setClipBounds(new Rect(0, 0, (int) Math.round(clipRight - space / getPageScale(position)), page.getHeight()));
            } else if (position > pageRoundFactor) {
                page.setClipBounds(new Rect((int) Math.round(clipLeft + space / getPageScale(position)), 0, page.getWidth(), page.getHeight()));
            } else {
                page.setClipBounds(new Rect(0, 0, page.getWidth(), page.getHeight()));
            }
        } else if (page instanceof Clipable) {
            if (position <= pageRoundFactor - 1) {
                ((Clipable) page).setClipBound(new Rect(0, 0, (int) Math.round(clipRight - space / getPageScale(position)), page.getHeight()));
            } else if (position > pageRoundFactor) {
                ((Clipable) page).setClipBound(new Rect((int) Math.round(clipLeft + space / getPageScale(position)), 0, page.getWidth(), page.getHeight()));
            } else {
                ((Clipable) page).setClipBound(new Rect(0, 0, page.getWidth(), page.getHeight()));
            }
        } else {
            Log.d("FlowTransformer", "ViewPager的ItemView必须实现Clipable接口，您也可以直接使用ClipView");
        }
    }

    /**
     * 因为ViewPager的getChildDrawingOrder方法无法在滑动的过程中修改排序，观看源码发现顺序是由LayoutParams里面的position控制的，所以，这里直接反射设置
     * 调整Page的绘制顺序，滑动到最靠近中间的Page显示在最上端，然后依次往两边由浅入深
     *
     * @param page
     * @param position
     */
    protected void correctPageOrder(View page, float position) {
        try {
            Field mDrawingOrder = ViewPager.class.getDeclaredField("mDrawingOrder");
            mDrawingOrder.setAccessible(true);
            int order = (int) mDrawingOrder.get(viewPager);
            if (order == 2) {
                ViewGroup.LayoutParams layoutParam = page.getLayoutParams();
                Field field = ViewPager.LayoutParams.class.getDeclaredField("position");
                field.setAccessible(true);
                int lPosition = (int) field.get(layoutParam);
                if (lPosition != Math.abs(round(position))) {
                    field.set(layoutParam, Math.abs(round(position)));
                    page.requestLayout();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int round(float position) {
        if (position >= 1 - pageRoundFactor || position <= -1 + pageRoundFactor) {
            return Math.round(position);
        }
        if (position > 0) {
            if ((position - (int) position) < pageRoundFactor) {
                return (int) position;
            } else if ((position - (int) position) >= pageRoundFactor) {
                return (int) position + 1;
            } else {
                return (int) position;
            }
        } else {
            if ((position - (int) position) > -pageRoundFactor) {
                return (int) position;
            } else if (Math.abs(position - (int) position) <= pageRoundFactor) {
                return (int) position - 1;
            } else {
                return (int) position;
            }
        }
    }


    protected void transLationX(View page, float position) {
        float transLationX = getTransLationX(position) * page.getWidth();
        page.setTranslationX(transLationX);
    }

    /**
     * 以边界线中点作为page的转换中心点，让视觉效果看起来更圆滑
     * 中心点不同，则整套算法也会变化，所以，TransformIntercepter一定不能修改page的pivotX、pivotY
     */
    private void setPivotXY(View page, float position) {
        if (position > 0) {//右边的以右边边界线中点作为中心点
            page.setPivotX(page.getWidth());
            page.setPivotY(page.getHeight() / 2);
        } else {//左边的以左边边界中心点作为中心点
            page.setPivotX(0);
            page.setPivotY(page.getHeight() / 2);
        }
    }

    protected float getPageScale(float position) {
        float scale = scaleTransformer.getScale(position);
        scale = scale == 0 ? 0.0001f : scale;
        return Math.abs(scale);
    }

    protected float getPreTransLationX(float position) {
        float dTrans = locationTransformer.getTransLation(position);
        float preTrans = locationTransformer.getTransLation(position - 1);
        return preTrans - dTrans;
    }

    protected float getNextTransLationX(float position) {
        float dTrans = locationTransformer.getTransLation(position);
        float preTrans = locationTransformer.getTransLation(position + 1);
        return dTrans - preTrans;
    }

    protected float getTransLationX(float position) {
        return locationTransformer.getTransLation(position);
    }

    protected float getRotation(float position) {
        float rotationY = rotationTransformer.getRotation(position);
        return rotationY;
    }

    protected float getAlpha(float position) {
        return alphaTransformer.getAlpha(position);
    }

    public void setDoClip(boolean doClip) {
        this.doClip = doClip;
    }

    public void setDoRotationY(boolean doRotationY) {
        this.doRotationY = doRotationY;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public void setPageRoundFactor(float pageRoundFactor) {
        this.pageRoundFactor = pageRoundFactor;
    }

    public void setScaleTransformer(ScaleTransformer scaleTransformer) {
        this.scaleTransformer = scaleTransformer;
    }

    public void setLocationTransformer(LocationTransformer locationTransformer) {
        this.locationTransformer = locationTransformer;
    }

    public void setRotationTransformer(RotationTransformer rotationTransformer) {
        this.rotationTransformer = rotationTransformer;
    }

    public void setAlphaTransformer(AlphaTransformer alphaTransformer) {
        this.alphaTransformer = alphaTransformer;
    }

    public void setTransformIntercepter(TransformIntercepter transformIntercepter) {
        this.transformIntercepter = transformIntercepter;
    }

    public boolean isDoClip() {
        return doClip;
    }

    public boolean isDoRotationY() {
        return doRotationY;
    }

    public int getSpace() {
        return space;
    }

    public float getPageRoundFactor() {
        return pageRoundFactor;
    }

    public ScaleTransformer getScaleTransformer() {
        return scaleTransformer;
    }

    public LocationTransformer getLocationTransformer() {
        return locationTransformer;
    }

    public RotationTransformer getRotationTransformer() {
        return rotationTransformer;
    }

    public AlphaTransformer getAlphaTransformer() {
        return alphaTransformer;
    }

    public TransformIntercepter getTransformIntercepter() {
        return transformIntercepter;
    }
}