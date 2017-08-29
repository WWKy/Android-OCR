package com.example.wwk.demo.image_manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wwk.demo.Global;
import com.example.wwk.demo.OCR.OCRActivity;
import com.example.wwk.demo.OCR.TessOCR;
import com.example.wwk.demo.Path;
import com.example.wwk.demo.R;
import com.example.wwk.demo.photoView.PhotoView;
import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Edge;
import com.googlecode.leptonica.android.Enhance;
import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.MorphApp;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Scale;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.googlecode.leptonica.android.Binarize.otsuAdaptiveThreshold;
import static com.googlecode.leptonica.android.Edge.L_ALL_EDGES;
import static com.googlecode.tesseract.android.TessBaseAPI.OEM_TESSERACT_ONLY;

public class FullImageActivity extends AppCompatActivity {
    public static final int MIN_PIXEL_COUNT = 3 * 1024 * 1024;
    private boolean visible = false;
    private File image;
    private Bitmap bitmap;
    private Pix pix;
    //private File imageBrinary;
    private FloatingActionButton floatingActionButton;
    private PhotoView photoView;
    private static boolean LanguageChoices[] = {true, true,false};
    private boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Intent i = getIntent();
        image = (File) i.getSerializableExtra("Image");

        photoView = (PhotoView) findViewById(R.id.photo_view);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add2);

        hide();

        photoView.enable();
        photoView.disableRotate();
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visible) {
                    hide();
                } else {
                    show();
                }
            }
        });
        //Glide.with(this).load(Global.imageFiles[Global.imagePosition]).into(imageView);
        Glide.with(this).load(image).into(photoView);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Global.ocr_flag=Global.OCR_ONLY;
                showOCRLanguageDialog();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image_full, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.show_preprocess){
            pix= ReadFile.readFile(image);
            //pix= Enhance.unsharpMasking(pix);
            pix=Convert.convertTo8(pix);
           // pix= Binarize.otsuAdaptiveThreshold(pix);
            pix=GrayQuant.pixThresholdToBinary(pix,220);
            bitmap=WriteFile.writeBitmap(pix);
            photoView.setImageBitmap(bitmap);
            flag=true;


            return true;
        }

        if (id==R.id.show_preprocess2){
            TessBaseAPI mTess = new TessBaseAPI();
            mTess.init(Path.path,"eng");
            mTess.setImage(image);
            Pix pix=mTess.getThresholdedImage();

            Bitmap bitmap=WriteFile.writeBitmap(pix);
            photoView.setImageBitmap(bitmap);
            flag=false;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private File preprocessTess(File file){
        TessBaseAPI mTess = new TessBaseAPI();
        mTess.init(Path.path,"eng");
        mTess.setImage(image);
        Pix pix=mTess.getThresholdedImage();

        WriteFile.writeImpliedFormat(pix,file);
        return file;
    }


    private File preprocess(File file){
        pix= ReadFile.readFile(image);
        //pix= Enhance.unsharpMasking(pix);
        pix=enLarge(pix);

        pix=Convert.convertTo8(pix);

        //pix= Edge.pixSobelEdgeFilter(pix,L_ALL_EDGES);
        pix= AdaptiveMap.pixContrastNorm(pix);


        pix= Binarize.otsuAdaptiveThreshold(pix);
        //pix=Binarize.sauvolaBinarizeTiled(pix);
       // pix=GrayQuant.pixThresholdToBinary(pix,140);


        WriteFile.writeImpliedFormat(pix,file);
        return file;
    }

    private Pix enLarge(Pix pix){
        int pixCount=pix.getHeight()*pix.getWidth();
        if (pixCount < MIN_PIXEL_COUNT) {

            double scale = Math.sqrt(((double) MIN_PIXEL_COUNT) / pixCount);

            pix = Scale.scale(pix, (float) scale);
        }
        return pix;
    }


    private File creatOutputFile(File outputDir, String name) {
        File outputImage = new File(outputDir, name);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputImage;
    }

    private void startOCRActivity(boolean useLetopnica){
        if(useLetopnica){
            File file=creatOutputFile(Environment.getExternalStorageDirectory(),"BinTemp.jpg");
            file=preprocess(file);
            Intent intent = new Intent(FullImageActivity.this, OCRActivity.class);
            intent.putExtra("ocr_flag", Global.OCR_ONLY);
            intent.putExtra("Image", file);
            startActivity(intent);

        }else
        {
            File file=creatOutputFile(Environment.getExternalStorageDirectory(),"BinTempTess.jpg");
            file=preprocessTess(file);
            Intent intent = new Intent(FullImageActivity.this, OCRActivity.class);
            intent.putExtra("ocr_flag", Global.OCR_ONLY);
            intent.putExtra("Image", file);


        startActivity(intent);}
    }


    void hide() {
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
        photoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        floatingActionButton.hide();
        visible=false;
    }

    void show() {


        photoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        floatingActionButton.show();

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.show();
//        }
        visible=true;

    }


    private void showOCRLanguageDialog() {
        //Toast.makeText(MainActivity.this,"来点反应",Toast.LENGTH_SHORT);
        final String[] items = {"英文", "中文（简体）","使用Leptonica二值化"};
        final AlertDialog OCRLanguageDialog = new AlertDialog.Builder(FullImageActivity.this)
                .setTitle("选择要识别的语言")
                .setMultiChoiceItems(items, LanguageChoices, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        switch (which) {
                            case 0:
                                LanguageChoices[0] = isChecked;
                                break;
                            case 1:
                                LanguageChoices[1] = isChecked;
                                break;
                            case 2:
                                LanguageChoices[2]=isChecked;
                        }
                    }
                })
                .setPositiveButton("启动OCR", null)
                .setNegativeButton("取消",null)
                .create();
        OCRLanguageDialog.show();
        OCRLanguageDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LanguageChoices[0]) {
                    if (LanguageChoices[1]) {
                        //双选
                        TessOCR.language = "chi_sim+eng";
                        OCRLanguageDialog.dismiss();
                        startOCRActivity(LanguageChoices[2]);
                    } else {
                        //英语
                        TessOCR.language = "eng";
                        OCRLanguageDialog.dismiss();
                        startOCRActivity(LanguageChoices[2]);
                    }
                } else {
                    //中文
                    if (LanguageChoices[1]) {
                        TessOCR.language = "chi_sim+~eng";
                        OCRLanguageDialog.dismiss();
                        startOCRActivity(LanguageChoices[2]);
                    } else {
                        //都没有
                        Toast.makeText(FullImageActivity.this, "至少选择一种语言", Toast.LENGTH_SHORT).show();
                    }
                }


            }

        });

    }
}
