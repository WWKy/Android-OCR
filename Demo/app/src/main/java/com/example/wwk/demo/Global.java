package com.example.wwk.demo;

import java.io.File;

/**
 * Created by WWK on 2017/4/10.
 */

public class Global {
    public static final int OCR_ONLY=1;
    public static final int START_CAMERA=2;
    public static final int START_GALLERY=3;
    //public static int ocr_flag=OCR_ONLY;

    //public static int imagePosition;
   // public static File file;

    public static String[] dirsName;
    public static File[] dirsFiles;
    public static File[] imageFiles;
    public static File dirSelected;
    public static boolean deleteIndex[];

    public static void getDirsList() {
        File file = new File(Path.ImageDir);
        dirsName = file.list();
        dirsFiles = file.listFiles();
    }

    public static void getImageList()
    {
        imageFiles=dirSelected.listFiles();
    }

}



