package com.example.wwk.demo;

import android.app.Application;
import android.util.DisplayMetrics;

import com.example.wwk.demo.image_manager.ImageCellSize;

/**
 * Created by WWK on 2017/5/19.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        caculateImageCellSzieByScreen();
    }



    void caculateImageCellSzieByScreen(){
        //获取屏幕宽度，设置imagecell宽度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ImageCellSize.width = dm.widthPixels / 2;
        ImageCellSize.setHeigthByWidth();


    }
}
