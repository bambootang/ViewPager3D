package com.bambootang.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSample(View view) {
        startActivity(new Intent(this, SampleActivity.class));
//        startActivity(new Intent(this, AoTestActivity.class));
    }

    public void onSample1(View view) {
        startActivity(new Intent(this, Sample1Activity.class));
    }

    public void onSample2(View view) {
        startActivity(new Intent(this, Sample2Activity.class));

    }

    public void onSample3(View view) {
        startActivity(new Intent(this, Sample3Activity.class));
    }
}
