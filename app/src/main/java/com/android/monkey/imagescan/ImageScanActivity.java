package com.android.monkey.imagescan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Description:预览大图的Activity
 * Author: Monkey
 * Time: 2017/1/24 15:14
 */

public class ImageScanActivity extends AppCompatActivity {
    public final static String EXTRA_TAG = "TAG";
    public final static String EXTRA_IMAGE_LIST = "IMAGE_LIST";
    public final static String EXTRA_CURRENT_SELECT_POSITION = "CURRENT_SELECT_POSITION";
    private ArrayList<String> mImageList;
    private int mCurrentSelectPos;
    private ViewPager mViewPager;
    private String mTag;
    private ImageScanAdapter mImageScanAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagescan);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        if(intent!=null){
            mImageList = (ArrayList<String>)intent.getSerializableExtra(EXTRA_IMAGE_LIST);
            mCurrentSelectPos = intent.getIntExtra(EXTRA_CURRENT_SELECT_POSITION,0);
            mTag = intent.getStringExtra(EXTRA_TAG);
        }
    }

    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.vp_image_scan);
        mImageScanAdapter = new ImageScanAdapter(mTag,mImageList,this);
        mViewPager.setAdapter(mImageScanAdapter);
        mViewPager.setCurrentItem(mCurrentSelectPos);
    }
}
