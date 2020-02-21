package com.seagazer.bannersample;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.banner.BannerViewPager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BannerViewPager viewPager = findViewById(R.id.banner);
        viewPager.setAdapter(new BannerAdapter());
        viewPager.setAutoFlip(true);
        viewPager.setAutoFlipTime(3500);
        viewPager.setOnPageClickListener(new BannerViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
                Toast.makeText(MainActivity.this, "position =" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
