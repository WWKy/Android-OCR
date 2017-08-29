package com.example.wwk.demo.OCR;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wwk.demo.R;
import com.example.wwk.demo.photoView.PhotoView;

import java.io.File;

public class BigImageActivity extends AppCompatActivity {
    private File image;
    private PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        Intent intent=getIntent();
        image= (File) intent.getSerializableExtra("image");

        photoView= (PhotoView) findViewById(R.id.photo_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        photoView.enable();
        photoView.disableRotate();

        Glide.with(this)
                .load(image)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(photoView);

    }
}
