package com.hhh.mypetsapp.sideBar.gallery;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.databinding.ActivityDetailGalleryBinding;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailGalleryBinding binding;
    private ViewPager2 viewPager2;private SharedPreferences defPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean key = defPref.getBoolean("theme", false);
        if (key){
            //dark
            setTheme(R.style.Theme_MyPetsApp_Dark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }
        super.onCreate(savedInstanceState);

        binding = ActivityDetailGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> list = bundle.getStringArrayList("imagesGallery");
        int pos = getIntent().getIntExtra("positionImage", 0);

        viewPager2 = binding.viewpager2;
        ViewPager2AdapterGallery viewPager2AdapterGallery = new ViewPager2AdapterGallery(
                this, list);
        viewPager2.setAdapter(viewPager2AdapterGallery);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        viewPager2.setCurrentItem(pos, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0f);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0f);
        }
    }
}