package com.bambootang.sample;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by tangshuai on 2017/9/19.
 */

public class AoTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ao_test);
        findViewById(R.id.v).setPivotX(100000);
        findViewById(R.id.v).setPivotY(0.5f * getWindow().getDecorView().getHeight());

        int AO = 576;
        double S = 10 * Math.PI / 180;
        double ao = (AO * Math.cos(S) * 100000) / (AO + Math.sin(S) * 100000);
        System.out.println(ao);

        double XO = Math.round(1200 * AO / (AO * Math.cos(S) - 1200 * Math.sin(S)));
        System.out.println(XO);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        System.out.println(findViewById(R.id.v).getWidth() + " , ");

        Rect rect = new Rect();
        int[] location = new int[2];
        findViewById(R.id.v).getLocationInWindow(location);
        findViewById(R.id.v).getLocalVisibleRect(rect);
        System.out.println(rect.toString() + "  -  width = " + rect.width());
        System.out.println(location[0] + "," + location[1]);
    }
}
