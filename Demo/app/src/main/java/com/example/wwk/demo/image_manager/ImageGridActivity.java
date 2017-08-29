package com.example.wwk.demo.image_manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.wwk.demo.FileIO;
import com.example.wwk.demo.OCR.CameraAndGalleryActivity;
import com.example.wwk.demo.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;


public class ImageGridActivity extends CameraAndGalleryActivity {

    private RecyclerView rv;
    private Uri imageUri;
    private ImageGridAdpter imageGridAdpter;
    private FloatingActionButton cameraBtn;
    private FloatingActionButton back;
    private MenuItem delete_image;
    private ItemClickListener itemClickListener;
    private File[] imagesList;
    private File dirSelect;
    private boolean[] toDelete;
    private int choice;

    //private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);


        Intent intent = getIntent();
        dirSelect = (File) intent.getSerializableExtra("dirSelected");
        updateImagesList();
        toDelete = new boolean[imagesList.length];


        rv = (RecyclerView) findViewById(R.id.image_grid);
        //设置布局，其中2表示两列布局
        rv.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        imageGridAdpter = new ImageGridAdpter(imagesList, new ImageGridAdpter.OnSubItemClickListener() {
            @Override
            public void onDeleteCheckedChanged(int clickPosition, boolean isChecked) {
                toDelete[clickPosition] = isChecked;
            }
        });
        rv.setAdapter(imageGridAdpter);//填充内容


        itemClickListener = new ItemClickListener(this, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Global.imagePosition=position;
                Intent intent = new Intent(ImageGridActivity.this, FullImageActivity.class);
                intent.putExtra("Image", imagesList[position]);
                startActivity(intent);
//                Toast.makeText(ImageGridActivity.this,"Click "+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                cameraBtn.hide();
                back.show();
                delete_image.setVisible(true);
                imageGridAdpter.setMuticSelect(true);
                rv.swapAdapter(imageGridAdpter, false);
                rv.removeOnItemTouchListener(itemClickListener);
                //Toast.makeText(ImageGridActivity.this,"Long Click ",Toast.LENGTH_SHORT).show();
            }
        });
        rv.addOnItemTouchListener(itemClickListener);

        back = (FloatingActionButton) findViewById(R.id.fab_back2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraBtn.show();
                back.hide();
                delete_image.setVisible(false);
                imageGridAdpter.setMuticSelect(false);
                rv.swapAdapter(imageGridAdpter, false);
                rv.addOnItemTouchListener(itemClickListener);
            }
        });
        cameraBtn = (FloatingActionButton) findViewById(R.id.fab_camera);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                File file = new File(Global.dirSelected, new Date().toString());
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                imageUri = Uri.fromFile(file);
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序
                showAddNewImageDialog();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CROP_PHOTO) {
//            if (resultCode == RESULT_OK) {
//                updateImagesList();
//                imageGridAdpter.updateImagesList(imagesList);
//                imageGridAdpter.notifyDataSetChanged();
//            }
//        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //Log.e("ImageGridActivity", "CROPURI:"+resultUri.toString());


                updateImagesList();
                imageGridAdpter.updateImagesList(imagesList);
                imageGridAdpter.notifyDataSetChanged();
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image_grid, menu);
        delete_image = menu.findItem(R.id.delete_image);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_image) {
            Toast.makeText(this, "给点反应", Toast.LENGTH_SHORT).show();
            if (toDelete == null) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                return false;
            }
            new AlertDialog.Builder(this)
                    //.setTitle("新建文件夹")
                    .setMessage("确认要删除这些图片？")
                    .setIcon(android.R.drawable.ic_delete)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file;
                            boolean flag = false;
                            for (int i = 0; i < toDelete.length; i++) {
                                if (toDelete[i]) {
                                    file = imagesList[i];
                                    if (file.exists()) {
                                        flag = file.delete();
                                    }
                                }
                            }
                            if (flag) {
                                Toast.makeText(ImageGridActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ImageGridActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }

                            //Global.deleteIndex=null;
                            cameraBtn.show();
                            back.hide();
                            delete_image.setVisible(false);
                            imageGridAdpter.setMuticSelect(false);
                            updateImagesList();
                            imageGridAdpter.updateImagesList(imagesList);
                            rv.swapAdapter(imageGridAdpter, false);
                            rv.addOnItemTouchListener(itemClickListener);
                            imageGridAdpter.notifyDataSetChanged();
                        }
                    })
                    .show();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddNewImageDialog() {
        choice = 0;
        final String[] items = {"摄像头", "系统相册"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("从哪里获取图片？");
        dialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choice = which;
            }
        });
        dialog.setPositiveButton("启动",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choice == 0) {
                            startCamera(dirSelect, FileIO.getCurrentTime() + ".jpg");
                        } else if (choice == 1) {
                            startGallery(dirSelect, FileIO.getCurrentTime() + ".jpg");
                        }
                    }
                });
        dialog.setNegativeButton("取消", null);
        // 创建实例并显示
        dialog.show();
    }


    private void updateImagesList() {
        imagesList = FileIO.getImagesList(dirSelect);
    }
}
