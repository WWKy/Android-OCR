package com.example.wwk.demo.OCR;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wwk.demo.Baidu_translate.TransApi;
import com.example.wwk.demo.FileIO;
import com.example.wwk.demo.Global;
import com.example.wwk.demo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class OCRActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static final int Trans_Result =3;
    public static final int TTS=1;
    private EditText editText;
    private TextView transTextView;
    private ImageView imageView;
    private int ocr_flag;
    private SpannableStringBuilder OCRresult;
    private String resultH;
    private String transResult;
    private int percent;
    private int meanConfidence;
    private int choice;
    private File image;
    private TextToSpeech tts;
    ProgressFragment progressFragment = new ProgressFragment();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == TessOCR.OCR_Result) {
                OCRresult = (SpannableStringBuilder) msg.obj;
                meanConfidence = msg.arg1;
                progressFragment.onStop();
                preTTS();
                showResultView();

            } else if (msg.what == TessOCR.OCR_Progress) {
                percent = msg.arg1;
                progressFragment.updateProgress(percent);

            } else if (msg.what == 10) {
                resultH = (String) msg.obj;
                //Log.e("OCRActivity", resultH);
            }else if (msg.what==Trans_Result){
                transResult= (String) msg.obj;
                transTextView.setText(transResult);
                Toast.makeText(OCRActivity.this,"翻译完成",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Messenger messenger = new Messenger(handler);
    private TessOCR tessOCR = new TessOCR(messenger);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr2);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, progressFragment)
                .commit();

        // textProgress= (TextView) findViewById(R.id.text_progress);


        Intent i = getIntent();
        ocr_flag = i.getIntExtra("ocr_flag", 0);
        image = (File) i.getSerializableExtra("Image");

        if (ocr_flag == Global.OCR_ONLY) {
            tessOCR.startOCR(image);
            //Glide.with(this).load(Global.imageFiles[imagePosition]).into(imageView);
        }
    }

    void showResultView() {
        setContentView(R.layout.activity_ocr);
        editText = (EditText) findViewById(R.id.text);
        transTextView= (TextView) findViewById(R.id.text_trans);
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OCRActivity.this, BigImageActivity.class);
                intent.putExtra("image", image);
                startActivity(intent);

            }
        });

        Glide.with(this)
                .load(image)
               .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);


        editText.setText(OCRresult);
        editText.setMovementMethod(LinkMovementMethod.getInstance());
        Toast.makeText(this, "识别成功！平均准确率为：" + String.valueOf(meanConfidence) + "%", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        tessOCR.stop();
        Toast.makeText(this,"OCR取消",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ocr_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.translate) {
            //Toast.makeText(this,"翻译",Toast.LENGTH_SHORT).show();
            showLanguageChoosingDialog();
            return true;
        }else if (id==R.id.tts){
           // Toast.makeText(this,"tts",Toast.LENGTH_SHORT).show();
            String content= String.valueOf(editText.getText());
            startTTS(content);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(this,this);
            }
            else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    private void startTTS(String content) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(content,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    private void preTTS(){
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TTS);
    }


    private void startBaiduTrans(final String content,final String lanFrom,final String lanTo){
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransApi api = new TransApi();
                String query =  content.replace("\n"," ");
                Log.e("OCR", "英文原文"+query);
                String JSresult=api.getTransResult(query, lanFrom, lanTo);
                try {
                    JSONObject jsObj=new JSONObject(JSresult);
                    JSONArray jsArray=jsObj.getJSONArray("trans_result");
                    JSONObject jsObj2=jsArray.getJSONObject(0);
                    String result=jsObj2.getString("dst");

                    Message message = new Message();
                    message.what=Trans_Result;
                    message.obj=result;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showLanguageChoosingDialog() {
        choice = 0;
        final String[] items = {"中文（简体）", "英语"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("要翻译成？");
        dialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choice = which;
            }
        });
        dialog.setPositiveButton("开始翻译",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string= String.valueOf(editText.getText());
                        if (choice == 0) {
                            startBaiduTrans(string,"auto","zh");
                        } else if (choice == 1) {
                            startBaiduTrans(string,"auto","en");
                        }
                    }
                });
        dialog.setNegativeButton("取消", null);
        // 创建实例并显示
        dialog.show();
    }

    @Override
    public void onInit(int status) {

    }
}







