# ViewPager3D

## Demo

![image1.png](https://github.com/bambootang/ViewPager3D/blob/master/imgs/img1.gif)

![image3.png](https://github.com/bambootang/ViewPager3D/blob/master/imgs/img3.gif)

或者下载 [sample.apk](https://github.com/bambootang/ViewPager3D/blob/master/sample.apk) 查看实际的效果.


## dependencies

        compile 'com.bambootang:viewpager3d:1.4'

  
  
  
## How To Use BambooFlowViewPager
### 1、Use BambooFlowViewPager In XML

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
  
  
### 2、Use FlowTransformer In Java Code

        ViewPager vp_pagers = (ViewPager) findViewById(R.id.vp_pagers);
        vp_pagers.setAdapter(pagerAdapter);
        FlowTransformer transformer = new FlowTransformer(vp_pagers);
        vp_pagers.setPageTransformer(true, transformer);
        transformer.setxxx(xxx);
        
        
        
        
        
## How To Use BambooReflectView 
  
### 1、Use BambooReflectView In XML

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
  
### 2、Use BambooReflectView In Java Code

        ImageView imageView = new ImageView(container.getContext());
        imageView.setImageResource(imgIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ReflectView reflectView = new ReflectView(container.getContext());
        reflectView.setContentView(imageView, 0.5f);
        reflectView.setSpace(20);
#
  
  
  
## How To Regulate CenterPage's With

    viewPager.setPaddingLeft(paddingLeft);
    viewPager.setPaddingRight(paddingRight);
    //then centerpage's with = viewPager.getWidth() - paddingLeft - paddingRight
#### Or

    android:width="800dp"
    android:paddingLeft="220dp"
    android:paddingRight="220dp"
    //then centerpage's with = 360dp
    

灵活使用这些可变项，可以做出非常丰富的动画效果。

更多使用方式，请查阅sample module.



# The Last, Thanks For Your Star * 😄
