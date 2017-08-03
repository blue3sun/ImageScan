package com.android.monkey.imagescan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Description:图片适配器
 * Author: Monkey
 * Time: 2017/1/23 11:12
 */

public class ThumbnailImageAdapter extends BaseAdapter{
    private String mTag;
    private List<String> mImageLists;
    private Context mContext;

    public ThumbnailImageAdapter(String mTag, List<String> mImageLists, Context mContext) {
        this.mTag = mTag;
        this.mImageLists = mImageLists;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if(mImageLists ==null){
            return 0;
        }else{
            return mImageLists.size();
        }
    }

    @Override
    public String getItem(int position) {
        if(mImageLists ==null||position<0||position>= mImageLists.size()){
            return null;
        }else{
            return mImageLists.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.adapter_image,parent,false);
            holder = new ViewHolder();
            holder.mIvThumbnail = (ImageView)convertView.findViewById(R.id.iv_thumbnail_image);
            holder.mTvImgInfo = (TextView) convertView.findViewById(R.id.tv_img_info);
            convertView.setTag(holder);
        }else{
           holder = (ViewHolder)convertView.getTag();
        }
        String imgPath = getItem(position);
        if(!TextUtils.isEmpty(imgPath)){
            //转换成bitmap展示
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            if(bitmap!=null){
                holder.mIvThumbnail.setImageBitmap(bitmap);
                double sizeInMb = bitmap.getByteCount()/(1024.0*1024.0);
                Log.e(mTag, imgPath+"的大小:"+sizeInMb+"Mb");
                holder.mTvImgInfo.setText("缩略图的大小为："+sizeInMb+"Mb");
            }else{
                holder.mIvThumbnail.setImageResource(R.mipmap.ic_launcher);
                holder.mTvImgInfo.setText("缩略图对应的bitmap为空");
            }
        }else{
            holder.mIvThumbnail.setImageResource(R.mipmap.ic_launcher);
            holder.mTvImgInfo.setText("缩略图的路径为空");
        }
        return convertView;
    }

    public class ViewHolder{
        private ImageView mIvThumbnail;
        private TextView mTvImgInfo;
    }
}
