package com.example.wwk.demo;

import java.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.sql.Time;

/**
 * Created by WWK on 2017/5/28.
 */

public class FileIO {
    static String TAG = "FileIO";

    public static File[] getImagesList(File dir) {
        return dir.listFiles(imagesSelector);
    }

    public static File[] getDirsList() {

        File file = new File(Path.ImageDir);
        return file.listFiles(dirsSelector);
    }

    public static String getCurrentTime() {
        Time time = new Time(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(time);

    }


    public boolean moveFile(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
    }


    private static FileFilter dirsSelector = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    private static FilenameFilter imagesSelector = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {

            Log.e(TAG, "name:" + name + "isImage:" + String.valueOf(isImage(name)));
            return isImage(name);
        }
    };


    private static boolean isImage(String name) {
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".bmp");

    }


}
