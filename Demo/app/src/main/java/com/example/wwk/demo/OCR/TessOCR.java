package com.example.wwk.demo.OCR;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.example.wwk.demo.Path;
import com.example.wwk.demo.image_manager.MainActivity;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.tesseract.android.TessBaseAPI.OEM_TESSERACT_ONLY;
import static com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel.RIL_PARA;
import static com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel.RIL_SYMBOL;
import static com.googlecode.tesseract.android.TessBaseAPI.PageSegMode.PSM_SINGLE_LINE;

/**
 * Created by WWK on 2017/4/3.
 */

public class TessOCR implements TessBaseAPI.ProgressNotifier {
    public static final int OCR_Result = 1;
    public static final int OCR_Progress = 2;
    private TessBaseAPI mTess;
    private Messenger messenger;
    private File file1;
    private Pix pix;
   // public static String string;
    public static String TAG = "TessOCR";
    public static String language = "eng";
    public TessOCR(Messenger messenger) {
        this.messenger = messenger;

    }



    public void startOCR(File file) {
        file1 = file;
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                mTess = new TessBaseAPI(TessOCR.this);
                if (mTess.init(Path.path, language,OEM_TESSERACT_ONLY)) {
                    Log.w(TAG, "Tess初始化成功");
                } else {
                    Log.w(TAG, "Tess初始化失败");
                }

                //////////////////////////司马二值化////////////////////////////////////////////////////////

//                pix=ReadFile.readFile(file1);
//                pix= Convert.convertTo8(pix);
//                // pix= Binarize.otsuAdaptiveThreshold(pix);
//                pix= GrayQuant.pixThresholdToBinary(pix,200);
//                mTess.setImage(pix);
//                mTess.setPageSegMode(PSM_SINGLE_LINE);
                mTess.setImage(file1);

                ////////////////////////////////司马二值化//////////////////////////////////////////////////
                //Log.e(TAG, "mTess.getHOCRText "+mTess.getHOCRText(1));
                String resultH = mTess.getHOCRText(0);
                String resultUTF = mTess.getUTF8Text();
                Log.e(TAG,resultUTF);
//                ResultIterator resultIterator = mTess.getResultIterator();
//                resultIterator.begin();
//                List<Pair<String,Double>> listTemp=resultIterator.getChoicesAndConfidence(RIL_PARA);
//                for (int i = 0; i < listTemp.size(); i++) {
//                    String out=listTemp.get(i).first+"|"+listTemp.get(i).second;
//                    Log.e(TAG, "备选："+out);
//                }
//


                SpannableStringBuilder spannableStringBuilder= new SpannableStringBuilder("");
               if(!TextUtils.isEmpty(resultUTF)){
                   List<Pair<String,Object>> characterList= getCharacterWithAlternative(RIL_PARA,RIL_SYMBOL,70);

                   for (int i = 0; i <characterList.size() ; i++) {
                      // Log.e(TAG,list.get(i).first+"|"+list.get(i).second);
                       final String string=characterList.get(i).first;
                       if (characterList.get(i).second!=null){
                           final List<Pair<String, Double>> alternativeList= (List<Pair<String, Double>>) characterList.get(i).second;
//                           ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
                           SpannableString spannableString=new SpannableString(string);
                           ClickableSpan clickableSpan=new ClickableSpan() {
                               @Override
                               public void onClick(View widget) {
//                                   Toast.makeText(widget.getContext(), string,Toast.LENGTH_SHORT).show();
//                                   logeAlternativeList(alternativeList);
                                   int stringsSize=alternativeList.size();
                                   String[] strings=new String[stringsSize];
                                   for (int j = 0; j <stringsSize ; j++) {
                                       strings[j]=alternativeList.get(j).first+" | "+alternativeList.get(j).second;
                                   }
                                   showAlternativeDialog(widget.getContext(),strings);
                                   //Log.e(TAG, "点击红字: *******************"+string);
                               }
                               @Override
                               public void updateDrawState(TextPaint ds) {
                                   super.updateDrawState(ds);
                                   ds.setColor(Color.RED);
                               }
                           };
                           spannableString.setSpan(clickableSpan,0,1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                           spannableStringBuilder.append(spannableString);

                       }else {
                           spannableStringBuilder.append(string);
                       }

                   }
               }

                Message message = new Message();
                message.what = OCR_Result;
                message.obj = spannableStringBuilder;
                message.arg1=mTess.meanConfidence();
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                Message message2 = new Message();
                message2.what = 10;
                message2.obj = resultH;
                try {
                    messenger.send(message2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private List<Pair<String,Object>> getCharacterWithAlternative(int level1, int level2, float confidenceThreshold){
        //level1一般为block、paragraph，level2为word、symbol
        //Object为可能选项列表。为空即正确率高于阈值
        List<String> blocks =new ArrayList<>();
        List<Pair<String,Object>> charWithAlternative=new ArrayList<>();
        //Boolaen记录该字符是否标红
        ResultIterator resultIterator = mTess.getResultIterator();
        resultIterator.begin();
        do {
                blocks.add(resultIterator.getUTF8Text(level1));
            } while (resultIterator.next(level1));

        resultIterator.begin();
        for (int i=0;i<blocks.size();i++)
        {
            String block=blocks.get(i);
            int blockLength=block.length();
            for(int j=0;j<blockLength;j++){
                char c=block.charAt(j);
                if(c=='\u0020'){
                   // Log.e(TAG, "有\\u0020");
                    charWithAlternative.add(new Pair<String, Object>(String.valueOf(c),null));
                }else if(c=='\n'){
                    Log.e(TAG, "有\\n");
                }
                else {
                    float confidence= resultIterator.confidence(level2);
                    if (confidence<confidenceThreshold)
                    {
                        Object object=resultIterator.getChoicesAndConfidence(RIL_SYMBOL);
                        charWithAlternative.add(new Pair<String, Object>(String.valueOf(c),object));
                    }else
                    {charWithAlternative.add(new Pair<String, Object>(String.valueOf(c),null));}

                    resultIterator.next(level2);
                }
            }
            charWithAlternative.add(new Pair<String, Object>("\n\n",null));

        }
        return charWithAlternative;
    }


    public void stop(){
        if (mTess != null)
            mTess.stop();
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }

    private void logeAlternativeList(List<Pair<String, Double>> list){
        for (int i = 0; i <list.size() ; i++) {
            Log.e(TAG, "logeAlternativeList:"+list.get(i).first+"||"+list.get(i).second);
        }
    }

    private void showAlternativeDialog(final Context context,final String[] items){

        //final String[] items = { "我是1","我是2","我是3","我是4" };

        AlertDialog.Builder listDialog = new AlertDialog.Builder(context);
        listDialog.setTitle("可能的识别结果：");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // which 下标从0开始
            // ...To-do
//            Toast.makeText(MainActivity.this,
//                                        "你点击了" + items[which],
//                                        Toast.LENGTH_SHORT).show();
        }
    });
    listDialog.show();
    }


    @Override
    public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
        Log.e(TAG, progressValues.getPercent() + "");
        Message message = new Message();
        message.what = OCR_Progress;
        message.arg1 = progressValues.getPercent();
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
