package com.bambootang.sample;

import android.graphics.Color;
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

import java.util.HashMap;

public class Sample1Activity extends AppCompatActivity {

    //    int[] imgIds = {R.drawable.img_001,
//            R.drawable.img_002,
//            R.drawable.img_003,
//            R.drawable.img_004,
//            R.drawable.img_005,
//            R.drawable.img_006,
//            R.drawable.img_007
//    };
    int[] imgIds = {
            Color.parseColor("#60000000"),
            Color.parseColor("#80FF0000"),
            Color.parseColor("#60000000"),
            Color.parseColor("#80FF0000"),
            Color.parseColor("#60000000")
    };

    BambooFlowViewPager fvp_pagers;

    HashMap<Integer, ClipView> imageViewList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample1);

        fvp_pagers = (BambooFlowViewPager) findViewById(R.id.fvp_pagers);
        fvp_pagers.setAdapter(pagerAdapter);

        // 设置ViewPager的缓存页数，因为demo没有做缓存，所以为了方便就这么搞了，页数不多的时候可以把这里设置为总页数
        fvp_pagers.setOffscreenPageLimit(imgIds.length);
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
                imageView.setBackgroundColor(imgIds[position]);
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                clipView = new ClipView(container.getContext());
                clipView.setId(position + 1);
                clipView.addView(imageView, new ClipView.LayoutParams(ClipView.LayoutParams.MATCH_PARENT, ClipView.LayoutParams.MATCH_PARENT));
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
