# ViewPager3D

## Demo

![image1.png](https://github.com/bambootang/ViewPager3D/blob/master/imgs/img1.gif)

![image2.png](https://github.com/bambootang/ViewPager3D/blob/master/imgs/img2.gif)

![image3.png](https://github.com/bambootang/ViewPager3D/blob/master/imgs/img3.gif)

或者下载 [sample.apk](https://github.com/bambootang/ViewPager3D/blob/master/sample.apk) 查看实际的效果.


**ViewPager3D组件BambooFlowViewPager支持3种变换器：**

LocationTransformer   控制page之间重叠区域

ScaleTransform         控制page之间的缩减变换

RotationTransform      控制page的内旋角度

同时支持重叠区域Clip裁切、page之间的space、中间页切换的切换点控制等。

利用上面的功能以及内置的倒影组件可以做出非常丰富的特效动画效果。
  
## 如何使用ViewPager3D组件 BambooFlowViewPager

BambooFlowViewPager自定义了xml属性来设置各个变换器变换因子，你可以选择直接使用xml的自定义属性控制变换器的变换因子，也可以选择使用java代码重写各个变换器的变换算法来实现各种不一样的特效。更多具体的使用方式请查看sample的源码


## dependencies

        compile 'com.bambootang:viewpager3d:1.4'

  
  

### 1、在XML中使用BambooFlowViewPager


    <com.bambootang.viewpager3d.BambooFlowViewPager
        android:id="@+id/fvp_pagers"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_margin="20dp"
        android:paddingLeft="200dp"
        android:paddingRight="200dp"
        app:clipAble="true"
        app:maxRotationY="30"
        app:pageRound="0.5"
        app:pageSpace="8dp"
        app:reflectAlphaFactor="0.9"
        app:rotaionYAble="true"
        app:rotationYFactor="15"
        app:scaleFactor="0.1"
        app:translationFactor="0.5" />

#### 属性说明

**clipAble：** 控制是否裁切掉重叠区域，在内容包含透明区域时很有意义，**default = true**

**pageRound：** 控制切换中间页上下层切换的位置，\[0～1\]，**default = 0.5**

**pageSpace：** 页间距，clipAble为true时才有意义，**default = 0**

**reflectAlphaFactor：** 倒影透明度渐变因子，指数次方，**default = 0.9**

**rotaionYAble：** 控制是否内旋，**default = true**

**rotationYFactor：** 旋转因子，每一页的旋转差，\[0～1\]，**default = 10**

**maxRotationY：** 控制最大旋转角度，\[0～90\)，**default = 30**

**scaleFactor：** 缩减因子，相对于中间页的每一页的缩减量，\[0～1\]，**default = 0.15**

**translationFactor：** 移动因子，相对于自身宽度向前一页移动的比例，\[0～1\]，**default = 0.4**


#
  
  
### 2、在Java代码中使用FlowTransformer

        ViewPager vp_pagers = (ViewPager) findViewById(R.id.vp_pagers);
        vp_pagers.setAdapter(pagerAdapter);
        FlowTransformer transformer = new FlowTransformer(vp_pagers);
        vp_pagers.setPageTransformer(true, transformer);
        flowTransformer.setDoClip(false);
        flowTransformer.setDoRotationY(false);
        flowTransformer.setSpace(0);
        flowTransformer.setLocationTransformer(new LinearLocationTransformer(0.5f));
        flowTransformer.setScaleTransformer(new LinearScaleTransformer(0.15f));
        flowTransformer.setPageRoundFactor(0.0f);
        flowTransformer.setRotationTransformer(new LinearRotationTransformer(10));
        
        
        
        
## 如何使用倒影组件 BambooReflectView 

  
### 1、在XML中使用BambooReflectView

    <com.bambootang.viewpager3d.BambooReflectView
        android:id="@+id/rv_reflect"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:reflect="0.5"
        app:reflectSpace="8dp"
    </com.bambootang.viewpager3d.BambooReflectView>

**reflect：** 倒影生成占原View的比例，\[0～1\]，**default = 0.5**

**reflectSpace：** //倒影与View的中间的间隔，**default = 2dp**

#
  
### 2、在Java代码中使用BambooReflectView

        ImageView imageView = new ImageView(container.getContext());
        imageView.setImageResource(imgIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ReflectView reflectView = new ReflectView(container.getContext());
        reflectView.setContentView(imageView, 0.5f);
        reflectView.setSpace(20);
#
  
  
  
## 怎么控制ViewPager3D中间页的with

    viewPager.setPaddingLeft(paddingLeft);
    viewPager.setPaddingRight(paddingRight);
    //then centerpage's with = viewPager.getWidth() - paddingLeft - paddingRight
#### 或者

    android:width="800dp"
    android:paddingLeft="220dp"
    android:paddingRight="220dp"
    //then centerpage's with = 360dp
    

灵活使用这些可变项，可以做出非常丰富的动画效果。

