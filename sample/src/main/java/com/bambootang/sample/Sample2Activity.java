package com.bambootang.sample;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bambootang.viewpager3d.ClipView;
import com.bambootang.viewpager3d.FlowTransformer;
import com.bambootang.viewpager3d.BambooFlowViewPager;
import com.bambootang.viewpager3d.LocationTransformer;
import com.bambootang.viewpager3d.RotationTransformer;
import com.bambootang.viewpager3d.ScaleTransformer;

import java.util.HashMap;

public class Sample2Activity extends AppCompatActivity {

    int[] imgIds = {R.drawable.img_001,
            R.drawable.img_002,
            R.drawable.img_003,
            R.drawable.img_004,
            R.drawable.img_005,
            R.drawable.img_006,
            R.drawable.img_007
    };

    BambooFlowViewPager fvp_pagers;

    HashMap<Integer, ClipView> imageViewList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample2);

        fvp_pagers = (BambooFlowViewPager) findViewById(R.id.fvp_pagers);
        fvp_pagers.setAdapter(pagerAdapter);

        fvp_pagers.setOffscreenPageLimit(imgIds.length);

//        fvp_pagers.getTransformer()
        FlowTransformer flowTransformer = new FlowTransformer(fvp_pagers);
        flowTransformer.setDoClip(false);
        flowTransformer.setDoRotationY(false);
        flowTransformer.setSpace(0);
        flowTransformer.setLocationTransformer(new LocationTransformer() {
            @Override
            public float getTransLation(float position) {
                if (position > 0) {
                    return -position * 0.98f;
                } else if (position > -1) {
                    return 0;
                } else {
                    return position * 10;
                }
            }
        });
        //右边的逐级缩小0.1,左边的第一个不缩小，其他的缩小为0
        flowTransformer.setScaleTransformer(new ScaleTransformer() {
            @Override
            public float getScale(float position) {
                if (position > 0) {
                    return 0.9f + 0.1f * (1 - Math.abs(position));
                } else if (position > -1) {
                    return 1;
                } else {
                    return 0;
                }
            }

        });
        //重叠翻页，所以应该是翻到最后的位置才替换上下层
        flowTransformer.setPageRoundFactor(0.0f);
        //设置旋转变换器
        flowTransformer.setRotationTransformer(new RotationTransformer() {
            @Override
            public float getRotation(float position) {
                float rotationY = -position * 20;
                rotationY = rotationY >= 30 ? 30 : rotationY;
                rotationY = rotationY <= -30 ? -30 : rotationY;
                return rotationY;
            }
        });
        fvp_pagers.setPageTransformer(true, flowTransformer);

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return imgIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ClipView clipView;
            if (imageViewList.containsKey(position)) {
                clipView = imageViewList.get(position);
            } else {
                ImageView imageView = new ImageView(container.getContext());
                imageView.setImageResource(imgIds[position]);
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                clipView = new ClipView(container.getContext());
                clipView.setId(position + 1);
                clipView.addView(imageView);
                imageViewList.put(position, clipView);
            }
            container.addView(clipView);
            clipView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "v.getId " + v.getId(), Toast.LENGTH_LONG).show();
                }
            });
            return clipView;
        }
    };
}
