package com.example.android24;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class SlideshowActivity extends AppCompatActivity {
    private final Handler slideHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        ArrayList<String> imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        ArrayList<Picture> pictures = new ArrayList<>();

        assert imagePaths != null;
        for (String path : imagePaths) {
            pictures.add(new Picture(path));
        }

        ViewPager2 viewPager = findViewById(R.id.viewPagerSlideshow);
        SlideshowAdapter adapter = new SlideshowAdapter(this, pictures);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        slideHandler.removeCallbacksAndMessages(null);
    }
}