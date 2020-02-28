package com.seagazer.bannersample;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.seagazer.banner.BannerViewPager;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private BannerViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.banner);
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
        RadioGroup orientation = findViewById(R.id.orientation);
        RadioGroup order = findViewById(R.id.order);
        RadioGroup mode = findViewById(R.id.mode);
        orientation.setOnCheckedChangeListener(this);
        order.setOnCheckedChangeListener(this);
        mode.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.horizontal:
                viewPager.setOrientation(RecyclerView.HORIZONTAL);
                break;
            case R.id.vertical:
                viewPager.setOrientation(RecyclerView.VERTICAL);
                break;
            case R.id.sequence:
                viewPager.setReverseFlip(false);
                break;
            case R.id.reverse:
                viewPager.setReverseFlip(true);
                break;
            case R.id.auto:
                viewPager.setAutoFlip(true);
                break;
            case R.id.hand:
                viewPager.setAutoFlip(false);
                break;
        }
    }
}
