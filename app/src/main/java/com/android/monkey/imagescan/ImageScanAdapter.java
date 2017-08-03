package com.android.monkey.imagescan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Description:预览大图的Adapter
 * Author: Monkey
 * Time: 2017/1/24 15:38
 */

public class ImageScanAdapter extends PagerAdapter{
    private String mTag;
    private ArrayList<View> mViewList;
    private ArrayList<String> mImageList;
    private Context mContext;

    public ImageScanAdapter(String mTag, ArrayList<String> mImageList, Context mContext) {
        this.mTag = mTag;
        this.mImageList = mImageList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if(mImageList==null){
            return 0;
        }else{
            return mImageList.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        if(position>=0&&position<getCount()){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.adapter_image_scan, null);
            ImageView ivImageScan = (ImageView) view.findViewById(R.id.iv_image_scan);
            Bitmap bitmap = BitmapFactory.decodeFile(mImageList.get(position));
            if(bitmap!=null){
                ivImageScan.setImageBitmap(bitmap);
            }else{
                ivImageScan.setImageResource(R.mipmap.ic_launcher);
            }
            container.addView(view);//添加页卡
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);//删除页卡
    }
}
