package com.example.wwk.demo.OCR;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.example.wwk.demo.R;


public class HOCRActivity extends AppCompatActivity {


    private String text;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hocr);
        webView= (WebView) findViewById(R.id.web_view);
        Intent intent=getIntent();
        text=intent.getStringExtra("resultH");
        Log.e("HOCRActivity", "textH:"+text);
        webView.loadData(text,"text/html","utf-8");

    }

}



