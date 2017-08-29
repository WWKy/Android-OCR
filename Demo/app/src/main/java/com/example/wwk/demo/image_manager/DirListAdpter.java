package com.example.wwk.demo.image_manager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wwk.demo.FileIO;
import com.example.wwk.demo.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by WWK on 2017/4/10.
 */

class DirListAdpter extends RecyclerView.Adapter {

    public interface OnSubItemClickListener {
        void onDeleteClick(int position);
    }

    private OnSubItemClickListener onSubItemClickListener;
    private File[] dirsList;
    private boolean deleteEnable;


    public DirListAdpter(File[] dirsList, OnSubItemClickListener onSubItemClickListener){
        this.onSubItemClickListener=onSubItemClickListener;
        this.dirsList=dirsList;
    }

    public void updataDirsList(File[] dirsList){
        this.dirsList=dirsList;
    }


    class ViewHolder extends RecyclerView.ViewHolder{


        private TextView textView1;
        private TextView textView2;
        private ImageView imageView;
        private ImageButton deleteBtn;
        public ViewHolder(View root) {
            super(root);
            textView1= (TextView)root.findViewById(R.id.list_cell_text1);
            textView2= (TextView)root.findViewById(R.id.list_cell_text2);
            imageView= (ImageView) root.findViewById(R.id.list_cell_image);
            deleteBtn = (ImageButton) root.findViewById(R.id.delete_btn);
        }

        public TextView getTextView1() {
            return textView1;
        }

        public TextView getTextView2() {
            return textView2;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public ImageButton getDeleteBtn() {
            return deleteBtn;
        }

    }

    public void setDeleteEnable(boolean deleteEnable) {
        this.deleteEnable = deleteEnable;
    }


    //绘制cell
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell,parent,false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder vh=(ViewHolder)holder;
        vh.getDeleteBtn().setVisibility(INVISIBLE);

        vh.getTextView1().setText(dirsList[position].getName());


        Date date =new Date(dirsList[position].lastModified());
        SimpleDateFormat formatter= new SimpleDateFormat(" yyyy-MM-dd  HH:mm");
        String ctime = formatter.format(date);
        vh.getTextView2().setText(ctime);


        File dir= dirsList[position];
        File image[]= FileIO.getImagesList(dir);
        if(image!=null&&image.length!=0){
                Glide
                        .with(holder.itemView.getContext())
                        .load(image[0])
                        .centerCrop()
                        .placeholder(R.color.blue_gray)
                        .crossFade()
                        .into(vh.getImageView());}else {
            Glide
                .with(holder.itemView.getContext())
                .load(R.color.blue_gray)
                    .crossFade()
                    .into(vh.getImageView());}

        if(deleteEnable){
            vh.getDeleteBtn().setVisibility(VISIBLE);
            vh.getDeleteBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSubItemClickListener.onDeleteClick(position);


                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dirsList.length;
    }

}
