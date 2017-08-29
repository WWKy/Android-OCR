package com.example.wwk.demo.OCR;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import static android.content.Intent.ACTION_PICK;

/**
 * Created by WWK on 2017/5/19.
 * <p>
 * StartCameraAndGalleryActivity
 */

public class CameraAndGalleryActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int PICK_PHOTO = 2;
    public static final int CROP_PHOTO = 3;
    private Uri outputUri;


    public void startGallery(File outputDir, String name) {
        creatOutputFile(outputDir, name);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent, PICK_PHOTO);
        Intent intent = new Intent(ACTION_PICK);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
//        if (Build.VERSION.SDK_INT < 19) {
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//        } else {
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//        }
        startActivityForResult(intent,PICK_PHOTO);
    }


    public void startCamera(File outputDir, String name) {
        creatOutputFile(outputDir, name);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序

    }

    private void startCrop3(Uri uriInput){
        CropImage
                .activity(uriInput)
                .setAllowRotation(true)
                .setOutputUri(outputUri)
                .start(this);
    }



    private void startCrop(Uri uriInput,Uri uriOutput) {
        //uri 裁剪图片来源地址
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uriInput, "image/*");//设置文件类型
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriOutput);//设置保存路径
        startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序

    }

    private void creatOutputFile(File outputDir, String name) {
        File outputImage = new File(outputDir, name);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputUri = Uri.fromFile(outputImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    startCrop3(outputUri);
                }
                break;

            case PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri=data.getData();
                    //Log.e("Camera,GalleryActivity","PICK URI"+uri.toString());
                    startCrop3(uri);
                }
                break;
        }
    }
}
