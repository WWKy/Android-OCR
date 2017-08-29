package com.example.wwk.demo.image_manager;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wwk.demo.R;

import java.io.File;

/**
 * Created by WWK on 2017/4/10.
 */

class ImageGridAdpter extends RecyclerView.Adapter {

    public interface OnSubItemClickListener{
        void onDeleteCheckedChanged(int clickPosition, boolean isChecked);
    }

    private OnSubItemClickListener onSubItemClickListener;
    private boolean MuticSelect=false;
    private File[] imagesList;
    public ImageGridAdpter(File[] imagesList,OnSubItemClickListener onSubItemClickListener){
        this.imagesList=imagesList;
        this.onSubItemClickListener=onSubItemClickListener;
    }

    public void updateImagesList(File[] imagesList){
        this.imagesList=imagesList;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //设置每项的格式
      //设置成TextView
        private ImageView imageView;
        private FrameLayout frameLayout;
        private CheckBox checkBox;
        public ViewHolder(View root) {
            super(root);
            //Global.deleteIndex=null;
            checkBox= (CheckBox) root.findViewById(R.id.checkbox);
            imageView= (ImageView) root.findViewById(R.id.image_cell);
            frameLayout= (FrameLayout) root.findViewById(R.id.image_cell_frame);
            GridLayoutManager.LayoutParams params= (GridLayoutManager.LayoutParams) frameLayout.getLayoutParams();
            params.width=ImageCellSize.width;
            params.height=ImageCellSize.heigth;
            frameLayout.setLayoutParams(params);

        }
        public ImageView getImageView() {
            return imageView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }

    public void setMuticSelect(boolean muticSelect) {
        MuticSelect = muticSelect;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid_cell,parent,false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ViewHolder viewHolder=(ViewHolder) holder;
        CheckBox checkBox= viewHolder.getCheckBox();
        checkBox.setVisibility(View.INVISIBLE);
        checkBox.setChecked(false);

        if (MuticSelect){
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onSubItemClickListener.onDeleteCheckedChanged(holder.getAdapterPosition(),isChecked);
                    //Global.deleteIndex[holder.getAdapterPosition()]=isChecked;
                }
            });
        }


        Glide
                .with(holder.itemView.getContext())
                .load(imagesList[position])
                .centerCrop()
                .placeholder(R.color.blue_gray)
                .crossFade()
                .into(viewHolder.getImageView());
    }

    @Override
    public int getItemCount() {
        return imagesList.length;
    }




    }
