package com.example.wwk.demo.image_manager;

/**
 * Created by WWK on 2017/4/13.
 */

public class ImageCellSize {
    public static int width=0;
    public static int heigth=0;
    public static void setHeigthByWidth(){
        heigth=width*(16/9);
    }

}
