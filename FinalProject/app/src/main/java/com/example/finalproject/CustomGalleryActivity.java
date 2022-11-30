package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;

public class CustomGalleryActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>();
    File[] listFile;
    private String folderName = "MyPhotoDir";
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;
    LinearLayout galleryLayout;

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getFromSdcard();
        mViewPager = findViewById(R.id.viewPagerMain);
        mViewPagerAdapter = new ViewPagerAdapter(this, f);
        mViewPager.setAdapter(mViewPagerAdapter);

        galleryLayout = (LinearLayout) findViewById(R.id.galleryLayout);

//        getting the new pages for the viewpager adaptor to display new photo and colour palettes
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int origin = extras.getInt("EXTRA_PAGE");
            int position = 0;
            if (origin == 0) {
                position = mViewPager.getAdapter().getCount();
            }
            mViewPager.setCurrentItem(position);
            Log.e("position", String.valueOf(position));
        }
        updateDarkMode();
    }

//    get the photos from the sd card in the app directory
    public void getFromSdcard() {
        File file = new File(getExternalFilesDir(folderName), "/");
        if (file.isDirectory()) {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                f.add(listFile[i].getAbsolutePath());
            }
        }
    }

//    change the UI based on shared prefs
    public void updateDarkMode() {
        SharedPreferences sharedPrefs = getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        Boolean isDark = sharedPrefs.getBoolean("isDark", true);

        Log.e("sharedPref", isDark + "");

        if (isDark) {
            galleryLayout.setBackgroundColor(Color.rgb(32, 33, 33));

        } else {
            galleryLayout.setBackgroundColor(Color.rgb(255, 255, 255));

        }
    }
}
