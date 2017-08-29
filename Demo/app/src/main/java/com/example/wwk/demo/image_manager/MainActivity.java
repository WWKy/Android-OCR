package com.example.wwk.demo.image_manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wwk.demo.FileIO;
import com.example.wwk.demo.Global;
import com.example.wwk.demo.OCR.CameraAndGalleryActivity;
import com.example.wwk.demo.OCR.OCRActivity;
import com.example.wwk.demo.Path;
import com.example.wwk.demo.R;
import com.example.wwk.demo.TTSActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;


public class MainActivity extends CameraAndGalleryActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AddOnClickListener addOnClickListener = new AddOnClickListener();
    private RecyclerView rv;
    private String TAG = "MainActivity";
    private DirListAdpter dirListAdpter;
    private ItemClickListener itemClickListener;
    private FloatingActionButton fabBack;
    private FloatingActionButton fabAdd;
    private File[] dirsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        //创建图片目录
        String path=Path.creatDirs();
        if (path!=null) {
            Toast.makeText(MainActivity.this, "DirsOK，请确认trainned data已经拷贝到以下目录："+path, Toast.LENGTH_LONG).show();
        }

        //获取文件夹列表
//        Global.updateDirsList();
        updateDirsList();

        //文件夹列表
        rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));//设置布局
        dirListAdpter=new DirListAdpter(dirsList, new DirListAdpter.OnSubItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                File dir=dirsList[position];
                if(dir.exists())
                {
                    if(dir.delete()){
                        updateDirsList();
                        dirListAdpter.updataDirsList(dirsList);
                        dirListAdpter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this,"文件夹不存在",Toast.LENGTH_SHORT).show();
                }
            }
        });
        rv.setAdapter(dirListAdpter);//填充内容

        itemClickListener = new ItemClickListener(this, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

               // Uri dirSelectedUri =Uri.fromFile(dirsList[position]);
                File dirSelected=dirsList[position];
//                Global.getImageList();

//                Global.deleteIndex = new boolean[Global.imageFiles.length];
//                for (int i = 0; i < Global.deleteIndex.length; i++) {
//                    Global.deleteIndex[i] = false;
//                }
                Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
                intent.putExtra("dirSelected",dirSelected);
                startActivity(intent);
                //Toast.makeText(MainActivity.this,"Click "+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

                fabAdd.hide();
                fabBack.show();
                rv.removeOnItemTouchListener(itemClickListener);
                dirListAdpter.setDeleteEnable(true);
                rv.swapAdapter(dirListAdpter, false);
            }
        });
        rv.addOnItemTouchListener(itemClickListener);


        fabBack = (FloatingActionButton) findViewById(R.id.fab_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.addOnItemTouchListener(itemClickListener);
                dirListAdpter.setDeleteEnable(false);
                rv.swapAdapter(dirListAdpter, false);
                fabBack.hide();
                fabAdd.show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //两个新建文件夹按钮
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(addOnClickListener);
        ImageButton imageBtn = (ImageButton) findViewById(R.id.add_btn);
        imageBtn.setOnClickListener(addOnClickListener);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //右上角的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        CropImage.ActivityResult result = CropImage.getActivityResult(data);
//        if (resultCode == RESULT_OK) {
//            Uri resultUri = result.getUri();
//        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
//                Uri resultUri = result.getUri();
//                File file=new File(resultUri.toString());
                File file=new File(Environment.getExternalStorageDirectory(),"tempimage.jpg");
                Intent intent = new Intent(this, OCRActivity.class);
                intent.putExtra("ocr_flag", Global.OCR_ONLY);
                intent.putExtra("Image",file);
                startActivity(intent);
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
           startCamera(Environment.getExternalStorageDirectory(),"tempimage.jpg");


        } else if (id == R.id.nav_gallery) {
            startGallery(Environment.getExternalStorageDirectory(),"tempimage.jpg");


        } else if (id == R.id.nav_slideshow) {

        ;

        } else if (id == R.id.nav_manage) {
            showPreprocessSettingDialog();


        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, TTSActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    private void showPreprocessSettingDialog() {
        //Toast.makeText(MainActivity.this,"来点反应",Toast.LENGTH_SHORT);
        final String[] items = {"黑底白字", "光照不均","低像素"};

        final AlertDialog PreprocessSettingDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("图片预处理设置")
                .setMultiChoiceItems(items,null,null)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消",null)
                .create();
        PreprocessSettingDialog.show();

    }

    public void updateDirsList(){
        dirsList =FileIO.getDirsList();
    }

    private class AddOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //dialog();
            final EditText editText = new EditText(MainActivity.this);
            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("新建文件夹")
                    .setMessage("请输入文件夹名")
                    .setIcon(android.R.drawable.ic_input_add)
                    .setView(editText)
                    .setPositiveButton("确定", null)
                    .setNegativeButton("取消", null)
                    .create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(editText.getText())) {
                        Toast.makeText(MainActivity.this, "文件夹名不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        File file = new File(Path.ImageDir + editText.getText() + File.separator);
                        if (!file.exists()) {
                            Log.w(TAG, "目录不存在");
                            if (file.mkdir()) {
                                Log.w(TAG, "目录创建成功");
                                //Global.updateDirsList();
                                dirsList=FileIO.getDirsList();
                                dirListAdpter.updataDirsList(dirsList);
                                dirListAdpter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, editText.getText() + "目录创建成功 ", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "目录创建失败");
                                Toast.makeText(MainActivity.this, editText.getText() + "目录创建失败 ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "目录存在");
                            Toast.makeText(MainActivity.this, editText.getText() + "目录已存在 ", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
//                        updateDirsList();
//                        dirListAdpter.updataDirsList(dirsList);
//                        dirListAdpter.notifyDataSetChanged();
                    }
                }
            });

        }
    }
}
