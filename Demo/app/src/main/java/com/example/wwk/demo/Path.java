package com.example.wwk.demo;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by WWK on 2017/4/10.
 */

public class Path {
    private static String TAG="tag";
    public static String path = Environment.getExternalStorageDirectory() + "/tesseract/";
    public static String ImageDir = path+"image/";

    public static String creatDirs() {
        Log.w(TAG, "path" + Path.path);

        File dir1 = new File(Path.path + "tessdata/");
        if (!dir1.exists()) {
            Log.w(TAG, "tessdata目录不存在");
            if (dir1.mkdirs()) {
                Log.w(TAG, "tessdata目录创建成功");
            } else{
                Log.w(TAG, "tessdata目录创建失败");
                return null;}
        } else {
            Log.w(TAG, "tessdata目录存在");
        }

        File dir2 = new File(Path.ImageDir);
        if (!dir2.exists()) {
            Log.w(TAG, "image目录不存在");
            if (dir2.mkdirs()) {
                Log.w(TAG, "image目录创建成功");
            } else{
                Log.w(TAG, "image目录创建失败");
                return null;}
        } else {
            Log.w(TAG, "image目录存在");
        }
        return path;
    }
}
