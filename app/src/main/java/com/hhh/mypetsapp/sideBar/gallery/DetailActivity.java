package com.hhh.mypetsapp.sideBar.gallery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.hhh.mypetsapp.databinding.ActivityDetailGalleryBinding;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailGalleryBinding binding;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> list = bundle.getStringArrayList("imagesGallery");
        int pos = getIntent().getIntExtra("positionImage", 0);

        viewPager2 = binding.viewpager2;
        viewPager2.setCurrentItem(pos, false);
        ViewPager2AdapterGallery viewPager2AdapterGallery = new ViewPager2AdapterGallery(
                this, list);
        viewPager2.setAdapter(viewPager2AdapterGallery);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}